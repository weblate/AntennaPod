package de.danoeh.antennapod.core.service.playback;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.ForwardingPlayer;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.DefaultMediaNotificationProvider;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import de.danoeh.antennapod.core.R;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.event.PlayerErrorEvent;
import de.danoeh.antennapod.event.playback.PlaybackPositionEvent;
import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;
import de.danoeh.antennapod.storage.preferences.UserPreferences;
import de.danoeh.antennapod.ui.appstartintent.MainActivityStarter;
import de.danoeh.antennapod.ui.appstartintent.VideoPlayerActivityStarter;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EasyPlaybackService extends MediaLibraryService
        implements MediaLibraryService.MediaLibrarySession.Callback, Player.Listener {
    private MediaLibrarySession session;
    private Player player;
    private Disposable positionEventTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        DefaultLoadControl.Builder loadControl = new DefaultLoadControl.Builder();
        loadControl.setBufferDurationsMs(30000, 120000,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
        loadControl.setBackBuffer(UserPreferences.getRewindSecs() * 1000 + 500, true);
        ExoPlayer exoPlayer = new ExoPlayer.Builder(getApplicationContext())
                .setRenderersFactory(new DefaultRenderersFactory(this))
                .setLoadControl(loadControl.build())
                .build();
        player = new ForwardingPlayer(exoPlayer) {
            @Override
            @NonNull
            public Commands getAvailableCommands() {
                return super.getAvailableCommands().buildUpon()
                        .add(ExoPlayer.COMMAND_SEEK_FORWARD)
                        .add(ExoPlayer.COMMAND_SEEK_BACK)
                        .build();
            }
        };
        player.addListener(this);
        session = new MediaLibrarySession.Builder(this, player, this).build();
        DefaultMediaNotificationProvider notificationProvider = new DefaultMediaNotificationProvider(this);
        notificationProvider.setSmallIcon(R.drawable.ic_notification);
        setMediaNotificationProvider(notificationProvider);
        setupPositionObserver();
    }

    @Override
    public void onDestroy() {
        cancelPositionObserver();
        super.onDestroy();
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

    private static MediaItem createMediaInfo(FeedItem item) {
        MediaItem.Builder builder = new MediaItem.Builder()
                .setMediaId(String.valueOf(item.getId()))
                .setMediaMetadata(MediaMetadataConverter.createMediaMetadata(item));
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

    @Override
    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
        if (mediaItem == null) {
            PlaybackPreferences.writeNoMediaPlaying();
            return;
        }
        PlaybackPreferences.writeMediaPlaying(Long.parseLong(mediaItem.mediaId));
        Intent playerActivityIntent;
        if (mediaItem.mediaMetadata.mediaType != null
                && mediaItem.mediaMetadata.mediaType == MediaMetadata.MEDIA_TYPE_VIDEO) {
            playerActivityIntent = new VideoPlayerActivityStarter(this).getIntent();
        } else {
            playerActivityIntent = new MainActivityStarter(this).withOpenPlayer().getIntent();
        }
        session.setSessionActivity(PendingIntent.getActivity(this, R.id.pending_intent_player_activity,
                playerActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        | (Build.VERSION.SDK_INT >= 31 ? PendingIntent.FLAG_MUTABLE : 0)));
    }

    @Override
    public void onPlayerError(PlaybackException error) {
        if (NetworkUtils.wasDownloadBlocked(error)) {
            EventBus.getDefault().postSticky(
                    new PlayerErrorEvent(getString(R.string.download_error_blocked)));
        } else {
            Throwable cause = error.getCause();
            if (cause instanceof HttpDataSource.HttpDataSourceException) {
                if (cause.getCause() != null) {
                    cause = cause.getCause();
                }
            }
            if (cause != null && "Source error".equals(cause.getMessage())) {
                cause = cause.getCause();
            }
            EventBus.getDefault().postSticky(
                    new PlayerErrorEvent(cause != null ? cause.getMessage() : error.getMessage()));
        }
    }

    private void setupPositionObserver() {
        cancelPositionObserver();
        positionEventTimer = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(number -> {
                    EventBus.getDefault().post(new PlaybackPositionEvent(
                            (int) player.getCurrentPosition(), (int) player.getDuration()));
                });
    }

    private void cancelPositionObserver() {
        if (positionEventTimer != null) {
            positionEventTimer.dispose();
        }
    }
}
