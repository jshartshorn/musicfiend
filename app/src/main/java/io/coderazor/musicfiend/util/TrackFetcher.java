package io.coderazor.musicfiend.util;


import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.coderazor.musicfiend.app.AppConstant;
import io.coderazor.musicfiend.model.Track;

public class TrackFetcher {
    private static final String TAG = "TrackFetcher";

    //here is the actual url for mbid
    //http://musicbrainz.org/ws/2/recording/fcbcdc39-8851-4efc-a02a-ab0e13be224f?inc=artist-credits+isrcs+releases&fmt=json

    //here is the
    //http://musicbrainz.org/ws/2/recording?query=It%27s%20only%20Love%20AND%20artist:%28ZZ%20Top%29
//    private static final String API_KEY = "";
//    private static final String FETCH_RECENTS_METHOD = "musicbrainz.tracks.getRecent";
//    private static final String SEARCH_METHOD = "musicbrainz.tracks.search";
//    private static final Uri ENDPOINT = Uri
//            .parse("http://musicbrainz.org/ws/2/recording?query")

    private static final String API_KEY = "f786bb2ce97a3b811fb701bdde55510a";
    private static final String FETCH_TOP_METHOD = "tag.gettoptracks";
    private static final String SEARCH_METHOD = "track.search";
    private static final Uri ENDPOINT = Uri
            .parse("http://ws.audioscrobbler.com/2.0/?")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .build();

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<Track> fetchRecentTracks() {
        String url = buildUrl(FETCH_TOP_METHOD, null);
        return downloadTracks(url, FETCH_TOP_METHOD);
    }

    public List<Track> searchTracks(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadTracks(url, SEARCH_METHOD);
    }

    private List<Track> downloadTracks(String url, String searchMethod) {
        List<Track> items = new ArrayList<>();

        try {
            String jsonString = getUrlString(url);
            Log.d(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);

            switch (searchMethod){
                case SEARCH_METHOD: {
                    //JSONObject tracksJsonObject = jsonBody.getJSONObject("results").getJSONObject("trackmatches");
                    //JSONArray trackJsonArray = tracksJsonObject.getJSONObject("trackmatches").getJSONArray("track");
                    jsonBody = jsonBody.getJSONObject("results").getJSONObject("trackmatches");
                    break;
                }
                case FETCH_TOP_METHOD:{
                    jsonBody = jsonBody.getJSONObject("tracks");
                    break;
                }
                default:{
                    break;
                }
            }



            parseItems(items, jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return items;
    }

    private String buildUrl(String method, String query) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);

        if (method.equals(SEARCH_METHOD)) {
            uriBuilder.appendQueryParameter("track", query);
        }

        return uriBuilder.build().toString();
    }

    private void parseItems(List<Track> items, JSONObject jsonBody)
            throws IOException, JSONException {

        JSONArray trackJsonArray = jsonBody.getJSONArray("track");
        Gson gson = new Gson();

        for (int i = 0; i < trackJsonArray.length(); i++) {
            JSONObject trackJsonObject = trackJsonArray.getJSONObject(i);
            JSONObject imageJsonOjbect = trackJsonObject.getJSONArray("image").getJSONObject(1);

            ArrayList<String> defaultGenre = new ArrayList<String>();
            defaultGenre.add("default");

            //ArrayList<String> defaultGenre = gson.fromJson(AppConstant.DEFAULT_TRACK_GENRE_JSON, new TypeToken<ArrayList<String>>() {}.getType());

            //setup a base track object from defaults then see if the external provider has more info
            Track item = new Track(AppConstant.DEFAULT_TRACK_ID, AppConstant.DEFAULT_TRACK_TITLE,
                    AppConstant.DEFAULT_TRACK_DESCRIPTION, AppConstant.DEFAULT_TRACK_DURATION, AppConstant.DEFAULT_TRACK_ARTIST,
                    defaultGenre, AppConstant.DEFAULT_TRACK_URL,AppConstant.DEFAULT_TRACK_URL,
                    AppConstant.DEFAULT_TRACK_URL);

            item.setId(trackJsonObject.getString("mbid"));
            item.setTitle(trackJsonObject.getString("name"));
            item.setArtworkURL(imageJsonOjbect.getString("#text"));
            item.setPrimaryURL(trackJsonObject.getString("url"));
            if(trackJsonObject.has("artist")) {
                item.setArtist(trackJsonObject.getString("artist"));
            }
            if(trackJsonObject.has("genre")) {
                //item.setGenre(gson.fromJson(trackJsonObject.getString("genre").toString(), new TypeToken<ArrayList<String>>() {}.getType()));
            }
            if(trackJsonObject.has("duration")) {
                item.setArtist(trackJsonObject.getString("duration"));
            }

            items.add(item);
        }
    }

}
