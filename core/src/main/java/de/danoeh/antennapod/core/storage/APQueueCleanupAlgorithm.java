package de.danoeh.antennapod.core.storage;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import de.danoeh.antennapod.model.feed.FeedItem;

/**
 * A cleanup algorithm that removes any item that isn't in the queue and isn't a favorite
 * but only if space is needed.
 */
public class APQueueCleanupAlgorithm extends EpisodeCleanupAlgorithm {

    private static final String TAG = "APQueueCleanupAlgorithm";

    /**
     * @return the number of episodes that *could* be cleaned up, if needed
     */
    public int getReclaimableItems()
    {
        return getCandidates().size();
    }

    @Override
    public int performCleanup(Context context, int numberOfEpisodesToDelete) {
        List<FeedItem> candidates = getCandidates();
        List<FeedItem> delete;


        return 0;
    }

    @NonNull
    private List<FeedItem> getCandidates() {
        List<FeedItem> candidates = new ArrayList<>();

        return candidates;
    }

    @Override
    public int getDefaultCleanupParameter() {
        return getNumEpisodesToCleanup(0);
    }
}
