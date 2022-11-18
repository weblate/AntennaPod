package de.danoeh.antennapod.core.storage;

import androidx.annotation.NonNull;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.model.feed.FeedItem;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Performs search on Feeds and FeedItems.
 */
public class FeedSearcher {
    private FeedSearcher() {

    }

    @NonNull
    public static List<FeedItem> searchFeedItems(final String query, final long selectedFeed) {

            return Collections.emptyList();
    }

    @NonNull
    public static List<Feed> searchFeeds(final String query) {

            return Collections.emptyList();
    }
}
