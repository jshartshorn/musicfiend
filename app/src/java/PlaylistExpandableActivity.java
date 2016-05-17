package io.coderazor.musicfienddev;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.coderazor.musicfiend.adapter.ExpandableRecyclerAdapter;
import io.coderazor.musicfiend.adapter.PlaylistExpandableAdapter;
import io.coderazor.musicfiend.app.AppController;
import io.coderazor.musicfiend.model.Playlist;
import io.coderazor.musicfiend.model.Track;
import io.coderazor.musicfiend.view.AddPlaylistDialog;

//import android.app.FragmentManager;

public class PlaylistExpandableActivity extends Activity implements ExpandableRecyclerAdapter.ExpandCollapseListener {
    // Log tag
    private static final String LOG_NAME = PlaylistExpandableActivity.class.getSimpleName();

    // playlist json url
    private static final String url = "http://joel.hartshorn.com/playlist.json";
    private ProgressDialog pDialog;
    private ArrayList<Playlist> playlists = new ArrayList<Playlist>();
    private RecyclerView listView;
    private PlaylistExpandableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_playlist);

        //getActionBar();
        //getActionBar().show();

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
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#1b1b1b")));

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
                                playlist.setDescription(obj.getString("description"));

                                Gson gson = new Gson();

                                ArrayList<Track> tracks = gson.fromJson(obj.getString("tracks"), new TypeToken<ArrayList<Track>>() {}.getType());

                                // adding track to tracks array
                                playlists.add(playlist);
                                adapter.notifyParentItemInserted(i);
                                adapter.notifyChildItemRangeInserted(i,1,tracks.size());

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
            case R.id.action_add:
                //io.coderazor.musicfienddev.AddPlaylistDialog dialog = new io.coderazor.musicfienddev.AddPlaylistDialog();
                //dialog.show(this.getSupportFragmentManager(), "AddPlaylistDialog");
                //DialogFragment newFragment = io.coderazor.musicfienddev.AddPlaylistDialog.newInstance();
                //newFragment.show(getFragmentManager(), "AddPlaylistDialog");
                //AddPlaylistDialog newFragment = AddPlaylistDialog.
                //newFragment.show(getFragmentManager(),"newone");

                AddPlaylistDialog dialog = new AddPlaylistDialog();
                //dialog.show(getFragmentManager(), "AddPlaylistDialog");

//                FragmentManager fm = getFragmentManager();
//                DialogFragment newFragment = new AddPlaylistDialog();
//                newFragment.show()
                return true;

            case R.id.action_settings:
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
