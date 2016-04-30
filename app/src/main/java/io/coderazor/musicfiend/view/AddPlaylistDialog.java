package io.coderazor.musicfiend.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Random;

import io.coderazor.musicfiend.data.DataProvider;
import io.coderazor.musicfiend.R;
import io.coderazor.musicfiend.model.Playlist;
import io.coderazor.musicfiend.model.Track;

public class AddPlaylistDialog extends DialogFragment {

    public static AddPlaylistDialog newInstance() {
        return new AddPlaylistDialog();
    }

    public AddPlaylistDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context ctx = getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.dialog_playlist_add, null, false);
        final EditText txtTitle = (EditText) rootView.findViewById(R.id.add_playlist_title);
        final EditText txtDescription = (EditText) rootView.findViewById(R.id.add_playlist_description);

        return new AlertDialog.Builder(ctx)
                .setTitle("Add Playlist")
                .setView(rootView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txtTitle.setError(null);

                        String title = txtTitle.getText().toString();
                        String desc = txtDescription.getText().toString();
                        if (TextUtils.isEmpty(title)) {
                            txtTitle.setError(getString(R.string.err_field_required));
                            return;
                        }

                        try {
                            //Data data = new Data(title, desc, null, null, null);

                            //need to gen id...perhaps random number to 10000....whatever...check for existence
                            //....or just max id of collection + 1...not hard....

                            Random r = new Random();
                            int rand = r.nextInt(280 - 2) + 20;

                            Playlist playlist = new Playlist();

                            playlist.setId(rand);
                            playlist.setTitle(title);
                            playlist.setDescription(desc);
                            playlist.setTracks(new ArrayList<Track>());

                            ContentValues values = new ContentValues(1);
                            values.put(DataProvider.COL_CONTENT, playlist.toString());
                            ctx.getContentResolver().insert(DataProvider.CONTENT_URI_DATA, values);
                        } catch (SQLException sqle) {}
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

}
