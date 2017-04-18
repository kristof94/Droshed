package kristof.fr.droshed;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

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
