package io.coderazor.musicfiend;

import android.os.Bundle;
import android.support.v4.app.Fragment;


public class TrackViewerActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return TrackSearchFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);
        activateToolbar();
        //activateToolbarWithHomeEnabled();
        //setupActionBar();
        //setUpForDropbox();
        //setUpNavigationDrawer();
        //setUpActions();
    }
}
