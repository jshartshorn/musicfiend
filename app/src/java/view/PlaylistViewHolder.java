package io.coderazor.musicfiend.view;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import io.coderazor.musicfiend.model.Playlist;
import io.coderazor.musicfienddev.DataProvider;
import io.coderazor.musicfienddev.R;

/**
 * Created by joelhartshorn on 4/23/16.
 */
public class PlaylistViewHolder extends ParentViewHolder {

    private static  final String LOG_NAME = "PlayListViewHolder";
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;
    private static final float PIVOT_VALUE = 0.5f;
    private static final long DEFAULT_ROTATE_DURATION_MS = 200;
    private static final boolean HONEYCOMB_AND_ABOVE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    public ImageView mArrowExpandImageView;

    //items for playlist
    public TextView mTitle;
    public TextView mDescription;

    private ImageView mSearch;
    private ImageView mShare;

    private Context mContext;

    public PlaylistViewHolder(View itemView) {
        super(itemView);
        Log.d(LOG_NAME,"constructor");

        mTitle = (TextView) itemView.findViewById(R.id.playlist_title);
        mDescription = (TextView) itemView.findViewById(R.id.playlist_description);
        mSearch = (ImageView) itemView.findViewById(R.id.search_playlist);
        mShare = (ImageView) itemView.findViewById(R.id.share_playlist);


        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.list_item_parent_horizontal_arrow_imageView);
        mArrowExpandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded()) {
                    collapseView();
                } else {
                    expandView();
                }
            }
        });

        mContext = itemView.getContext();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "This is where we should add the dialog for the note...", Toast.LENGTH_SHORT).show();
                //essentially
                //....show note dialog
                //....save note
                //....call notify
            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Search click", Toast.LENGTH_SHORT).show();
                //decide on method and choose
                //savePlaylist(playlists.get(0));
            }
        });

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Share click", Toast.LENGTH_SHORT).show();
                //decide on method and choose
                //savePlaylist(playlists.get(0));
            }
        });
    }

    @Override
    public void setMainItemClickToExpand() {
        super.setMainItemClickToExpand();
    }

    @Override
    public boolean isExpanded() {
        return super.isExpanded();
    }

    @Override
    public ParentListItemExpandCollapseListener getParentListItemExpandCollapseListener() {
        return super.getParentListItemExpandCollapseListener();
    }

    @Override
    public void setParentListItemExpandCollapseListener(ParentListItemExpandCollapseListener parentListItemExpandCollapseListener) {
        super.setParentListItemExpandCollapseListener(parentListItemExpandCollapseListener);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected void expandView() {
        super.expandView();
    }

    @Override
    protected void collapseView() {
        super.collapseView();
    }

    public void bind(Playlist playlist) {
        //Toast.makeText(mContext, "Parent bind for: "+playlist.getTitle(), Toast.LENGTH_SHORT).show();
        Log.d("PlayListViewHolder","onbind for: "+ playlist.getTitle());
        mDescription.setText(playlist.getDescription());
        mTitle.setText(playlist.getTitle());
    }



    @SuppressLint("NewApi")
    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (!HONEYCOMB_AND_ABOVE) {
            return;
        }

        if (expanded) {
            mArrowExpandImageView.setRotation(ROTATED_POSITION);
        } else {
            mArrowExpandImageView.setRotation(INITIAL_POSITION);
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        if (!HONEYCOMB_AND_ABOVE) {
            return;
        }

        RotateAnimation rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                INITIAL_POSITION,
                RotateAnimation.RELATIVE_TO_SELF, PIVOT_VALUE,
                RotateAnimation.RELATIVE_TO_SELF, PIVOT_VALUE);
        rotateAnimation.setDuration(DEFAULT_ROTATE_DURATION_MS);
        rotateAnimation.setFillAfter(true);
        mArrowExpandImageView.startAnimation(rotateAnimation);
    }

    @Override
    public boolean shouldItemViewClickToggleExpansion() {
        return false;
    }

}
