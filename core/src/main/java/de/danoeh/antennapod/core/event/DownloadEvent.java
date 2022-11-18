package de.danoeh.antennapod.core.event;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class DownloadEvent {

    public final DownloaderUpdate update;

    private DownloadEvent(DownloaderUpdate downloader) {
        this.update = downloader;
    }


    @NonNull
    @Override
    public String toString() {
        return "DownloadEvent{" +
                "update=" + update +
                '}';
    }

    public boolean hasChangedFeedUpdateStatus(boolean oldStatus) {
        return oldStatus != update.feedIds.length > 0;
    }
}
