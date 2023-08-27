package de.danoeh.antennapod.core.service.playback;

import android.net.Uri;
import androidx.media3.common.MediaMetadata;
import de.danoeh.antennapod.core.feed.util.ImageResourceUtils;
import de.danoeh.antennapod.model.feed.FeedItem;

public class MediaMetadataConverter {
    public static MediaMetadata createMediaMetadata(FeedItem item) {
        return new MediaMetadata.Builder()
                .setTitle(item.getTitle())
                .setAlbumTitle(item.getFeed().getTitle())
                .setArtworkUri(Uri.parse(ImageResourceUtils.getEpisodeListImageLocation(
                        item.getMedia().getItem())))
                .build();
    }
}
