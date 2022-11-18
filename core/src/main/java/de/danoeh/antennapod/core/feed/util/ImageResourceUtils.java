package de.danoeh.antennapod.core.feed.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;
import de.danoeh.antennapod.model.playback.Playable;

/**
 */
public final class ImageResourceUtils {

    private ImageResourceUtils() {
    }

    /**
     * returns the image location, does prefer the episode cover if available and enabled in settings.
     */
    @Nullable
    public static String getEpisodeListImageLocation(@NonNull Playable playable) {
            return getFallbackImageLocation(playable);
    }

    /**
     * returns the image location, does prefer the episode cover if available and enabled in settings.
     */
    @Nullable
    public static String getEpisodeListImageLocation(@NonNull FeedItem feedItem) {
            return feedItem.getImageLocation();
    }

    @Nullable
    public static String getFallbackImageLocation(@NonNull Playable playable) {
        if (playable instanceof FeedMedia) {
            FeedMedia media = (FeedMedia) playable;
            FeedItem item = media.getItem();
            if (item != null && item.getFeed() != null) {
                return item.getFeed().getImageUrl();
            } else {
                return null;
            }
        } else {
            return playable.getImageLocation();
        }
    }

    @Nullable
    public static String getFallbackImageLocation(@NonNull FeedItem feedItem) {
        if (feedItem.getFeed() != null) {
            return feedItem.getFeed().getImageUrl();
        } else {
            return null;
        }
    }
}
