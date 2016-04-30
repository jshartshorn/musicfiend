package io.coderazor.musicfiend.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/*
jsh: this class is used for simple local data persistence to sqlite...single column with single data
 */

public class DataProvider extends ContentProvider {

    public static final Uri CONTENT_URI_DATA = Uri.parse("content://io.coderazor.musicfiend.provider/data");

    public static final String TABLE_DATA = "data";
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";

    private static final int DATA_ALLROWS = 1;
    private static final int DATA_SINGLE_ROW = 2;

    private DbHelper dbHelper;

    public DataProvider() {
    }

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("io.coderazor.musicfiend.provider", "data", DATA_ALLROWS);
        uriMatcher.addURI("io.coderazor.musicfiend.provider", "data/#", DATA_SINGLE_ROW);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch(uriMatcher.match(uri)) {
            case DATA_ALLROWS:
                qb.setTables(TABLE_DATA);
                break;

            case DATA_SINGLE_ROW:
                qb.setTables(TABLE_DATA);
                qb.appendWhere("_id = " + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id;
        switch(uriMatcher.match(uri)) {
            case DATA_ALLROWS:
                id = db.insertOrThrow(TABLE_DATA, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Uri insertUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(insertUri, null);
        return insertUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count;
        switch(uriMatcher.match(uri)) {
            case DATA_ALLROWS:
                count = db.update(TABLE_DATA, values, selection, selectionArgs);
                break;

            case DATA_SINGLE_ROW:
                count = db.update(TABLE_DATA, values, "_id = ?", new String[]{uri.getLastPathSegment()});
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count;
        switch(uriMatcher.match(uri)) {
            case DATA_ALLROWS:
                count = db.delete(TABLE_DATA, selection, selectionArgs);
                break;

            case DATA_SINGLE_ROW:
                count = db.delete(TABLE_DATA, "_id = ?", new String[]{uri.getLastPathSegment()});
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    protected static final class DbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "musicfiend.db";
        private static final int DATABASE_VERSION = 10;

        private static final String SQL_CREATE_DATA = "create table data (_id integer primary key autoincrement, content text);";

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("DataProvider","onCreate");
            db.execSQL(SQL_CREATE_DATA);
            loadDefaultData();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("DataProvider","onUpgrade");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
            onCreate(db);
        }

        //method to initialize tables
        private void loadDefaultData(){

        }

        public static void deleteDatabase(Context context) {
            Log.d("DataProvider","DeleteDB");
            context.deleteDatabase(DATABASE_NAME);
        }


    }
}
