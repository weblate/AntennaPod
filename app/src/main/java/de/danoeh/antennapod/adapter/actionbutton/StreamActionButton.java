package de.danoeh.antennapod.adapter.actionbutton;

import android.content.ComponentName;
import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.service.playback.EasyPlaybackService;
import de.danoeh.antennapod.core.service.playback.PlaybackServiceOld;
import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;
import de.danoeh.antennapod.model.playback.MediaType;
import de.danoeh.antennapod.core.preferences.UsageStatistics;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.core.util.playback.PlaybackServiceStarter;
import de.danoeh.antennapod.dialog.StreamingConfirmationDialog;

import java.util.concurrent.ExecutionException;

public class StreamActionButton extends ItemActionButton {

    public StreamActionButton(FeedItem item) {
        super(item);
    }

    @Override
    @StringRes
    public int getLabel() {
        return R.string.stream_label;
    }

    @Override
    @DrawableRes
    public int getDrawable() {
        return R.drawable.ic_stream;
    }

    @Override
    public void onClick(Context context) {
        final FeedMedia media = item.getMedia();
        if (media == null) {
            return;
        }
        UsageStatistics.logAction(UsageStatistics.ACTION_STREAM);

        if (!NetworkUtils.isStreamingAllowed()) {
            new StreamingConfirmationDialog(context, media).show();
            return;
        }

        SessionToken sessionToken = new SessionToken(context.getApplicationContext(),
                new ComponentName(context, EasyPlaybackService.class));
        ListenableFuture<MediaController> mediacontrollerFuture = new MediaController.Builder(
                context, sessionToken).buildAsync();
        mediacontrollerFuture.addListener(() -> {
            try {
                MediaController mediaController = mediacontrollerFuture.get();
                mediaController.setMediaItem(new MediaItem.Builder().setMediaId(String.valueOf(item.getId())).build());
                mediaController.prepare();
                mediaController.play();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, MoreExecutors.directExecutor());

        //new PlaybackServiceStarter(context, media)
        //       .callEvenIfRunning(true)
        //       .start();

        //if (media.getMediaType() == MediaType.VIDEO) {
        //    context.startActivity(PlaybackServiceOld.getPlayerActivityIntent(context, media));
        //}
    }
}
