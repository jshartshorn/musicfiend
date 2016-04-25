package io.coderazor.musicfiend;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private Typeface font;
    private TextView text1;

    private Handler handler;
    private Runnable callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //need to replace this with my own splash page
        getWindow().setBackgroundDrawableResource(R.drawable.background_splash);

        setContentView(R.layout.activity_splash);

        text1 = (TextView) findViewById(android.R.id.text1);

        font = Typeface.createFromAsset(getAssets(), "fonts/Lemon-Regular.ttf");
        //text1.setTypeface(font);

        //launch the next screen after delay
        handler = new Handler();
        callback = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };
        handler.postDelayed(callback, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(callback);
    }

}
