package io.coderazor.musicfiend;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.coderazor.musicfiend.adapter.ExpandableRecyclerAdapter;
import io.coderazor.musicfiend.adapter.PlaylistExpandableAdapter;
import io.coderazor.musicfiend.app.AppController;
import io.coderazor.musicfiend.model.Playlist;
import io.coderazor.musicfiend.model.Track;
import io.coderazor.musicfiend.view.AddPlaylistDialog;

//import android.app.FragmentManager;

public class PlaylistExpandableActivity extends BaseActivity implements ExpandableRecyclerAdapter.ExpandCollapseListener {
    // Log tag
    private static final String LOG_NAME = PlaylistExpandableActivity.class.getSimpleName();

    //various globals we need
    private List<PlaylistOld> mPlaylistOlds;
    private RecyclerView mRecyclerView;
    private PlaylistAdapterOld mPlaylistAdapterOld;
    private ContentResolver mContentResolver;
    private static Boolean mIsInAuth;
    public static Bitmap mSendingImage = null;
    private boolean mIsImageNotFound = false;
    private DropboxAPI<AndroidAuthSession> mDropboxAPI;

    // playlist json url
    private static final String url = "http://joel.hartshorn.com/playlist.json";
    private ProgressDialog pDialog;
    private ArrayList<Playlist> playlists = new ArrayList<Playlist>();
    private RecyclerView listView;
    private PlaylistExpandableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_expand_layout);

        //stuff we need from the old playlist
        activateToolbar();
        setUpForDropbox();
        setUpNavigationDrawer();
        setUpActions();

        //getActionBar();
        //getActionBar().show();

        //now initialize the data for the activity
        initPlaylist();


    }

    private void initPlaylist(){
        listView = (RecyclerView) findViewById(R.id.list);
        //search = (ImageView) findViewById(R.id.search_playlist);
        //share = (ImageView) findViewById(R.id.share_playlist);

        //this seems out of order and strange since there are no items when we set the adapter...
        //however, this is by design and the adapter listens for the changes
        adapter = new PlaylistExpandableAdapter(getApplicationContext(),playlists);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));

        //setup the recyclerview
        adapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onListItemExpanded(int position) {
                Playlist expandedPlaylist = playlists.get(position);
//                String toastMsg = getResources().getString(R.string.expanded, expandedPlaylist.getTitle());
//                Toast.makeText(PlaylistExpandableActivity.this,
//                        toastMsg,
//                        Toast.LENGTH_SHORT)
//                        .show();
            }

            @Override
            public void onListItemCollapsed(int position) {
                Playlist collapsedPlaylist = playlists.get(position);
//                String toastMsg = getResources().getString(R.string.collapsed, collapsedPlaylist.getTitle());
//                Toast.makeText(PlaylistExpandableActivity.this,
//                        toastMsg,
//                        Toast.LENGTH_SHORT)
//                        .show();
            }
        });



        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // changing action bar color
//        getActionBar().setBackgroundDrawable(
//                new ColorDrawable(Color.parseColor("#1b1b1b")));

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
                                playlist.setTitle(obj.getString("title"));
                                //track.setArtworkURL(obj.getString("artwork_url"));
                                playlist.setDescription(obj.getString("description"));
                                //track.setArtist(obj.getString("artist"));

                                Gson gson = new Gson();
                                //Track tracks[] = gson.fromJson(obj.getString("tracks"), Track[].class);

//                                Type listOfTestObject = new TypeToken<List<TestObject>>(){}.getType();
//                                String s = gson.toJson(list, listOfTestObject);
//                                List<TestObject> list2 = gson.fromJson(s, listOfTestObject);

                                ArrayList<Track> tracks = gson.fromJson(obj.getString("tracks"), new TypeToken<ArrayList<Track>>() {}.getType());
                                //adapter.notifyChildItemRangeInserted(i,0,tracks.size());
                                Toast.makeText(PlaylistExpandableActivity.this,
                                        "There are: "+tracks.size()+" tracks in this playlist: "+playlist.getTitle(),
                                        Toast.LENGTH_SHORT)
                                        .show();
//                                // tracks is json array
                                //JSONArray tracks = obj.getJSONArray("tracks");
                                //ArrayList<Track> tracks = new ArrayList<Track>();
                                for (int j = 0; j < tracks.size(); j++) {
                                    //genre.add((String) genreArry.get(j));
//                                    Track track = new Track();
//                                    track.setArtist(tracks[j]);
//                                    track.setDescription();
//                                    track.setArtworkURL();
                                    //adapter.notifyChildItemInserted(i,j);
                                }
                                playlist.setTracks(tracks);
                                //adapter.notifyChildItemRangeInserted(i,1,tracks.size());

                                // adding track to tracks array
                                playlists.add(playlist);
                                adapter.notifyParentItemInserted(i);
                                adapter.notifyChildItemRangeInserted(i,1,tracks.size());

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        //adapter.notifyDataSetChanged();
                        //adapter.notifyParentItemInserted(1);
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
//        Toast.makeText(PlaylistExpandableActivity.this,
//                "option selected...",
//                Toast.LENGTH_SHORT)
//                .show();
        switch (item.getItemId()) {
            case R.id.add_playlist:
//                Toast.makeText(PlaylistExpandableActivity.this,
//                        "action add...",
//                        Toast.LENGTH_SHORT)
//                        .show();
                AddPlaylistDialog dialog = new AddPlaylistDialog();
                dialog.show(getSupportFragmentManager(), "AddPlaylistDialog");
                return true;

            case R.id.action_settings:
//                Toast.makeText(PlaylistExpandableActivity.this,
//                        "action settings...",
//                        Toast.LENGTH_SHORT)
//                        .show();
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemExpanded(int position) {

    }

    @Override
    public void onListItemCollapsed(int position) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlists, menu);
        return true;
    }
}
