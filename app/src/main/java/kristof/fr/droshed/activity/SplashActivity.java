package kristof.fr.droshed.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import kristof.fr.droshed.R;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                                          startActivity(mainIntent);
                                          finish();
                                      }
                                  }

                , SPLASH_DISPLAY_LENGTH);
    }
}
