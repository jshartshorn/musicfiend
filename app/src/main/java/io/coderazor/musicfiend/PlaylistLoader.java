package io.coderazor.musicfiend;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import io.coderazor.musicfiend.app.AppConstant;
import io.coderazor.musicfiend.app.BaseActivity;
import io.coderazor.musicfiend.model.Playlist;

public class PlaylistLoader extends AsyncTaskLoader<List<Playlist>> {

    private List<Playlist> mPlaylists;
    private ContentResolver mContentResolver;
    private Cursor mCursor;
    private int mType;

    public PlaylistLoader(Context context, ContentResolver contentResolver, int type) {
        super(context);
        mContentResolver = contentResolver;
        mType = type;
    }

    @Override
    public List<Playlist> loadInBackground() {
        List<Playlist> entries = new ArrayList<>();
        String[] projection = {
                BaseColumns._ID,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_TITLE,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_DESCRIPTION,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_TYPE,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_DATE,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_TIME,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_IMAGE,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_IMAGE_STORAGE_SELECTION,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_JSON};

        Uri uri = PlaylistContract.URI_TABLE;
        mCursor = mContentResolver.query(uri,projection, null, null, BaseColumns._ID + " DESC");
        if(mCursor != null) {
            if(mCursor.moveToFirst()) {
                do {
                    String date = mCursor.getString(mCursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_DATE));
                    String time = mCursor.getString(mCursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_TIME));
                    String type = mCursor.getString(mCursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_TYPE));
                    String title = mCursor.getString(mCursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_TITLE));
                    String description = mCursor.getString(mCursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_DESCRIPTION));
                    String imagePath = mCursor.getString(mCursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_IMAGE));
                    String json = mCursor.getString(mCursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_JSON));
                    int imageSelection = mCursor.getInt(mCursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_IMAGE_STORAGE_SELECTION));
                    int _id = mCursor.getInt(mCursor.getColumnIndex(BaseColumns._ID));

                    if(mType == BaseActivity.PLAYLISTS) {
                        if(time.equals(AppConstant.NO_TIME)) {
                            time = "";
                            Playlist playlist = new Playlist(title, description, date, time, _id, imageSelection, type, json);
                            if(!imagePath.equals(AppConstant.NO_IMAGE)) {
                                if(imageSelection == AppConstant.DEVICE_SELECTION) {
                                    playlist.setBitmap(imagePath);
                                } else {
                                    // Is a Google Drive or Dropbox image
                                    playlist.setImagePath(imagePath);
                                }
                            } else {
                                playlist.setImagePath(AppConstant.NO_IMAGE);
                            }

                            entries.add(playlist);
                        }

                    } else {
                        throw new IllegalArgumentException("Invalid type = " + mType);
                    }
                } while(mCursor.moveToNext());
            }
        }

        return entries;
    }

    @Override
    public void deliverResult(List<Playlist> playlists) {
        if (isReset()) {
            if(playlists != null) {
                releaseResources();
                return;
            }
        }
        List<Playlist> oldPlaylists = mPlaylists;
        mPlaylists = playlists;
        if(isStarted()) {
            super.deliverResult(playlists);
        }
        if(oldPlaylists != null && oldPlaylists != playlists) {
            releaseResources();
        }
    }

    @Override
    protected void onStartLoading() {
        if(mPlaylists != null) {
            deliverResult(mPlaylists);
        }
        if (takeContentChanged()) {
            forceLoad();
        } else if(mPlaylists == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if(mPlaylists != null) {
            releaseResources();
            mPlaylists = null;
        }
    }

    @Override
    public void onCanceled(List<Playlist> playlists) {
        super.onCanceled(playlists);
        releaseResources();
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }

    private void releaseResources() {
        mCursor.close();
    }
}


