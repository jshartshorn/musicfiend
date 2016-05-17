package io.coderazor.musicfiend;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatDialogFragment;
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

import java.util.ArrayList;
import java.util.List;

import io.coderazor.musicfiend.model.Track;
import io.coderazor.musicfiend.util.TrackFetcher;


public class TrackSearchDialogFragment extends AppCompatDialogFragment {
    private static final String TAG = "TrackViewerFragment";

    private RecyclerView mTrackRecyclerView;
    private List<Track> mTracks = new ArrayList<>();
    private ThumbnailDownloader<TrackHolder> mThumbnailDownloader;

    public static TrackSearchDialogFragment newInstance() {
        return new TrackSearchDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
            new ThumbnailDownloader.ThumbnailDownloadListener<TrackHolder>() {
                @Override
                public void onThumbnailDownloaded(TrackHolder trackHolder, Bitmap bitmap) {
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    trackHolder.bindDrawable(drawable);
                }
            }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track_viewer, container, false);
        mTrackRecyclerView = (RecyclerView) v
                 .findViewById(R.id.fragment_track_viewer_recycler_view);
        //mTrackRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), 3));
        mTrackRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_track_viewer, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    Log.d(TAG, "QueryTextSubmit: " + s);
                    QueryPreferences.setStoredQuery(getActivity(), s);
                    updateItems();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    Log.d(TAG, "QueryTextChange: " + s);
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
        private ImageView mItemImageView;

        public TrackHolder(View itemView) {
            super(itemView);

            mItemImageView = (ImageView) itemView
                    .findViewById(R.id.fragment_track_viewer_image_view);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }

    private class TrackAdapter extends RecyclerView.Adapter<TrackHolder> {

        private List<Track> mTracks;

        public TrackAdapter(List<Track> tracks) {
            mTracks = tracks;
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
            Drawable placeholder = getResources().getDrawable(R.drawable.no_image);
            trackHolder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(trackHolder, track.getArtworkURL());
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // Get the layout inflater
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.list_track_viewer, null))
//                // Add action buttons
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        // sign in the user ...
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        TrackSearchDialogFragment.this.getDialog().cancel();
//                    }
//                });
//        return builder.create();
        return super.onCreateDialog(savedInstanceState);

    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
    }
}
