package io.coderazor.musicfiend.model;

import com.google.gson.annotations.SerializedName;

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

    public Track(String title, String id, String description, String duration, String artist, String streamURL, String artworkURL, ArrayList<String> genre, String primaryURL) {
        this.mTitle = title;
        this.mId = id;
        this.mDescription = description;
        this.mGenre = genre;
        this.mDuration = duration;
        this.mArtist = artist;
        this.mStreamURL = streamURL;
        this.mArtworkURL = artworkURL;
        this.mPrimaryURL = primaryURL;

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
