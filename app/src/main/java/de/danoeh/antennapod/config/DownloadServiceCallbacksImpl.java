package de.danoeh.antennapod.config;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.os.Bundle;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.DownloadAuthenticationActivity;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.DownloadServiceCallbacks;
import de.danoeh.antennapod.net.download.serviceinterface.DownloadRequest;
import de.danoeh.antennapod.fragment.CompletedDownloadsFragment;
import de.danoeh.antennapod.fragment.QueueFragment;


public class DownloadServiceCallbacksImpl implements DownloadServiceCallbacks {

    @Override
    public PendingIntent getNotificationContentIntent(Context context) {
        return null;
    }

    @Override
    public PendingIntent getAuthentificationNotificationContentIntent(Context context, DownloadRequest request) {
        return null;
    }

    @Override
    public PendingIntent getReportNotificationContentIntent(Context context) {
        return null;
    }

    @Override
    public PendingIntent getAutoDownloadReportNotificationContentIntent(Context context) {
        return null;
    }
}
