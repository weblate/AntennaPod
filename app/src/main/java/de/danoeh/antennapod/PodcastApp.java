package de.danoeh.antennapod;

import android.content.ComponentName;
import android.content.Intent;
import android.os.StrictMode;

import androidx.multidex.MultiDexApplication;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;

import de.danoeh.antennapod.activity.SplashActivity;
import de.danoeh.antennapod.config.ApplicationCallbacksImpl;
import de.danoeh.antennapod.core.ApCoreEventBusIndex;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.ClientConfigurator;
import de.danoeh.antennapod.error.CrashReportWriter;
import de.danoeh.antennapod.error.RxJavaErrorHandlerSetup;
import de.danoeh.antennapod.spa.SPAUtil;
import org.greenrobot.eventbus.EventBus;

/** Main application class. */
public class PodcastApp extends MultiDexApplication {

    private static PodcastApp singleton;

    public static PodcastApp getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public static void forceRestart() {
        Intent intent = new Intent(getInstance(), SplashActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        getInstance().startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

}
