package io.coderazor.musicfiend;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import io.coderazor.musicfiend.app.AppController;
import io.coderazor.musicfiend.model.Track;
import io.coderazor.musicfiend.util.TrackFetcher;


public class TrackSearchFragment extends Fragment {
    private static final String LOG_NAME = "TrackViewerFragment";

    private RecyclerView mTrackRecyclerView;
    private List<Track> mTracks = new ArrayList<>();
    private ThumbnailDownloader<TrackHolder> mThumbnailDownloader;

    public static TrackSearchFragment newInstance() {
        return new TrackSearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

//        Handler responseHandler = new Handler();
//        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
//        mThumbnailDownloader.setThumbnailDownloadListener(
//            new ThumbnailDownloader.ThumbnailDownloadListener<TrackHolder>() {
//                @Override
//                public void onThumbnailDownloaded(TrackHolder trackHolder, Bitmap bitmap) {
//                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//                    trackHolder.bindDrawable(drawable);
//                }
//            }
//        );
//        mThumbnailDownloader.start();
//        mThumbnailDownloader.getLooper();
        Log.d(LOG_NAME, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track_viewer, container, false);
        mTrackRecyclerView = (RecyclerView)v.findViewById(R.id.fragment_track_viewer_recycler_view);
        mTrackRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
 //       mThumbnailDownloader.quit();
        Log.d(LOG_NAME, "Background thread destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_track_viewer, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        //searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        //searchItem.expandActionView();
        final SearchView searchView = (SearchView) searchItem.getActionView();
        //searchView.setIconifiedByDefault(false);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    Log.d(LOG_NAME, "QueryTextSubmit: " + s);
                    QueryPreferences.setStoredQuery(getActivity(), s);
                    updateItems();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    Log.d(LOG_NAME, "QueryTextChange: " + s);
                    return false;
                }
            });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchTracksTask(query).execute();
    }

    private void setupAdapter() {
        if (isAdded()) {
            mTrackRecyclerView.setAdapter(new TrackAdapter(mTracks));
        }
    }

    private class TrackHolder extends RecyclerView.ViewHolder {
        //private ImageView mItemImageView;
        public NetworkImageView mArtwork;
        public TextView mTitle;
        public TextView mArtist;
        public TextView mGenre;
        public TextView mDuration;
        public TextView mDescription;
        public ImageLoader mImageLoader = AppController.getInstance().getImageLoader();
        public ImageView mPlay;

        private Context mContext;

        public TrackHolder(View itemView) {
            super(itemView);

            mContext = itemView.getContext();

            if (mImageLoader == null)
                mImageLoader = AppController.getInstance().getImageLoader();
            mArtwork = (NetworkImageView) itemView.findViewById(R.id.track_artwork);
            mTitle = (TextView) itemView.findViewById(R.id.track_title);
            mArtist = (TextView) itemView.findViewById(R.id.track_artist);
            mGenre = (TextView) itemView.findViewById(R.id.genre);
            mDescription = (TextView) itemView.findViewById(R.id.track_description);
            mDuration = (TextView) itemView.findViewById(R.id.track_duration);
            mPlay = (ImageView) itemView.findViewById(R.id.play_arrow);

//            mItemImageView = (ImageView) itemView
//                    .findViewById(R.id.fragment_track_viewer_image_view);
        }

//        public void bindDrawable(Drawable drawable) {
//            mItemImageView.setImageDrawable(drawable);
//        }
    }

    private class TrackAdapter extends RecyclerView.Adapter<TrackHolder> {

        private String LOG_NAME = "Frag.TrackAdapter";

        private List<Track> mTracks;

        private Context mContext;


        public TrackAdapter(List<Track> tracks) {
            mTracks = tracks;
            mContext = getContext();
        }

        @Override
        public TrackHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_track_viewer, viewGroup, false);
            return new TrackHolder(view);
        }

        @Override
        public void onBindViewHolder(TrackHolder trackHolder, int position) {
            Track track = mTracks.get(position);
            Log.d(LOG_NAME,"onBindViewHolder for: "+track.getTitle());

            // thumbnail image
            trackHolder.mArtwork.setImageUrl(track.getArtworkURL(), trackHolder.mImageLoader);

            // title
            trackHolder.mTitle.setText(track.getTitle());

            // artist
            trackHolder.mArtist.setText("Artist: " + String.valueOf(track.getArtist()));

            trackHolder.mDuration.setText(String.valueOf(track.getDuration()));

            // genre
//            String genreStr = "";
//            for (String str : track.getGenre()) {
//                genreStr += str + ", ";
//            }
//            genreStr = genreStr.length() > 0 ? genreStr.substring(0,
//                    genreStr.length() - 2) : genreStr;
//            trackHolder.mGenre.setText(genreStr);

            // description
            trackHolder.mDescription.setText(String.valueOf(track.getDescription()));

            trackHolder.mPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_NAME,"onClick");
                    //Toast.makeText(mContext, "Wire me up and playme...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mTracks.size();
        }
    }

    private class FetchTracksTask extends AsyncTask<Void,Void,List<Track>> {

        private String mQuery;

        public FetchTracksTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<Track> doInBackground(Void... params) {

            if (mQuery == null) {
                return new TrackFetcher().fetchRecentTracks();
            } else {
                return new TrackFetcher().searchTracks(mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            mTracks = tracks;
            setupAdapter();
        }

    }



}
