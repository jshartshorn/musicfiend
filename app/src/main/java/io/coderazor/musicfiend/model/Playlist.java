package io.coderazor.musicfiend.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements io.coderazor.musicfiend.model.ParentListItem, Serializable {

    private String mDate, mTime, mImagePath, mType, mJson;
    private boolean mHasNoImage = false;
    private int mStorageSelection;

    private Bitmap mBitmap;

    private int mParentNumber;
    private boolean mInitiallyExpanded;

    @SerializedName("id")
    private int mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("tracks")
    private ArrayList<Track> mTracks;

    public Playlist(){}

    public Playlist(int id, String title,  String description, ArrayList<Track> tracks) {
        this.mId = id;
        this.mTitle = title;
        this.mDescription = description;
        this.mTracks = tracks;
        this.mInitiallyExpanded=false;
    }

    public Playlist(String title, String description, String date, String time, int id, int storageSelection, String type, String json) {
        this.mTitle = title;
        this.mDescription = description;
        this.mDate = date;
        this.mTime = time;
        this.mId = id;
        this.mStorageSelection = storageSelection;
        this.mType = type;
        this.mJson = json;
    }

//    public Playlist(String reminderString) {
//        String[] fields = reminderString.split("\\$");
//        this.mType = fields[0];
//        this.mId = Integer.parseInt(fields[1]);
//        this.mTitle = fields[2];
//        this.mDate = fields[5];
//        this.mTime = fields[3];
//        this.mImagePath = fields[4];
//        this.mStorageSelection = Integer.parseInt(fields[6]);
//        this.mJson = fields[8];
//        if (mType.equals(AppConstant.NORMAL)) {
//            this.mDescription = fields[7];
//            Playlist aPlaylist = new Playlist(this.mTitle, this.mDescription, this.mDate, this.mTime, this.mId, this.mStorageSelection, this.mType, this.mJson);
//            // Previous constructor does not set this, so we do it manually after invoking
//            // the constructor
//            aPlaylist.setImagePath(this.mImagePath);
//        } else {
//            String list = "";
//            for(int i = 7;i<fields.length;i++)
//                list = list+fields[i];
//            this.mDescription = list;
//        }
//    }

    public Playlist(String json){
        super();
        try {
            JSONObject obj = new JSONObject(json);
            this.mId = Integer.parseInt(obj.optString("id"));
            this.mTitle = obj.optString("title");;
            this.mDescription = obj.optString("description");
            this.mDate = obj.optString("date");
            this.mTime = obj.optString("time");
            this.mType = obj.optString("type");
            this.mJson = obj.optString("json");
            Gson gson = new Gson();
            ArrayList<Track> tracks = gson.fromJson(obj.getString("tracks"), new TypeToken<ArrayList<Track>>() {}.getType());
            this.setTracks(tracks);
        } catch (JSONException e) {}
    }

    public String convertToString() {
        return mType + "$"
                + mId + "$"
                + mTitle + "$"
                + mTime + "$"
                + mImagePath + "$"
                + mDate + "$"
                + mStorageSelection + "$"
                + mJson + "$"
                + mDescription;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
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

    public void setBitmap(String path) {
        setImagePath(path);
        this.mBitmap = BitmapFactory.decodeFile(path);
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }
    public Bitmap getBitmap() {
        return mBitmap;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getJson() {
        return mJson;
    }

    public void setJson(String json) {
        mJson = json;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public boolean isHasNoImage() {
        return mHasNoImage;
    }

    public void setHasNoImage(boolean hasNoImage) {
        mHasNoImage = hasNoImage;
    }

    public int getStorageSelection() {
        return mStorageSelection;
    }

    public void setStorageSelection(int storageSelection) {
        mStorageSelection = storageSelection;
    }
}
