package de.danoeh.antennapod;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.MediaItem;
import androidx.media3.datasource.DataSourceBitmapLoader;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.CacheBitmapLoader;
import androidx.media3.session.CommandButton;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;
import androidx.media3.session.SessionCommand;
import androidx.media3.session.SessionCommands;
import androidx.media3.session.SessionResult;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class PlaybackService extends MediaLibraryService {
    private CustomMediaLibrarySessionCallback librarySessionCallback = new CustomMediaLibrarySessionCallback();
    private ExoPlayer player;
    private MediaLibrarySession mediaLibrarySession;
    private List<CommandButton> customCommands;

    private static final String SEARCH_QUERY_PREFIX_COMPAT = "androidx://media3-session/playFromSearch";
    private static final String SEARCH_QUERY_PREFIX = "androidx://media3-session/setMediaUri";
    private static final String CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON = "android.media3.session.demo.SHUFFLE_ON";
    private static final String CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF = "android.media3.session.demo.SHUFFLE_OFF";
    private static final int NOTIFICATION_ID = 123;
    private static final String CHANNEL_ID = "demo_session_notification_channel_id";

    @Override
    public void onCreate() {
        super.onCreate();
        customCommands = new ArrayList<>();
        customCommands.add(getShuffleCommandButton(new SessionCommand(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON, Bundle.EMPTY)));
        customCommands.add(getShuffleCommandButton(new SessionCommand(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF, Bundle.EMPTY)));
        //initializeSessionAndPlayer();
        setListener(new MediaSessionServiceListener());
    }

    @Nullable
    @Override
    public MediaLibrarySession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaLibrarySession;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (!player.getPlayWhenReady() || player.getMediaItemCount() == 0) {
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        //mediaLibrarySession.setSessionActivity(getBackStackedActivity());
        //mediaLibrarySession.release();
        player.release();
        clearListener();
        super.onDestroy();
    }

    private class CustomMediaLibrarySessionCallback implements MediaLibrarySession.Callback {
        @Override
        public MediaSession.ConnectionResult onConnect(MediaSession session, MediaSession.ControllerInfo controller) {
            SessionCommands.Builder availableSessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon();
            for (CommandButton commandButton : customCommands) {
                availableSessionCommands.add(commandButton.sessionCommand);
            }
            return new MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(availableSessionCommands.build())
                    .build();
        }

        @Override
        public ListenableFuture<MediaSession.MediaItemsWithStartPosition> onPlaybackResumption(MediaSession mediaSession, MediaSession.ControllerInfo controller) {
            return Futures.immediateFuture(new MediaSession.MediaItemsWithStartPosition(
                    Collections.singletonList(MediaItemTree.INSTANCE.getRandomItem()), 0, 0));
        }

        @Override
        public ListenableFuture<SessionResult> onCustomCommand(MediaSession session, MediaSession.ControllerInfo controller, SessionCommand customCommand, Bundle args) {
            if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON == customCommand.customAction) {
                player.setShuffleModeEnabled(true);
                session.setCustomLayout(ImmutableList.of(customCommands.get(1)));
            } else if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF == customCommand.customAction) {
                player.setShuffleModeEnabled(false);
                session.setCustomLayout(ImmutableList.of(customCommands.get(0)));
            }
            return Futures.immediateFuture(new SessionResult(SessionResult.RESULT_SUCCESS));
        }

        @Override
        public ListenableFuture<LibraryResult<MediaItem>> onGetLibraryRoot(MediaLibrarySession session, MediaSession.ControllerInfo browser, @Nullable @org.jetbrains.annotations.Nullable LibraryParams params) {
            if (params != null && params.isRecent) {
                // The service currently does not support playback resumption. Tell System UI by returning
                // an error of type 'RESULT_ERROR_NOT_SUPPORTED' for a `params.isRecent` request. See
                // https://github.com/androidx/media/issues/355
                //return Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED));

                return Futures.immediateFuture(LibraryResult.ofItem(MediaItemTree.INSTANCE.getRandomItem(), params));
            }
            return Futures.immediateFuture(LibraryResult.ofItem(MediaItemTree.INSTANCE.getRootItem(), params));
        }

        @Override
        public ListenableFuture<LibraryResult<MediaItem>> onGetItem(MediaLibrarySession session, MediaSession.ControllerInfo browser, String mediaId) {
            MediaItem item = MediaItemTree.INSTANCE.getItem(mediaId);
            if (item == null) {
                return Futures.immediateFuture(
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE));
            }
            return Futures.immediateFuture(LibraryResult.ofItem(item, /* params= */ null));
        }

        @Override
        public ListenableFuture<LibraryResult<Void>> onSubscribe(MediaLibrarySession session, MediaSession.ControllerInfo browser, String parentId, @Nullable @org.jetbrains.annotations.Nullable LibraryParams params) {
            List<MediaItem> children = MediaItemTree.INSTANCE.getChildren(parentId);
            if (children == null) {
                return Futures.immediateFuture(
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE));
            }
            session.notifyChildrenChanged(browser, parentId, children.size(), params);
            return Futures.immediateFuture(LibraryResult.ofVoid());
        }

        @Override
        public ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> onGetChildren(MediaLibrarySession session, MediaSession.ControllerInfo browser, String parentId, int page, int pageSize, @Nullable @org.jetbrains.annotations.Nullable LibraryParams params) {
            List<MediaItem> children = MediaItemTree.INSTANCE.getChildren(parentId);
            if (children == null) {
                return Futures.immediateFuture(
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE));
            }
            return Futures.immediateFuture(LibraryResult.ofItemList(children, params));
        }

        @Override
        public ListenableFuture<List<MediaItem>> onAddMediaItems(MediaSession mediaSession, MediaSession.ControllerInfo controller, List<MediaItem> mediaItems) {
            List<MediaItem> updatedMediaItems = new ArrayList<>();
            for (MediaItem item : mediaItems) {
                if (item.requestMetadata.searchQuery != null) {
                    updatedMediaItems.add(getMediaItemFromSearchQuery(item.requestMetadata.searchQuery));
                } else {
                    MediaItem tree = MediaItemTree.INSTANCE.getItem(item.mediaId);
                    updatedMediaItems.add(tree != null ? tree : item);
                }
            }
            return Futures.immediateFuture(updatedMediaItems);
        }
    }

    MediaItem getMediaItemFromSearchQuery(String query) {
        String mediaTitle;
        if (query.startsWith("play")) {
            mediaTitle = query.substring(5);
        } else {
            mediaTitle = query;
        }
        MediaItem item = MediaItemTree.INSTANCE.getItemFromTitle(mediaTitle);
        if (item != null) {
            return item;
        }
        return MediaItemTree.INSTANCE.getRandomItem();
    }

    private void initializeSessionAndPlayer() {
        player =
                new ExoPlayer.Builder(this)
                        .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
                        .build();
        MediaItemTree.INSTANCE.initialize(getAssets());

        mediaLibrarySession =
                new MediaLibrarySession.Builder(this, player, librarySessionCallback)
                        .setSessionActivity(getSingleTopActivity())
                        .setCustomLayout(ImmutableList.of(customCommands.get(0)))
                        .setBitmapLoader(new CacheBitmapLoader(new DataSourceBitmapLoader(/* context= */ this)))
                        .build();
    }

    private PendingIntent getSingleTopActivity() {
        return PendingIntent.getActivity(
                this,
                0,
                new Intent(this, PlayerActivity.class),
        FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private CommandButton getShuffleCommandButton(SessionCommand sessionCommand) {
        boolean isOn = sessionCommand.customAction == CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON;
        return new CommandButton.Builder()
                .setDisplayName(
                        getString(
                                (isOn) ? R.string.exo_controls_shuffle_on_description
        : R.string.exo_controls_shuffle_off_description
        )
      )
      .setSessionCommand(sessionCommand)
                .setIconResId(isOn ? R.drawable.exo_icon_shuffle_off : R.drawable.exo_icon_shuffle_on)
      .build();
    }

    class MediaSessionServiceListener implements Listener {
        @Override
        public void onForegroundServiceStartNotAllowedException() {
            // Show notification
        }
    }
}
