package de.danoeh.antennapod.core.sync.queue;

import android.content.Context;

import de.danoeh.antennapod.core.sync.LockingAsyncExecutor;
import de.danoeh.antennapod.core.sync.SynchronizationSettings;
import de.danoeh.antennapod.model.feed.FeedMedia;

public class SynchronizationQueueSink {
    // To avoid a dependency loop of every class to SyncService, and from SyncService back to every class.
    private static Runnable serviceStarterImpl = () -> { };

    public static void setServiceStarterImpl(Runnable serviceStarter) {
        serviceStarterImpl = serviceStarter;
    }

    public static void syncNow() {
        serviceStarterImpl.run();
    }

    public static void clearQueue(Context context) {
    }

    public static void enqueueFeedAddedIfSynchronizationIsActive(Context context, String downloadUrl) {
        if (!SynchronizationSettings.isProviderConnected()) {
            return;
        }
        LockingAsyncExecutor.executeLockedAsync(() -> {
            syncNow();
        });
    }

    public static void enqueueFeedRemovedIfSynchronizationIsActive(Context context, String downloadUrl) {
        if (!SynchronizationSettings.isProviderConnected()) {
            return;
        }
        LockingAsyncExecutor.executeLockedAsync(() -> {
            syncNow();
        });
    }



    public static void enqueueEpisodePlayedIfSynchronizationIsActive(Context context, FeedMedia media,
                                                                     boolean completed) {

    }

}
