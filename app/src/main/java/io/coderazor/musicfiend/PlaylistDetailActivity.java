package io.coderazor.musicfiend;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import io.coderazor.musicfiend.app.AppConstant;
import io.coderazor.musicfiend.app.AppSharedPreferences;
import io.coderazor.musicfiend.app.BaseActivity;
import io.coderazor.musicfiend.dropbox.DropBoxActions;
import io.coderazor.musicfiend.dropbox.DropBoxImageUploadAsync;
import io.coderazor.musicfiend.dropbox.DropBoxPickerActivity;
import io.coderazor.musicfiend.model.Playlist;

public class PlaylistDetailActivity extends BaseActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // Constants
    public static final int NORMAL = 1;
    public static final int LIST = 2;
    public static final int CAMERA_REQUEST = 1888;
    public static final int TAKE_GALLERY_CODE = 1;
    private static int sMonth, sYear, sHour, sDay, sMinute, sSecond;
    private static TextView sDateTextView, sTimeTextView, sJson;
    private static boolean sIsInAuth;
    private static String sTmpFlNm;
    private DropboxAPI<AndroidAuthSession> mApi;
    private File mDropBoxFile;
    private String mCameraFileName;
    private PlaylistCustomList mPlaylistCustomList;
    private EditText mTitleEditText, mDescriptionEditText, mJson;
    private ImageView mNoteImage;
    private String mImagePath = AppConstant.NO_IMAGE;
    private String mId;
    private boolean mGoingToCameraOrGallery = false, mIsEditing = false;
    private boolean mIsImageSet = false;
    private boolean mIsList = false;
    private Bundle mBundle;
    private ImageView mStorageSelection;
    private boolean mIsNotificationMode = false;
    private String mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mBundle = savedInstanceState;
        setContentView(R.layout.activity_expandable_detail_playlist_layout);
        activateToolbarWithHomeEnabled();
        if (getIntent().getStringExtra(AppConstant.LIST_PLAYLISTS) != null) {
            initializeComponents(LIST);
        } else {
            initializeComponents(NORMAL);
        }

        setUpIfEditing();
        if (getIntent().getStringExtra(AppConstant.GO_TO_CAMERA) != null) {
            callCamera();
        }

    }

    private void setUpIfEditing() {
        if (getIntent().getStringExtra(AppConstant.ID) != null) {
            mId = getIntent().getStringExtra(AppConstant.ID);
            mIsEditing = true;
            if (getIntent().getStringExtra(AppConstant.LIST) != null) {
                initializeComponents(LIST);
            }
            setValues(mId);
            mStorageSelection.setEnabled(false);
        }
        if (getIntent().getStringExtra(AppConstant.REMINDER) != null) {
            Playlist aPlaylist = new Playlist(getIntent().getStringExtra(AppConstant.REMINDER));
            mId = aPlaylist.getId() + "";
            mIsNotificationMode = true;
            setValues(aPlaylist);
            //removeFromReminder(aPlaylist);
            mStorageSelection.setEnabled(false);
        }
    }

    //
    private void setValues(String id) {
        String[] projection = {BaseColumns._ID,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_TITLE,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_DESCRIPTION,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_JSON,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_DATE,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_IMAGE,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_IMAGE_STORAGE_SELECTION,
                PlaylistContract.PlaylistsColumns.PLAYLISTS_TIME};
        // Query database - check parameters to return only partial records.
        Uri r = PlaylistContract.URI_TABLE;
        String selection = PlaylistContract.PlaylistsColumns.PLAYLIST_ID + " = " + id;
        Cursor cursor = getContentResolver().query(r, projection, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_TITLE));
                    String description = cursor.getString(cursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_DESCRIPTION));
                    String json = cursor.getString(cursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_JSON));
                    String time = cursor.getString(cursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_TIME));
                    String date = cursor.getString(cursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_DATE));
                    String image = cursor.getString(cursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_IMAGE));
                    int storageSelection = cursor.getInt(cursor.getColumnIndex(PlaylistContract.PlaylistsColumns.PLAYLISTS_IMAGE_STORAGE_SELECTION));
                    mTitleEditText.setText(title);
                    mJson.setText(json);
                    if (mIsList) {
                        CardView cardView = (CardView) findViewById(R.id.card_view);
                        cardView.setVisibility(View.GONE);
                        setUpList(description);
                    } else {
                        mDescriptionEditText.setText(description);
                    }
                    sTimeTextView.setText(time);
                    sDateTextView.setText(date);

                    mImagePath = image;
                    if (!image.equals(AppConstant.NO_IMAGE)) {
                        mNoteImage.setImageBitmap(PlaylistActivity.mSendingImage);
                    }
                    switch (storageSelection) {
                        case AppConstant.GOOGLE_DRIVE_SELECTION:
                            updateStorageSelection(null, R.drawable.ic_google_drive, AppConstant.GOOGLE_DRIVE_SELECTION);
                            break;
                        case AppConstant.DEVICE_SELECTION:
                        case AppConstant.NONE_SELECTION:
                            updateStorageSelection(null, R.drawable.ic_local, AppConstant.DEVICE_SELECTION);
                            break;
                        case AppConstant.DROP_BOX_SELECTION:
                            updateStorageSelection(null, R.drawable.ic_dropbox, AppConstant.DROP_BOX_SELECTION);
                            break;
                    }
                } while (cursor.moveToNext());
            }
        }

    }

    private void setValues(Playlist playlist) {
        getSupportActionBar().setTitle(AppConstant.DRAWER_EXPANDED_PLAYLIST);
        String title = playlist.getTitle();
        String description = playlist.getDescription();
        String time = playlist.getTime();
        String date = playlist.getDate();
        String image = playlist.getImagePath();
        if (playlist.getType().equals(AppConstant.LIST)) {
            mIsList = true;
        }
        mTitleEditText.setText(title);
        if (mIsList) {
            initializeComponents(LIST);
            CardView cardView = (CardView) findViewById(R.id.card_view);
            cardView.setVisibility(View.GONE);
            setUpList(description);
        } else {
            mDescriptionEditText.setText(description);
        }
        sTimeTextView.setText(time);
        sDateTextView.setText(date);
        mImagePath = image;
        int storageSelection = playlist.getStorageSelection();
        switch (storageSelection) {
            case AppConstant.GOOGLE_DRIVE_SELECTION:
                updateStorageSelection(null, R.drawable.ic_google_drive, AppConstant.GOOGLE_DRIVE_SELECTION);
                break;
            case AppConstant.DEVICE_SELECTION:
            case AppConstant.NONE_SELECTION:
                if (!mImagePath.equals(AppConstant.NO_IMAGE)) {
                    updateStorageSelection(null, R.drawable.ic_local, AppConstant.DEVICE_SELECTION);
                }
                break;
            case AppConstant.DROP_BOX_SELECTION:
                updateStorageSelection(BitmapFactory.decodeFile(mImagePath), R.drawable.ic_dropbox, AppConstant.DROP_BOX_SELECTION);
                break;

            default:
                break;
        }
    }

    //todo: this is storage selection
    //again always save to local db, but allow for saving elsewhere as well
    private void updateStorageSelection(Bitmap bitmap, int storageSelectionResource, int selection) {
        if (bitmap != null) {
            mNoteImage.setImageBitmap(bitmap);
        }
        mStorageSelection.setBackgroundResource(storageSelectionResource);
        AppSharedPreferences.setPersonalPlaylistsPreference(getApplicationContext(), selection);
    }

    //use this
    private void setUpList(String description) {
        mDescription = description;
        if (!mIsNotificationMode) {
            mPlaylistCustomList.setUpForEditMode(description);
        } else {
            LinearLayout newItemLayout = (LinearLayout) findViewById(R.id.add_check_list_layout);
            newItemLayout.setVisibility(View.GONE);
            mPlaylistCustomList.setUpForListNotification(description);
        }

        LinearLayout layout = (LinearLayout) findViewById(R.id.add_check_list_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlaylistCustomList.addNewCheckBox();
            }
        });
    }

    private void initializeComponents(int choice) {
        if (choice == LIST) {
            CardView cardView = (CardView) findViewById(R.id.card_view);
            cardView.setVisibility(View.GONE);
            cardView = (CardView) findViewById(R.id.card_view_list);
            cardView.setVisibility(View.VISIBLE);
            mIsList = true;
        } else if (choice == NORMAL) {
            CardView cardView = (CardView) findViewById(R.id.card_view_list);
            cardView.setVisibility(View.GONE);
            mIsList = false;
        }

        //need to initialize this from the menu bar
        //todo
        mStorageSelection = (ImageView) findViewById(R.id.image_storage);
        if (AppSharedPreferences.getUploadPreference(getApplicationContext()) ==
                AppConstant.GOOGLE_DRIVE_SELECTION) {
            mStorageSelection.setBackgroundResource(R.drawable.ic_google_drive);
        } else if (AppSharedPreferences.getUploadPreference(getApplicationContext()) ==
                AppConstant.DROP_BOX_SELECTION) {
            mStorageSelection.setBackgroundResource(R.drawable.ic_dropbox);
        } else {
            mStorageSelection.setBackgroundResource(R.drawable.ic_local);
        }

        mPlaylistCustomList = new PlaylistCustomList(this);
        mPlaylistCustomList.setUp();
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.check_list_layout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(mPlaylistCustomList    );
        mStorageSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(PlaylistDetailActivity.this, v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.actions_image_selection, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_device) {
                            updateStorageSelection(null, R.drawable.ic_local, AppConstant.DEVICE_SELECTION);
                        } else if (menuItem.getItemId() == R.id.action_google_drive) {
                            if (!AppSharedPreferences.isGoogleDriveAuthenticated(getApplicationContext())) {
                                startActivity(new Intent(PlaylistDetailActivity.this, GoogleDriveSelectionActivity.class));
                                finish();
                            } else {
                                updateStorageSelection(null, R.drawable.ic_google_drive, AppConstant.GOOGLE_DRIVE_SELECTION);
                            }
                        } else if (menuItem.getItemId() == R.id.action_dropbox) {
                            AppSharedPreferences.setPersonalPlaylistsPreference(getApplicationContext(), AppConstant.DROP_BOX_SELECTION);
                            if (!AppSharedPreferences.isDropBoxAuthenticated(getApplicationContext())) {
                                startActivity(new Intent(PlaylistDetailActivity.this, DropBoxPickerActivity.class));
                                finish();
                            } else {
                                updateStorageSelection(null, R.drawable.ic_dropbox, AppConstant.DROP_BOX_SELECTION);
                            }
                        }

                        if (mBundle != null) {
                            mCameraFileName = mBundle.getString("mCameraFileName");
                        }
                        AndroidAuthSession session = DropBoxActions.buildSession(getApplicationContext());
                        mApi = new DropboxAPI<AndroidAuthSession>(session);

                        return false;
                    }
                });
            }
        });

        mTitleEditText = (EditText) findViewById(R.id.make_playlist_title);
        mNoteImage = (ImageView) findViewById(R.id.image_make_playlist);
        mDescriptionEditText = (EditText) findViewById(R.id.make_playlist_detail);
        sDateTextView = (TextView) findViewById(R.id.date_textview_make_playlist);
        sTimeTextView = (TextView) findViewById(R.id.time_textview_make_playlist);
        ImageView datePickerImageView = (ImageView) findViewById(R.id.date_picker_button);
        ImageView dateTimeDeleteImageView = (ImageView) findViewById(R.id.delete_make_playlist);
        dateTimeDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sDateTextView.setText("");
                sTimeTextView.setText(AppConstant.NO_TIME);
            }
        });

        datePickerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDatePickerDialog datePickerDialog = new AppDatePickerDialog();
                datePickerDialog.show(getSupportFragmentManager(), AppConstant.DATE_PICKER);
            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.add_check_list_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlaylistCustomList.addNewCheckBox();
            }
        });
    }

    private Calendar getTargetTime() {
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();
        calSet.set(Calendar.MONTH, sMonth);
        calSet.set(Calendar.YEAR, sYear);
        calSet.set(Calendar.DAY_OF_MONTH, sDay);
        calSet.set(Calendar.HOUR_OF_DAY, sHour);
        calSet.set(Calendar.MINUTE, sMinute);
        calSet.set(Calendar.SECOND, sSecond);
        calSet.set(Calendar.MILLISECOND, 0);
        if (calSet.compareTo(calNow) <= 0) {
            calSet.add(Calendar.DATE, 1);
        }

        return calSet;
    }

    private void saveInDropBox() {
        AndroidAuthSession session = DropBoxActions.buildSession(getApplicationContext());
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        session = mApi.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
                DropBoxActions.storeAuth(session, getApplicationContext());
            } catch (IllegalStateException e) {
                showToast(AppConstant.AUTH_ERROR_DROPBOX + e.getLocalizedMessage());
            }
        }

        DropBoxImageUploadAsync upload = new DropBoxImageUploadAsync(this, mApi,
                mDropBoxFile, AppConstant.PLAYLIST_PREFIX + GDUT.time2Titl(null) + AppConstant.JPG);
        upload.execute();
        ContentValues values = createContentValues(AppConstant.PLAYLIST_PREFIX + GDUT.time2Titl(null), AppConstant.DROP_BOX_SELECTION, true);
    }

    private void saveInGoogleDrive() {
        GDUT.init(this);
        if (checkPlayServices() && checkUserAccount()) {
            GDActions.init(this, GDUT.AM.getActiveEmil());
            GDActions.connect(true);
        }
        if (mBundle != null) {
            sTmpFlNm = mBundle.getString(AppConstant.TMP_FILE_NAME);
        }
        final String resourceId = AppConstant.PLAYLIST_PREFIX + GDUT.time2Titl(null) + AppConstant.JPG;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    File tmpFl = new File(mImagePath);
                    GDActions.create(AppSharedPreferences.getGoogleDriveResourceId(getApplicationContext()),
                            resourceId, GDUT.MIME_JPEG, GDUT.file2Bytes(tmpFl));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // Add more error handling here
                }

            }
        }).start();
        ContentValues values = createContentValues(AppConstant.PLAYLIST_PREFIX + GDUT.time2Titl(null) +
                AppConstant.JPG, AppConstant.GOOGLE_DRIVE_SELECTION, true);
    }

    private void saveInDevice() {

        ContentValues values = createContentValues(mImagePath, AppConstant.DEVICE_SELECTION, true);
        int id = insertPlaylist(values);
        mId = id + "";
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(AppConstant.TMP_FILE_NAME, sTmpFlNm);
        outState.putString("mCameraFileName", mCameraFileName);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PlaylistDetailActivity.this, PlaylistActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_playlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_camera:
                mGoingToCameraOrGallery = true;
                callCamera();
                break;

            case R.id.action_gallery:
                mGoingToCameraOrGallery = true;
                callGallery();
                break;

            case android.R.id.home:
                if (!mIsNotificationMode) {
                    savePlaylist();
                } else {
                    if (!sTimeTextView.getText().toString().equals(AppConstant.NO_TIME)) {
                        //actAsReminder();
                    } else {
                        actAsPlaylist();
                    }
//                    moveToArchive(mIsList);
//                    //mType = ARCHIVES;
//                    mTitle = AppConstant.ARCHIVES;
//                    startActivity(new Intent(PlaylistDetailActivityOld.this, ArchivesActivity.class));
//                    finish();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void callGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, TAKE_GALLERY_CODE);
    }

    protected void savePlaylist() {
        if (mIsEditing) {
            switch (AppSharedPreferences.getUploadPreference(getApplicationContext())) {
                case AppConstant.DROP_BOX_SELECTION:
                    if (!mImagePath.equals(AppConstant.NO_IMAGE)) {
                        editForSaveInDropBox();
                    } else {
                        editForSaveInDevice();
                    }
                    break;

                case AppConstant.GOOGLE_DRIVE_SELECTION:
                    if (!mImagePath.equals(AppConstant.NO_IMAGE) && mIsImageSet) {
                        editForSaveInGoogleDrive();
                    } else {
                        editForSaveInDevice();
                    }
                    break;

                case AppConstant.DEVICE_SELECTION:
                case AppConstant.NONE_SELECTION:
                    editForSaveInDevice();
                    break;
            }
        } else if (mTitleEditText.getText().toString().length() > 0 && !mGoingToCameraOrGallery) {
            switch (AppSharedPreferences.getUploadPreference(getApplicationContext())) {
                case AppConstant.DROP_BOX_SELECTION:
                    if (!mImagePath.equals(AppConstant.NO_IMAGE)) {
                        saveInDropBox();
                    } else {
                        saveInDevice();
                    }
                    break;

                case AppConstant.GOOGLE_DRIVE_SELECTION:
                    if (!mImagePath.equals(AppConstant.NO_IMAGE)) {
                        saveInGoogleDrive();
                    } else {
                        saveInDevice();
                    }
                    break;

                case AppConstant.DEVICE_SELECTION:
                case AppConstant.NONE_SELECTION:
                    saveInDevice();
                    break;
            }
        }
        startActivity(new Intent(PlaylistDetailActivity.this, PlaylistActivity.class));
        finish();
    }

    private void editForSaveInDropBox() {
        if(AppSharedPreferences.isDropBoxAuthenticated(getApplicationContext())) {
            AndroidAuthSession session = DropBoxActions.buildSession(getApplicationContext());
            mApi = new DropboxAPI<AndroidAuthSession>(session);
            session = mApi.getSession();
            if(session.authenticationSuccessful()) {
                try {
                    session.finishAuthentication();
                    DropBoxActions.storeAuth(session, getApplicationContext());
                } catch(IllegalStateException e) {
                    showToast(AppConstant.AUTH_ERROR_DROPBOX + e.getLocalizedMessage());
                }
            }
        }
        ContentValues values = createContentValues("", AppConstant.DROP_BOX_SELECTION, false);
        if(mIsImageSet) {
            String filename = AppConstant.PLAYLIST_PREFIX + GDUT.time2Titl(null) + AppConstant.JPG;
            values.put(PlaylistContract.PlaylistsColumns.PLAYLISTS_IMAGE, filename);
            mDropBoxFile = new File(getApplicationContext().getCacheDir(), filename);
            try {
                mDropBoxFile.createNewFile();
            } catch(IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Bitmap newImage = ((BitmapDrawable) mNoteImage.getDrawable()).getBitmap();
            newImage.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapData = bos.toByteArray();
            try {
                FileOutputStream fos = new FileOutputStream(mDropBoxFile);
                fos.write(bitmapData);
                fos.flush();
                fos.close();
            } catch(IOException e) {
                e.printStackTrace();
            }

            DropBoxImageUploadAsync uploadAsync = new DropBoxImageUploadAsync(this, mApi, mDropBoxFile, filename);
            uploadAsync.execute();
        }

        updatePlaylist(values);
    }

    private void editForSaveInGoogleDrive() {
        GDUT.init(this);
        final String resourceId = AppConstant.PLAYLIST_PREFIX + GDUT.time2Titl(null) + AppConstant.JPG;
        if(checkPlayServices() && checkUserAccount()) {
            GDActions.init(this, GDUT.AM.getActiveEmil());
            GDActions.connect(true);
        }
        if(mBundle != null) {
            sTmpFlNm = mBundle.getString(AppConstant.TMP_FILE_NAME);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if(mIsImageSet) {
                        File tmpFL = null;
                        try {
                            tmpFL = new File(mImagePath);
                            GDActions.create(AppSharedPreferences.getGoogleDriveResourceId(getApplicationContext()),
                                    resourceId, GDUT.MIME_JPEG, GDUT.file2Bytes(tmpFL));
                        } finally {
//                            if(tmpFL != null) {
//                                tmpFL.delete();
//                            }
                        }
                    }
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        ContentValues values = createContentValues(resourceId, AppConstant.GOOGLE_DRIVE_SELECTION,  false);
        updatePlaylist(values);
    }

    private void editForSaveInDevice() {
        ContentValues values = createContentValues(mImagePath, AppConstant.DEVICE_SELECTION, false);
        updatePlaylist(values);
    }

    private void callCamera() {
        Intent cameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap photo = null;
        switch(requestCode) {
            case AppConstant.REQ_ACCPICK:
                if(resultCode == Activity.RESULT_OK && data != null) {
                    String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if(GDUT.AM.setEmil(email) == GDUT.AM.CHANGED) {
                        GDActions.init(this, GDUT.AM.getActiveEmil());
                        GDActions.connect(true);
                    }
                } else if(GDUT.AM.getActiveEmil() == null) {
                    GDUT.AM.removeActiveAccnt();
                    finish();
                }
                break;
            case AppConstant.REQ_AUTH:
            case AppConstant.REQ_RECOVER:
                sIsInAuth = false;
                if(resultCode == Activity.RESULT_OK) {
                    GDActions.connect(true);
                } else if(resultCode == RESULT_CANCELED) {
                    GDUT.AM.removeActiveAccnt();
                    finish();
                }
                break;

            case AppConstant.REQ_SCAN: {
                if(resultCode == Activity.RESULT_OK) {
                    final String titl = GDUT.time2Titl(null);
                    if (titl != null && sTmpFlNm != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                File tmpFl = null;
                                GDActions.createTreeGDAA(GDUT.MYROOT, titl, GDUT.file2Bytes(tmpFl));
                            }
                        }).start();
                    }
                }
                break;
            }

        }

        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            mGoingToCameraOrGallery = false;
            photo = (Bitmap) data.getExtras().get("data");
            mNoteImage.setImageBitmap(photo);
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(getRealPathFromURI(tempUri));
            mImagePath = finalFile.toString();
            mIsImageSet = true;
        } else if (requestCode == TAKE_GALLERY_CODE) {
            if(resultCode == RESULT_OK) {
                mGoingToCameraOrGallery = false;
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mImagePath = cursor.getString(columnIndex);
                cursor.close();
                File tempFile = new File(mImagePath);
                photo = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                mNoteImage.setVisibility(View.VISIBLE);
                mNoteImage.setImageBitmap(photo);
                mIsImageSet = true;
            } else {
                mIsImageSet = false;
            }
        }
        if(mIsImageSet) {
            mDropBoxFile = new File(mImagePath);
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(!sIsInAuth) {
            if(connectionResult.hasResolution()) {
                try {
                    sIsInAuth = true;
                    connectionResult.startResolutionForResult(this, AppConstant.REQ_AUTH);
                } catch(IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    // Add other error handling here
                    finish();
                }
            } else {
                finish();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    private boolean checkUserAccount() {
        String email = GDUT.AM.getActiveEmil();
        Account account = GDUT.AM.getPrimaryAccnt(true);
        if(email == null) {
            if(account == null) {
                account = showAccountPicker();
                return false;
            } else {
                // Only one a/c registered
                GDUT.AM.setEmil(account.name);
            }
            return true;
        }

        account = GDUT.AM.getActiveAccnt();
        if(account == null) {
            account = showAccountPicker();
            return false;
        }
        return true;
    }

    private boolean checkPlayServices() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS) {
            if(GooglePlayServicesUtil.isUserRecoverableError(status)) {
                errorDialog(status, AppConstant.REQ_RECOVER);
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void errorDialog(int errorCode, int requestCode) {
        Bundle args = new Bundle();
        args.putInt(AppConstant.DIALOG_ERROR, errorCode);
        args.putInt(AppConstant.REQUEST_CODE, requestCode);
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), AppConstant.DIALOG_ERROR);
    }

    protected ContentValues createContentValues(String noteImage, int storageSelection, boolean isSave) {
        if(noteImage == null || noteImage.equals("")) {
            noteImage = AppConstant.NO_IMAGE;
        }
        ContentValues values = new ContentValues();
        values.put(PlaylistContract.PlaylistsColumns.PLAYLISTS_TITLE, mTitleEditText.getText().toString());
        values.put(PlaylistContract.PlaylistsColumns.PLAYLISTS_DATE, sDateTextView.getText().toString());
        values.put(PlaylistContract.PlaylistsColumns.PLAYLISTS_TIME, sTimeTextView.getText().toString());
        if(mIsImageSet || isSave) {
            values.put(PlaylistContract.PlaylistsColumns.PLAYLISTS_IMAGE, noteImage);
        }
        values.put(PlaylistContract.PlaylistsColumns.PLAYLISTS_IMAGE_STORAGE_SELECTION, storageSelection);
        String type = AppConstant.NORMAL;
        String description = mDescriptionEditText.getText().toString();
        //String json = mJson.getText().toString();
        String json = "Default";
        if(mIsList) {
            description = mPlaylistCustomList.getLists();
            type = AppConstant.LIST;
        }

        values.put(PlaylistContract.PlaylistsColumns.PLAYLISTS_TYPE, type);
        values.put(PlaylistContract.PlaylistsColumns.PLAYLISTS_DESCRIPTION, description);
        values.put(PlaylistContract.PlaylistsColumns.PLAYLISTS_JSON, json);

        return values;
    }

    protected int insertPlaylist(ContentValues values) {
        //ContentResolver contentResolver = getContentResolver();
        //Uri uri = Uri.parse(PlaylistContract.BASE_CONTENT_URI + "/playlists");
        ///Uri uri = Uri.parse(DataProvider.CONTENT_URI_DATA.toString());
        //Uri returned = contentResolver.insert(DataProvider.CONTENT_URI_DATA, values);
        //String[] temp = returned.toString().split("/");
        //return Integer.parseInt(temp[temp.length-1]);

        //let's try and see if there is something missing
        Playlist playlist = new Playlist();
        playlist.setId(0);
        playlist.setTime(AppConstant.NO_TIME);
        playlist.setDate(AppConstant.NO_TIME);
        playlist.setImagePath(AppConstant.NO_IMAGE);
        playlist.setTitle(mTitleEditText.getText().toString());
        playlist.setDescription(mDescriptionEditText.getText().toString());
        playlist.setJson(playlist.getJson());

        String newId = this.savePlaylistToDB(playlist);
        playlist = getPlaylistFromDB(Integer.parseInt(newId));
        playlist.setId(Integer.parseInt(newId));

        return Integer.parseInt(newId);

    }

    protected void updatePlaylist(ContentValues values) {
//        ContentResolver contentResolver = getContentResolver();
//        Uri uri = Uri.parse(PlaylistContract.BASE_CONTENT_URI + "/playlists");
//        String selection = PlaylistContract.PlaylistsColumns.PLAYLIST_ID + " = " + mId;
//        contentResolver.update(uri, values, selection, null);
        showToast("Update Playlist");
    }

    private Account showAccountPicker() {
        Account account = GDUT.AM.getPrimaryAccnt(false);
        Intent intent = AccountPicker.newChooseAccountIntent(account, null,
                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},true,null, null, null, null);
        startActivityForResult(intent, AppConstant.REQ_ACCPICK);
        return account;
    }

    public static class AppDatePickerDialog extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private int mYear, mMonth, mDay;
        private String tempMonth;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            tempMonth = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.UK);
            return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if(year == mYear) {
                if(monthOfYear == mMonth) {
                    if(dayOfMonth == mDay) {
                        sDateTextView.setText(AppConstant.TODAY);
                    } else {
                        sDateTextView.setText(dayOfMonth + " " + sMonth);
                    }
                } else {
                    sDateTextView.setText(dayOfMonth + " " + sMonth);
                }
            } else {
                sDateTextView.setText(dayOfMonth + " " + sMonth + " " + year);
            }
            sYear = year;
            sMonth = monthOfYear;
            sDay = dayOfMonth;
            AppTimePickerDialog timePickerDialog = new AppTimePickerDialog();
            timePickerDialog.show(getFragmentManager(), AppConstant.DATE_PICKER);
        }
    }


    public static class AppTimePickerDialog extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        private int mHour, mMinute;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, mHour, mMinute,DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(minute <10) {
                sTimeTextView.setText(hourOfDay + ":0" + minute);
            } else {
                sTimeTextView.setText(hourOfDay + ":" + minute);
            }
            sHour = hourOfDay;
            sMinute = minute;
            sSecond = 0;
        }
    }
}