package de.danoeh.antennapod.core.service.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ServiceCompat;

import de.danoeh.antennapod.core.R;
import de.danoeh.antennapod.core.feed.LocalFeedUpdater;
import de.danoeh.antennapod.core.storage.EpisodeCleanupAlgorithmFactory;
import de.danoeh.antennapod.model.download.DownloadStatus;
import org.apache.commons.io.FileUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.danoeh.antennapod.core.event.DownloadEvent;
import de.danoeh.antennapod.core.util.download.ConnectionStateMonitor;
import de.danoeh.antennapod.event.FeedItemEvent;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;
import de.danoeh.antennapod.storage.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.model.download.DownloadError;

/**
 * Manages the download of feedfiles in the app. Downloads can be enqueued via the startService intent.
 * The argument of the intent is an instance of DownloadRequest in the EXTRA_REQUESTS field of
 * the intent.
 * After the downloads have finished, the downloaded object will be passed on to a specific handler, depending on the
 * type of the feedfile.
 */
public class DownloadService extends Service {
    private static final String TAG = "DownloadService";
    private static final int SCHED_EX_POOL_SIZE = 1;
    public static final String ACTION_CANCEL_DOWNLOAD = "action.de.danoeh.antennapod.core.service.cancelDownload";
    public static final String ACTION_CANCEL_ALL_DOWNLOADS = "action.de.danoeh.antennapod.core.service.cancelAll";
    public static final String EXTRA_DOWNLOAD_URL = "downloadUrl";
    public static final String EXTRA_REQUESTS = "downloadRequests";
    public static final String EXTRA_REFRESH_ALL = "refreshAll";
    public static final String EXTRA_INITIATED_BY_USER = "initiatedByUser";
    public static final String EXTRA_CLEANUP_MEDIA = "cleanupMedia";

    public static boolean isRunning = false;

    // Can be modified from another thread while iterating. Both possible race conditions are not critical:
    // Remove while iterating: We think it is still downloading and don't start a new download with the same file.
    // Add while iterating: We think it is not downloading and might start a second download with the same file.

    private final List<DownloadStatus> reportQueue = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public DownloadService() {

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service started");
        isRunning = true;

    }

    public static boolean isDownloadingFeeds() {

        return false;
    }

    public static boolean isDownloadingFile(String downloadUrl) {

        return false;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service shutting down");
        isRunning = false;

    }



    private final BroadcastReceiver cancelDownloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "cancelDownloadReceiver: " + intent.getAction());
            if (!isRunning) {
                return;
            }
            if (TextUtils.equals(intent.getAction(), ACTION_CANCEL_DOWNLOAD)) {
                String url = intent.getStringExtra(EXTRA_DOWNLOAD_URL);
                if (url == null) {
                    throw new IllegalArgumentException("ACTION_CANCEL_DOWNLOAD intent needs download url extra");
                }
            }
        }
    };

    private void doCancel(String url) {

    }

    /**
     * Adds a new DownloadStatus object to the list of completed downloads and
     * saves it in the database
     *
     * @param status the download that is going to be saved
     */
    private void saveDownloadStatus(@NonNull DownloadStatus status) {
        reportQueue.add(status);
        DBWriter.addDownloadStatus(status);
    }

    /**
     * Check if there's something else to download, otherwise stop.
     */
    private void stopServiceIfEverythingDone() {

    }

    @Nullable
    private FeedItem getFeedItemFromId(long id) {
        FeedMedia media = DBReader.getFeedMedia(id);
        if (media != null) {
            return media.getItem();
        } else {
            return null;
        }
    }




    private void cancelNotificationUpdater() {
    }

    private class NotificationUpdater implements Runnable {
        public void run() {

        }
    }

    private void postDownloaders() {
    }

    private void shutdown() {

    }
}
