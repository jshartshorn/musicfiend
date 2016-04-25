package io.coderazor.musicfiend.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements io.coderazor.musicfiend.model.ParentListItem, Serializable {

    private int mParentNumber;
    private boolean mInitiallyExpanded;

    @SerializedName("id")
    private int mID;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("tracks")
    private ArrayList<Track> mTracks;

    public Playlist(){}

    public Playlist(int mID, String mTitle,  String mDescription, ArrayList<Track> mTracks) {
        this.mID = mID;
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mTracks = mTracks;
        this.mInitiallyExpanded=false;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getID() {
        return mID;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public ArrayList<Track> getTracks() {
        return mTracks;
    }

    public void setTracks(ArrayList<Track> mTracks) {
        this.mTracks = mTracks;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }


    public int getParentNumber() {
        return mParentNumber;
    }

    public void setParentNumber(int parentNumber) {
        mParentNumber = parentNumber;
    }


    @Override
    public ArrayList<Track> getChildItemList() {
        return mTracks;
    }


    public void setChildItemList(ArrayList<Track> childItemList) {
        mTracks = childItemList;
    }

    public void setInitiallyExpanded(boolean initiallyExpanded) {
        mInitiallyExpanded = initiallyExpanded;
    }
}
