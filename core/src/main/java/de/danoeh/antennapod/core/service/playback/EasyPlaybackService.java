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
import de.danoeh.antennapod.model.feed.FeedMedia;
import de.danoeh.antennapod.model.playback.Playable;
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
        return buildQueue();
    }

    @Override
    @NonNull
    public ListenableFuture<MediaSession.MediaItemsWithStartPosition> onPlaybackResumption(
            @NonNull MediaSession mediaSession, @NonNull MediaSession.ControllerInfo controller) {
        SettableFuture<MediaSession.MediaItemsWithStartPosition> future = SettableFuture.create();
        Single.fromCallable(() -> new MediaSession.MediaItemsWithStartPosition(buildQueue().get(), 0, 0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(future::set, future::setException);
        return future;
    }

    SettableFuture<List<MediaItem>> buildQueue() {
        SettableFuture<List<MediaItem>> future = SettableFuture.create();
        Single.fromCallable(() -> {
            Playable playable = PlaybackPreferences.createInstanceFromPreferences(getApplicationContext());
            FeedMedia media = (FeedMedia) playable;
            List<MediaItem> items = new ArrayList<>();
            items.add(new MediaItem.Builder()
                    .setUri(media.getStreamUrl())
                    .setMediaMetadata(new MediaMetadata.Builder()
                            .setTitle(media.getEpisodeTitle())
                            .setAlbumTitle(media.getFeedTitle())
                            .setArtworkUri(Uri.parse(ImageResourceUtils.getEpisodeListImageLocation(media.getItem())))
                            .build())
                    .build());
            return items;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(future::set, future::setException);
        return future;
    }

    @Nullable
    @Override
    public MediaLibrarySession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return session;
    }
}
