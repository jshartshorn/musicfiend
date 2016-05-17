package io.coderazor.musicfiend.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Track {

    @SerializedName("title")
    private String mTitle;

    @SerializedName("id")
    private String mId;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("genre")
    private ArrayList<String> mGenre;

    @SerializedName("duration")
    private String mDuration;

    @SerializedName("artist")
    private String mArtist;

    @SerializedName("primary_url")
    private String mPrimaryURL;

    @SerializedName("stream_url")
    private String mStreamURL;

    @SerializedName("artwork_url")
    private String mArtworkURL;

    public Track() {
    }

    public Track(String id, String title,  String description, String duration, String artist, ArrayList<String> genre, String streamURL, String artworkURL,  String primaryURL) {
        this.mId = id;
        this.mTitle = title;
        this.mDescription = description;
        this.mDuration = duration;
        this.mArtist = artist;
        this.mGenre = genre;
        this.mStreamURL = streamURL;
        this.mArtworkURL = artworkURL;
        this.mPrimaryURL = primaryURL;


    }

    /*
        purpose: simple constructor using json string
     */
    public Track(String trackJson){
        super();
        try {
            JSONObject obj = new JSONObject(trackJson);
            this.mId = obj.optString("id");
            this.mTitle = obj.optString("title");
            this.mDescription = obj.optString("description");
            Gson gson = new Gson();
            this.mGenre = gson.fromJson(obj.getString("genre"), new TypeToken<ArrayList<String>>() {}.getType());
            //ArrayList<String> genre = gson.fromJson(obj.getString("genre"), new TypeToken<ArrayList<String>>() {}.getType());
//            ArrayList<String> list = new ArrayList<String>();
//            JSONArray jsonArray = obj.getJSONArray("genre");
//            if (jsonArray != null) {
//                int len = jsonArray.length();
//                for (int i=0;i<len;i++){
//                    list.add(jsonArray.get(i).toString());
//                }
//            }
            //this.mGenre = genre;
            this.mDuration = obj.optString("duration");
            this.mArtist = obj.optString("artist");
            this.mStreamURL = obj.optString("stream_url");
            this.mArtworkURL = obj.optString("artwork_url");
            this.mPrimaryURL = obj.optString("primary_url");
        } catch (JSONException e) {}
    }


    public String getTitle() {
        return mTitle;
    }


    public void setTitle(String title) {
        this.mTitle = title;
    }



    public void setId(String id) {
        this.mId = id;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public void setGenre(ArrayList<String> genre) {
        this.mGenre = genre;
    }


    public void setDuration(String duration) {
        this.mDuration = duration;
    }


    public void setArtist(String artist) {
        this.mArtist = artist;
    }



    public void setStreamURL(String streamURL) {
        this.mStreamURL = streamURL;
    }


    public void setArtworkURL(String artworkURL) {
        this.mArtworkURL = artworkURL;
    }

    public String getId() {
        return mId;
    }

    public String getStreamURL() {
        return mStreamURL;
    }

    public String getArtworkURL() {
        return mArtworkURL;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getDescription() {
        return mDescription;
    }


    public ArrayList<String> getGenre() {
        return mGenre;
    }

    public String getDuration() {
        return mDuration;
    }

    public String getPrimaryURL() {
        return mPrimaryURL;
    }

    public void setPrimaryURL(String primaryURL) {
        this.mPrimaryURL = primaryURL;
    }
}
