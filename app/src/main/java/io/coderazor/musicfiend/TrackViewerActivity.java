package io.coderazor.musicfiend;

import android.support.v4.app.Fragment;


public class TrackViewerActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return TrackViewerFragment.newInstance();
    }
}
