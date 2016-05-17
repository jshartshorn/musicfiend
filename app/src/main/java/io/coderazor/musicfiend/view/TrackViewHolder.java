package io.coderazor.musicfiend.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import io.coderazor.musicfiend.R;
import io.coderazor.musicfiend.app.AppController;
import io.coderazor.musicfiend.model.Track;

/**
 * Created by joelhartshorn on 4/23/16.
 */
public class TrackViewHolder extends ChildViewHolder {

    private static  final String LOG_NAME = "TrackViewHolder";

    //items for tracks
    public NetworkImageView mArtwork;
    public TextView mTitle;
    public TextView mArtist;
    public TextView mGenre;
    public TextView mDescription;
    public ImageLoader mImageLoader = AppController.getInstance().getImageLoader();
    public ImageView mPlay, mAddTrack;


    private Context mContext;


    public TrackViewHolder(View itemView) {
        super(itemView);

        mContext = itemView.getContext();

        if (mImageLoader == null)
            mImageLoader = AppController.getInstance().getImageLoader();
        mArtwork = (NetworkImageView) itemView.findViewById(R.id.track_artwork);
        mTitle = (TextView) itemView.findViewById(R.id.track_title);
        mArtist = (TextView) itemView.findViewById(R.id.track_artist);
        mGenre = (TextView) itemView.findViewById(R.id.genre);
        mDescription = (TextView) itemView.findViewById(R.id.track_description);
        mPlay = (ImageView) itemView.findViewById(R.id.play_arrow);
        mAddTrack = (ImageView) itemView.findViewById(R.id.add_track_check);

    }

    public void bind(Track track) {
        //Toast.makeText(mContext, "Child bind for: "+track.getTitle(), Toast.LENGTH_SHORT).show();
        Log.d("TrackViewHolder","Child bind for: "+track.getTitle());

        // thumbnail image
        mArtwork.setImageUrl(track.getArtworkURL(), mImageLoader);

        // title
        mTitle.setText(track.getTitle());

        // artist
        mArtist.setText("Artist: " + String.valueOf(track.getArtist()));

        // genre
        String genreStr = "";
        for (String str : track.getGenre()) {
            genreStr += str + ", ";
        }
        genreStr = genreStr.length() > 0 ? genreStr.substring(0,
                genreStr.length() - 2) : genreStr;
        mGenre.setText(genreStr);

        // description
        mDescription.setText(String.valueOf(track.getDescription()));

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_NAME,"onClick Play Track");
                Toast.makeText(mContext, "Wire me up and playme...", Toast.LENGTH_SHORT).show();
            }
        });

//        mAddTrack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(LOG_NAME,"onClick Add Track");
//                Toast.makeText(mContext, "Wire me up and add me to playlist...", Toast.LENGTH_SHORT).show();
//            }
//        });

    }


}
