package de.danoeh.antennapod.core.util.download;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import de.danoeh.antennapod.core.util.NetworkUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AutoUpdateManager {
    private static final String WORK_ID_FEED_UPDATE = "de.danoeh.antennapod.core.service.FeedUpdateWorker";
    private static final String WORK_ID_FEED_UPDATE_ONCE = WORK_ID_FEED_UPDATE + "Once";
    private static final String TAG = "AutoUpdateManager";

    private AutoUpdateManager() {

    }

    /**
     * Start / restart periodic auto feed refresh
     * @param context Context
     */
    public static void restartUpdateAlarm(Context context) {

    }

    /**
     * Sets the interval in which the feeds are refreshed automatically
     */
    private static void restartUpdateIntervalAlarm(long intervalMillis, Context context) {
        Log.d(TAG, "Restarting update alarm.");


;
    }

    /**
     * Sets time of day the feeds are refreshed automatically
     */
    private static void restartUpdateTimeOfDayAlarm(int hoursOfDay, int minute, Context context) {
        Log.d(TAG, "Restarting update alarm.");

        Calendar now = Calendar.getInstance();
        Calendar alarm = (Calendar) now.clone();
        alarm.set(Calendar.HOUR_OF_DAY, hoursOfDay);
        alarm.set(Calendar.MINUTE, minute);
        if (alarm.before(now) || alarm.equals(now)) {
            alarm.add(Calendar.DATE, 1);
        }
        long triggerAtMillis = alarm.getTimeInMillis() - now.getTimeInMillis();


    }

    /**
     * Run auto feed refresh once in background, as soon as what OS scheduling allows.
     *
     * Callers from UI should use {@link #runImmediate(Context)}, as it will guarantee
     * the refresh be run immediately.
     * @param context Context
     */
    public static void runOnce(Context context) {
        Log.d(TAG, "Run auto update once, as soon as OS allows.");


    }

    /**
     /**
     * Run auto feed refresh once in background immediately, using its own thread.
     *
     * Callers where the additional threads is not suitable should use {@link #runOnce(Context)}
     */
    public static void runImmediate(@NonNull Context context) {
        Log.d(TAG, "Run auto update immediately in background.");
        if (!NetworkUtils.networkAvailable()) {
            Log.d(TAG, "Ignoring: No network connection.");
        } else if (NetworkUtils.isFeedRefreshAllowed()) {
            startRefreshAllFeeds(context);
        } else {
            confirmMobileAllFeedsRefresh(context);
        }
    }

    private static void confirmMobileAllFeedsRefresh(final Context context) {

    }

    private static void startRefreshAllFeeds(final Context context) {
    }

    public static void disableAutoUpdate(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_ID_FEED_UPDATE);
    }

    private static Constraints getConstraints() {
        Constraints.Builder constraints = new Constraints.Builder();


        return constraints.build();
    }

}
