package de.danoeh.antennapod.core.service.playback;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.DefaultMediaNotificationProvider;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import de.danoeh.antennapod.core.R;
import de.danoeh.antennapod.core.feed.util.ImageResourceUtils;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

public class EasyPlaybackService extends MediaLibraryService
        implements MediaLibraryService.MediaLibrarySession.Callback {
    private MediaLibrarySession session;

    @Override
    public void onCreate() {
        super.onCreate();
        Player player = new ExoPlayer.Builder(getApplicationContext())
                .setRenderersFactory(new DefaultRenderersFactory(this).setExtensionRendererMode(
                        DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER))
                .build();
        session = new MediaLibrarySession.Builder(this, player, this).build();
        DefaultMediaNotificationProvider notificationProvider = new DefaultMediaNotificationProvider(this);
        notificationProvider.setSmallIcon(R.drawable.ic_notification);
        setMediaNotificationProvider(notificationProvider);
    }

    @Override
    @NonNull
    public ListenableFuture<List<MediaItem>> onAddMediaItems(@NonNull MediaSession mediaSession,
             @NonNull MediaSession.ControllerInfo controller, @NonNull List<MediaItem> mediaItems) {
        SettableFuture<List<MediaItem>> future = SettableFuture.create();
        Single.<List<MediaItem>>fromCallable(() -> {
            if (mediaItems.isEmpty()) {
                return new ArrayList<>();
            }
            int mediaId = Integer.parseInt(mediaItems.get(0).mediaId);
            List<FeedItem> queue = DBReader.getQueue();
            boolean isInQueue = false;
            for (FeedItem item : queue) {
                if (item.getMedia().getId() == mediaId) {
                    isInQueue = true;
                    break;
                }
            }
            if (!isInQueue) {
                FeedMedia media = DBReader.getFeedMedia(mediaId);
                queue = DBWriter.addQueueItem(this, false, true, media.getItem().getId()).get();
            }
            List<MediaItem> items = new ArrayList<>();
            boolean isBeforeCurrentItem = true;
            for (FeedItem item : queue) {
                if (item.getMedia().getId() == mediaId) {
                    isBeforeCurrentItem = false;
                }
                if (isBeforeCurrentItem) {
                    continue;
                }
                items.add(createMediaInfo(item));
            }
            return items;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(future::set, future::setException);
        return future;
    }

    @Override
    @NonNull
    public ListenableFuture<MediaSession.MediaItemsWithStartPosition> onPlaybackResumption(
            @NonNull MediaSession mediaSession, @NonNull MediaSession.ControllerInfo controller) {
        SettableFuture<MediaSession.MediaItemsWithStartPosition> future = SettableFuture.create();
        Single.fromCallable(() -> {
            @Nullable FeedMedia currentItem = (FeedMedia) PlaybackPreferences.createInstanceFromPreferences(this);
            List<FeedItem> queue = DBReader.getQueue();
            List<MediaItem> items = new ArrayList<>();
            int currentItemIdx = 0;
            long currentItemPosition = 0;
            for (int i = 0; i < queue.size(); i++) {
                items.add(createMediaInfo(queue.get(i)));
                if (currentItem != null && queue.get(i).getId() == currentItem.getId()) {
                    currentItemIdx = i;
                    currentItemPosition = currentItem.getPosition();
                }
            }
            return new MediaSession.MediaItemsWithStartPosition(items, currentItemIdx, currentItemPosition);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(future::set, future::setException);
        return future;
    }

    private MediaItem createMediaInfo(FeedItem item) {
        MediaItem.Builder builder = new MediaItem.Builder()
                .setMediaId(String.valueOf(item.getId()))
                .setMediaMetadata(new MediaMetadata.Builder()
                        .setTitle(item.getTitle())
                        .setAlbumTitle(item.getFeed().getTitle())
                        .setArtworkUri(Uri.parse(ImageResourceUtils.getEpisodeListImageLocation(
                                item.getMedia().getItem())))
                        .build());
        if (item.isDownloaded()) {
            builder.setUri(item.getMedia().getFile_url());
        } else {
            builder.setUri(item.getMedia().getStreamUrl());
        }
        return builder.build();
    }

    @Nullable
    @Override
    public MediaLibrarySession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return session;
    }
}
