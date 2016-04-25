package io.coderazor.musicfiend.app;

import android.content.Context;
import android.content.SharedPreferences;

import io.coderazor.musicfiend.AppConstant;

public class AppSharedPreferences {

    // Return preference for image location. Google Drive, Dropbox or Local
    public static int getUploadPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getInt(AppConstant.IMAGE_SELECTION_STORAGE, AppConstant.NONE_SELECTION);
    }

}