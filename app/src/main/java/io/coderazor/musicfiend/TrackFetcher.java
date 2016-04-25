package io.coderazor.musicfiend;


import android.net.Uri;
import android.util.Log;

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
            //.appendQueryParameter("track", "json")
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            //.appendQueryParameter("nojsoncallback", "1")
            //.appendQueryParameter("extras", "url_s")
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
        return downloadTracks(url);
    }

    public List<Track> searchTracks(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadTracks(url);
    }

    private List<Track> downloadTracks(String url) {
        List<Track> items = new ArrayList<>();

        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
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

        JSONObject tracksJsonObject = jsonBody.getJSONObject("results");
        JSONArray trackJsonArray = tracksJsonObject.getJSONObject("trackmatches").getJSONArray("track");

        for (int i = 0; i < trackJsonArray.length(); i++) {
            JSONObject trackJsonObject = trackJsonArray.getJSONObject(i);
            JSONObject imageJsonOjbect = trackJsonObject.getJSONArray("image").getJSONObject(1);

            Track item = new Track();
            item.setId(trackJsonObject.getString("mbid"));
            item.setTitle(trackJsonObject.getString("name"));
            item.setArtworkURL(imageJsonOjbect.getString("#text"));
            item.setPrimaryURL(trackJsonObject.getString("url"));

//            if (!trackJsonObject.has("url_s")) {
//                continue;
//            }
//
//            item.setUrl(trackJsonObject.getString("url_s"));
            items.add(item);
        }
    }

}
