package de.danoeh.antennapod.core;

import android.content.Context;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.preferences.SleepTimerPreferences;
import de.danoeh.antennapod.core.preferences.UsageStatistics;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.core.util.download.NetworkConnectionChangeHandler;
import de.danoeh.antennapod.core.util.gui.NotificationUtils;

import java.io.File;

public class ClientConfigurator {
    private static boolean initialized = false;

    public static synchronized void initialize(Context context) {
        if (initialized) {
            return;
        }
        UsageStatistics.init(context);
        PlaybackPreferences.init(context);
        NetworkUtils.init(context);
        NetworkConnectionChangeHandler.init(context);
        SleepTimerPreferences.init(context);
        NotificationUtils.createChannels(context);
        initialized = true;
    }
}
