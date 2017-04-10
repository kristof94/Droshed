package kristof.fr.droshed;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        title = (TextView) findViewById(R.id.title);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/A little sunshine.ttf");
        title.setTypeface(typeFace,Typeface.BOLD);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        new Handler().postDelayed(
                ()->{
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
        , SPLASH_DISPLAY_LENGTH);
    }
}
