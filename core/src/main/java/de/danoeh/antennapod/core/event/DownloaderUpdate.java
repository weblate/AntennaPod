package de.danoeh.antennapod.core.event;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.model.feed.FeedMedia;
import de.danoeh.antennapod.core.util.LongList;

public class DownloaderUpdate {

    /* Downloaders that are currently running */
    /**
     * IDs of feeds that are currently being downloaded
     * Often used to show some progress wheel in the action bar
     */
    public long[] feedIds;

    /**
     * IDs of feed media that are currently being downloaded
     * Can be used to show and update download progress bars
     */
    public long[] mediaIds;



    @NonNull
    @Override
    public String toString() {
        return "DownloaderUpdate{" +
                ", feedIds=" + Arrays.toString(feedIds) +
                ", mediaIds=" + Arrays.toString(mediaIds) +
                '}';
    }
}
