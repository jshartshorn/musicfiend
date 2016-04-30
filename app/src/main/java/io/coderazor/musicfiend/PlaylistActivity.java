package io.coderazor.musicfiend;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import io.coderazor.musicfiend.adapter.ExpandableRecyclerAdapter;
import io.coderazor.musicfiend.adapter.PlaylistAdapter;
import io.coderazor.musicfiend.app.AppConstant;
import io.coderazor.musicfiend.app.AppController;
import io.coderazor.musicfiend.app.AppSharedPreferences;
import io.coderazor.musicfiend.app.BaseActivity;
import io.coderazor.musicfiend.data.DataProvider;
import io.coderazor.musicfiend.dropbox.DropBoxActions;
import io.coderazor.musicfiend.model.Playlist;
import io.coderazor.musicfiend.model.Track;

//import android.app.FragmentManager;

public class PlaylistActivity extends BaseActivity implements ExpandableRecyclerAdapter.ExpandCollapseListener,
        PlaylistAdapter.PlaylistSearchClickCallback, PlaylistAdapter.PlayTrackClickCallback,
        LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    // Log tag
    private static final String LOG_NAME = PlaylistActivity.class.getSimpleName();



    //various globals we need
    private ArrayList<Playlist> mPlaylists = new ArrayList<Playlist>();
    private RecyclerView mRecyclerView;
    private PlaylistAdapter mPlaylistAdapter;
    private ContentResolver mContentResolver;
    private static Boolean mIsInAuth;
    public static Bitmap mSendingImage = null;
    private boolean mIsImageNotFound = false;
    private DropboxAPI<AndroidAuthSession> mDropboxAPI;
    private Bundle mBundle;
    private DropboxAPI<AndroidAuthSession> mApi;

    // playlist json url
    // todo this will eventually load from db...
    private static final String url = AppConstant.LOCAL_DEV_JSON_URL;
    private ProgressDialog pDialog;
    //private ArrayList<Playlist> playlists = new ArrayList<Playlist>();
    //private RecyclerView listView;
    //private PlaylistAdapter adapter;
    private ImageView mStorageSelection;
    private Toolbar mToolbar;
    //private Menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_expand_layout);

        this.mBundle = savedInstanceState;

        //stuff we need from the old playlist
        mToolbar = activateToolbar();
        setUpForDropbox();
        setUpNavigationDrawer();
        setUpActions();

        //setup the actual adapter and recyclerview
        initPlaylist();

        //call the background loader to init the data
        initData();


    }

    private void initPlaylist(){
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mPlaylistAdapter = new PlaylistAdapter(getApplicationContext(),mPlaylists, this, this);
        mRecyclerView.setAdapter(mPlaylistAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                edit(view);
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                PopupMenu popupMenu = new PopupMenu(PlaylistActivity.this, view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.action_playlists, popupMenu.getMenu());
                popupMenu.show();
                final View v = view;
                final int pos = position;
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_delete) {
                            //moveToTrash();
                            delete(v, position);
                            showToast("action delete");
                        } else if(item.getItemId() == R.id.action_archive) {
                            //moveToArchive(v, pos);
                            showToast("action archive");
                        } else if(item.getItemId() == R.id.action_edit) {
                            //edit(v);
                            showToast("action edit");
                        } else if(item.getItemId() == R.id.action_add) {
                            //edit(v);
                            add(v, position);
                            showToast("action add");
                        }

                        return false;
                    }
                });
            }
        }));

        //setup the recyclerview
        mPlaylistAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onListItemExpanded(int position) {
                Playlist expandedPlaylist = mPlaylists.get(position);

            }

            @Override
            public void onListItemCollapsed(int position) {
                Playlist collapsedPlaylist = mPlaylists.get(position);

            }
        });

        //addTestData(true);

    }

    private void addTestData(final Boolean addAll){
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest playlistReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(LOG_NAME, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Playlist playlist = new Playlist();
                                playlist.setId(Integer.parseInt(obj.getString("id")));
                                playlist.setTime(AppConstant.NO_TIME);
                                playlist.setDate(AppConstant.NO_TIME);
                                playlist.setImagePath(AppConstant.NO_IMAGE);
                                playlist.setTitle(obj.getString("title"));
                                playlist.setDescription(obj.getString("description"));
                                playlist.setJson(obj.toString());

                                Gson gson = new Gson();

                                ArrayList<Track> tracks = gson.fromJson(obj.getString("tracks"), new TypeToken<ArrayList<Track>>() {}.getType());

                                playlist.setTracks(tracks);

                                mPlaylists.add(playlist);
                                mPlaylistAdapter.notifyParentItemInserted(i);
                                mPlaylistAdapter.notifyChildItemRangeInserted(i,1,tracks.size());

                                //just add one item for testing if addAll is false
                                if(!addAll){
                                    break;
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_NAME, "Error: " + error.getMessage());
                hidePDialog();

            }
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(playlistReq);

        Log.d(LOG_NAME, "End on create...");
    }



    private void setUpForDropbox() {
        AndroidAuthSession session = DropBoxActions.buildSession(getApplicationContext());
        mDropboxAPI = new DropboxAPI<AndroidAuthSession>(session);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
//            case R.id.add_playlist:
//
//                AddPlaylistDialog dialog = new AddPlaylistDialog();
//                dialog.show(getSupportFragmentManager(), "AddPlaylistDialog");
//                return true;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData(){
        int LOADER_ID = 1;
        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
    }


    private Drawable getImageFromDropbox(DropboxAPI<?> mApi, String mPath, String filename) {
        FileOutputStream fos;
        Drawable drawable;
        String cachePath = getApplicationContext().getCacheDir().getAbsolutePath() + "/" + filename;
        File cacheFile = new File(cachePath);
        if(cacheFile.exists()) {
            mIsImageNotFound = false;
            return Drawable.createFromPath(cachePath);
        } else {
            try {
                DropboxAPI.Entry dirEnt = mApi.metadata(mPath, 1000, null, true, null);
                if(!dirEnt.isDir || dirEnt.contents == null) {
                    mIsImageNotFound = true;
                }
                ArrayList<DropboxAPI.Entry> thumbs = new ArrayList<DropboxAPI.Entry>();
                for (DropboxAPI.Entry ent : dirEnt.contents) {
                    if(ent.thumbExists) {
                        if(ent.fileName().startsWith(filename)) {
                            thumbs.add(ent);
                        }
                    }
                }
                if(thumbs.size() == 0) {
                    mIsImageNotFound = true;
                } else {
                    DropboxAPI.Entry ent = thumbs.get(0);
                    String path = ent.path;
                    try {
                        fos = new FileOutputStream(cachePath);

                    } catch (FileNotFoundException e) {
                        return getResources().getDrawable(R.drawable.ic_image_deleted);
                    }
                    mApi.getThumbnail(path, fos, DropboxAPI.ThumbSize.BESTFIT_960x640,
                            DropboxAPI.ThumbFormat.JPEG, null);
                    drawable = Drawable.createFromPath(cachePath);
                    mIsImageNotFound = false;
                    return drawable;
                }
            } catch(DropboxException e) {
                e.printStackTrace();
                mIsImageNotFound = true;
            }

            drawable = getResources().getDrawable(R.drawable.ic_loading);
            return drawable;
        }
    }

    private void changeNoItemTag() {
        TextView noItemTextView = (TextView) findViewById(R.id.no_item_textview);
        if(mPlaylistAdapter.getItemCount() !=0) {
            noItemTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noItemTextView.setText(AppConstant.EMPTY);
            noItemTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(!mIsInAuth) {
            if(connectionResult.hasResolution()) {
                try {
                    mIsInAuth = true;
                    connectionResult.startResolutionForResult(this, AppConstant.REQ_AUTH);
                } catch(IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    finish();
                }
            } else {
                finish();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }


    private boolean checkUserAccount() {
        String email = GDUT.AM.getActiveEmil();
        Account account = GDUT.AM.getPrimaryAccnt(true);
        if(email == null) {
            if(account == null) {
                account = GDUT.AM.getPrimaryAccnt(false);
                Intent accountIntent = AccountPicker.newChooseAccountIntent(account, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true,
                        null, null, null, null);
                startActivityForResult(accountIntent, AppConstant.REQ_ACCPICK);
                return false;
            } else {
                GDUT.AM.setEmil(account.name);
            }
            return true;
        }
        account = GDUT.AM.getActiveAccnt();
        if(account == null) {
            Intent accountIntent = AccountPicker.newChooseAccountIntent(account, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true,
                    null, null, null, null);
            startActivityForResult(accountIntent, AppConstant.REQ_ACCPICK);
            return false;
        }

        return true;

    }

    private boolean checkPlayServices() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS) {
            if(GooglePlayServicesUtil.isUserRecoverableError(status)) {
                errorDialog(status, AppConstant.REQ_RECOVER);
            } else {
                finish();
            }
            return false;
        }

        return true;
    }

    private void errorDialog(int errorCode, int requestCode) {
        Bundle args = new Bundle();
        args.putInt(AppConstant.DIALOG_ERROR, errorCode);
        args.putInt(AppConstant.REQUEST_CODE, requestCode);
        com.google.android.gms.common.ErrorDialogFragment dialogFragment = new com.google.android.gms.common.ErrorDialogFragment();
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), AppConstant.DIALOG_ERROR);
    }

    @Override
    public void onListItemExpanded(int position) {

    }

    @Override
    public void onListItemCollapsed(int position) {

    }

    private void updateStorageSelection(Bitmap bitmap, int storageSelectionResource, int selection) {
//        if (bitmap != null) {
//            mNoteImage.setImageBitmap(bitmap);
//        }
        //mStorageSelection.setBackgroundResource(storageSelectionResource);
        AppSharedPreferences.setPersonalPlaylistsPreference(getApplicationContext(), selection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlists, menu);

//        MenuItem myMenuItem = (MenuItem) mToolbar.findViewById(R.id.select_playlist_storage);
//
//        Object st  = menu.findItem(R.id.select_playlist_storage);
//        //mStorageSelection = menu.findItem(R.id.select_playlist_storage);
//        //mStorageSelection = (ImageView)mToolbar.findViewById(R.id.select_playlist_storage);
//
//        mStorageSelection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu = new PopupMenu(PlaylistActivity.this, v);
//                MenuInflater inflater = popupMenu.getMenuInflater();
//                inflater.inflate(R.menu.actions_image_selection, popupMenu.getMenu());
//                popupMenu.show();
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        if (menuItem.getItemId() == R.id.action_device) {
//                            updateStorageSelection(null, R.drawable.ic_local, AppConstant.DEVICE_SELECTION);
//                        } else if (menuItem.getItemId() == R.id.action_google_drive) {
//                            if (!AppSharedPreferences.isGoogleDriveAuthenticated(getApplicationContext())) {
//                                startActivity(new Intent(PlaylistActivity.this, GoogleDriveSelectionActivity.class));
//                                finish();
//                            } else {
//                                updateStorageSelection(null, R.drawable.ic_google_drive, AppConstant.GOOGLE_DRIVE_SELECTION);
//                            }
//                        } else if (menuItem.getItemId() == R.id.action_dropbox) {
//                            AppSharedPreferences.setPersonalPlaylistsPreference(getApplicationContext(), AppConstant.DROP_BOX_SELECTION);
//                            if (!AppSharedPreferences.isDropBoxAuthenticated(getApplicationContext())) {
//                                startActivity(new Intent(PlaylistActivity.this, DropBoxPickerActivity.class));
//                                finish();
//                            } else {
//                                updateStorageSelection(null, R.drawable.ic_dropbox, AppConstant.DROP_BOX_SELECTION);
//                            }
//                        }
//
//                        if (mBundle != null) {
//                            //mCameraFileName = mBundle.getString("mCameraFileName");
//                        }
//                        AndroidAuthSession session = DropBoxActions.buildSession(getApplicationContext());
//                        mApi = new DropboxAPI<AndroidAuthSession>(session);
//
//                        return false;
//                    }
//                });
//            }
//        });

        return true;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void playTrack(Track track) {
        Log.d(LOG_NAME, "playTrack");
        Toast.makeText(PlaylistActivity.this,
        "action settings...",
        Toast.LENGTH_SHORT)
        .show();
    }

    @Override
    public void searchPlaylist(int position, ArrayList<Track> tracks) {
        Log.d(LOG_NAME, "searchPlaylist");
        Toast.makeText(PlaylistActivity.this,
                "searchPlaylist",
                Toast.LENGTH_SHORT)
                .show();

        //intent with fragment
//        TrackSearchDialogFragment dialog = new TrackSearchDialogFragment();
//        dialog.setMenuVisibility(true);
//        dialog.setHasOptionsMenu(true);
//        dialog.show(getSupportFragmentManager(), "TrackSearchDialog");


        //intent with activity
//        Intent intent = new Intent(getApplicationContext(), TrackViewerActivity.class);
//        intent.putExtra(AppConstant.GO_TO_CAMERA, AppConstant.TRUE);
//        intent.putExtra(AppConstant.PLAYLIST_OR_REMINDER, mTitle);
//        startActivity(intent);

        //intent not of this world
        Intent intent = new Intent(getApplicationContext(), HelpFeedActivity.class);
        intent.putExtra(AppConstant.GO_TO_CAMERA, AppConstant.TRUE);
        intent.putExtra(AppConstant.PLAYLIST_OR_REMINDER, mTitle);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case AppConstant.REQ_ACCPICK: {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if(GDUT.AM.setEmil(email) == GDUT.AM.CHANGED) {
                        GDActions.init(this, GDUT.AM.getActiveEmil());
                        GDActions.connect(true);
                    }
                } else if(GDUT.AM.getActiveEmil() == null) {
                    GDUT.AM.removeActiveAccnt();
                    finish();
                }
                break;
            }

            case AppConstant.REQ_AUTH:

            case AppConstant.REQ_RECOVER: {
                mIsInAuth = false;
                if(resultCode == Activity.RESULT_OK) {
                    GDActions.connect(true);
                } else if(resultCode == RESULT_CANCELED) {
                    GDUT.AM.removeActiveAccnt();
                    finish();
                }
                break;
            }
        }
    }

    //TODO: just add the persistence piece using the dataprovider
    private void delete(View view, int position) {
        mPlaylists.remove(position);
        mPlaylistAdapter.notifyParentItemRemoved(position);
    }

    private void edit(View view) {
//        Intent intent = new Intent(PlaylistActivity.this, PlaylistDetailActivity.class);
//        String id = ((TextView) view.findViewById(R.id.id_playlist_custom_home)).getText().toString();
//        intent.putExtra(AppConstant.ID, id);
//        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.home_list);
//        int isList = linearLayout.getVisibility();
//        if (isList == View.VISIBLE) {
//            intent.putExtra(AppConstant.LIST, AppConstant.TRUE);
//        }
//        ImageView tempImageView = (ImageView) view.findViewById(R.id.image_playlist_custom_home);
//        if(tempImageView.getDrawable() != null) {
//            mSendingImage = ((BitmapDrawable) tempImageView.getDrawable()).getBitmap();
//        }
//        startActivity(intent);
    }

    //TODO: just add the persisitence piece with the data provider
    private void add(View view, Integer position) {
        //addTestData(false);
        String newId = this.savePlaylistToDB(mPlaylists.get(position));
        Playlist playlist = getPlaylistFromDB(Integer.parseInt(newId));
        playlist.setId(Integer.parseInt(newId));

        mPlaylists.add(playlist);
        position = mPlaylists.size()-1;
        mPlaylistAdapter.notifyParentItemInserted(position);
        //mPlaylistAdapter.notifyChildItemRangeInserted(position,1,playlist.getTracks().size());
//        Intent intent = new Intent(PlaylistActivity.this, PlaylistDetailActivity.class);
//        String id = ((TextView) view.findViewById(R.id.id_playlist_custom_home)).getText().toString();
//        intent.putExtra(AppConstant.ID, id);
//        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.home_list);
//        int isList = linearLayout.getVisibility();
//        if (isList == View.VISIBLE) {
//            intent.putExtra(AppConstant.LIST, AppConstant.TRUE);
//        }
//        ImageView tempImageView = (ImageView) view.findViewById(R.id.image_playlist_custom_home);
//        if(tempImageView.getDrawable() != null) {
//            mSendingImage = ((BitmapDrawable) tempImageView.getDrawable()).getBitmap();
//        }
//        startActivity(intent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(this,
                DataProvider.CONTENT_URI_DATA,
                new String[]{DataProvider.COL_ID, DataProvider.COL_CONTENT},
                null,
                null,
                null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //dapter.swapCursor(data);
        //should be able to get json here

        //ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        int count =0;
        if (cursor.moveToFirst()) {
            do {
                    Playlist playlist = new Playlist(cursor.getString(1).toString());
                mPlaylists.add(playlist);
                mPlaylistAdapter.notifyParentItemInserted(count);
                count++;
                // get the data into array, or class variable
            } while (cursor.moveToNext());
        }
        cursor.close();
        //return data;
        //mPlaylists = playlists;
        //mPlaylistAdapter.notifyParentItemRangeInserted(1,mPlaylists.size()-1);
        //mPlaylistAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //adapter.swapCursor(null);
        loader = null;
    }



}
