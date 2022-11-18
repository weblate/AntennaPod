package de.danoeh.antennapod.core.storage;

import android.content.Context;
import androidx.annotation.NonNull;
import de.danoeh.antennapod.model.feed.FeedItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A cleanup algorithm that removes any item that isn't a favorite but only if space is needed.
 */
public class ExceptFavoriteCleanupAlgorithm extends EpisodeCleanupAlgorithm {

    private static final String TAG = "ExceptFavCleanupAlgo";

    /**
     * The maximum number of episodes that could be cleaned up.
     *
     * @return the number of episodes that *could* be cleaned up, if needed
     */
    public int getReclaimableItems() {
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

        return 0;
    }
}
