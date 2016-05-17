package io.coderazor.musicfiend;

import android.net.Uri;
import android.provider.BaseColumns;

public class PlaylistContract {
    public interface PlaylistsColumns {
        String PLAYLIST_ID = "_ID";
        String PLAYLISTS_TITLE = "playlists_title";
        String PLAYLISTS_DESCRIPTION = "playlists_description";
        String PLAYLISTS_DATE = "playlist_date";
        String PLAYLISTS_TIME = "playlists_time";
        String PLAYLISTS_IMAGE = "playlists_image";
        String PLAYLISTS_TYPE = "playlists_type";
        String PLAYLISTS_IMAGE_STORAGE_SELECTION = "playlists_image_storage_selection";
        String PLAYLISTS_JSON = "playlists_json";
    }

    public static final String CONTENT_AUTHORITY = "io.coderazor.musicfiend.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_PLAYLISTS = "playlists";
    public static final Uri URI_TABLE = BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_PLAYLISTS).build();

    public static class Playlists implements  PlaylistsColumns, BaseColumns {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + ".playlists";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + ".playlists";

        public static Uri buildPlaylistUri(String playlistId) {
            return URI_TABLE.buildUpon().appendEncodedPath(playlistId).build();
        }

        public static String getPlaylistId(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }
}
