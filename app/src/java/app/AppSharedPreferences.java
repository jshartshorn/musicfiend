package io.coderazor.musicfienddev;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreferences {

    // Return preference for image location. Google Drive, Dropbox or Local
    public static int getUploadPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(io.coderazor.musicfienddev.AppConstant.PERSONAL_PLAYLISTS_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getInt(io.coderazor.musicfienddev.AppConstant.IMAGE_SELECTION_STORAGE, io.coderazor.musicfienddev.AppConstant.NONE_SELECTION);
    }

}