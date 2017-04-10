package kristof.fr.droshed;

import android.app.Application;
import android.content.res.Configuration;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;

/**
 * Created by kristof
 on 4/5/17.
 */

public class DroshedApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
