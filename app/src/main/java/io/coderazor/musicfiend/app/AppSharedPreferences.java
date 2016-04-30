package io.coderazor.musicfiend.app;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreferences {

    // Setter method for the Navigation Drawer state
    public static void setUserLearned(Context context, String prefName, String prefValue) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(prefName, prefValue);
        editor.apply();
    }

    // Getter method for the Navigation Drawer state
    public static String hasUserLearned(Context context, String prefName, String defaultValue) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(prefName, defaultValue);
    }

    // Return preference for image location. Google Drive, Dropbox or Local
    public static int getUploadPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getInt(AppConstant.IMAGE_SELECTION_STORAGE, AppConstant.NONE_SELECTION);
    }

    // Set preference for image location. Google Drive, Dropbox or Local
    public static void setPersonalPlaylistsPreference(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(
                AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(AppConstant.IMAGE_SELECTION_STORAGE, value);
        editor.apply();
    }

    // Set ResourceID of the Google Drive path (where we save image)
    public static void storeGoogleDriveResourceId(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AppConstant.GOOGLE_DRIVE_ID, value);
        editor.apply();
    }

    // Return ResourceID of the Google Drive path (where we save image)
    public static String getGoogleDriveResourceId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.GOOGLE_DRIVE_ID, "");
    }

    // Set Google Drive directory path (where we save image)
    public static void storeGoogleDriveUploadFileName(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AppConstant.GOOGLE_DRIVE_UPLOAD_DIR, value);
        editor.apply();
    }

    // Return Google Drive directory path (where we save image)
    public static String getGoogleDriveUploadPath(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.GOOGLE_DRIVE_UPLOAD_DIR, "");
    }

    // Set Dropbox directory path (where we save image)
    public static void storeDropBoxUploadPath(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AppConstant.DROP_BOX_UPLOAD_PATH, value);
        editor.apply();
    }

    // Return Dropbox directory path (where we save image)
    public static String getDropBoxUploadPath(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstant.DROP_BOX_UPLOAD_PATH, "");
    }

    public static boolean isDropBoxAuthenticated(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(AppConstant.DROP_BOX_AUTH_BOOL, false);
    }


    // Set authenticated state of Dropbox
    public static void isDropBoxAuthenticated(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(AppConstant.DROP_BOX_AUTH_BOOL, value);
        editor.apply();
    }

    public static boolean isGoogleDriveAuthenticated(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(AppConstant.GOOGLE_DRIVE_AUTH_BOOL, false);
    }

    // Set authenticated state of Google drive
    public static void isGoogleDriveAuthenticated(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(AppConstant.GOOGLE_DRIVE_AUTH_BOOL, value);
        editor.apply();
    }
}