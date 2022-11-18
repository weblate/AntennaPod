package de.danoeh.antennapod.core.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;

import java.util.concurrent.TimeUnit;

import de.danoeh.antennapod.core.R;
import de.danoeh.antennapod.storage.preferences.UserPreferences;
import de.danoeh.antennapod.model.playback.MediaType;
import de.danoeh.antennapod.core.receiver.MediaButtonReceiver;
import de.danoeh.antennapod.core.receiver.PlayerWidget;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.feed.util.ImageResourceUtils;
import de.danoeh.antennapod.core.util.TimeSpeedConverter;
import de.danoeh.antennapod.model.playback.Playable;
import de.danoeh.antennapod.playback.base.PlayerStatus;

/**
 * Updates the state of the player widget.
 */
public abstract class WidgetUpdater {
    private static final String TAG = "WidgetUpdater";

    public static class WidgetState {
        final Playable media;
        final PlayerStatus status;
        final int position;
        final int duration;
        final float playbackSpeed;

        public WidgetState(Playable media, PlayerStatus status, int position, int duration, float playbackSpeed) {
            this.media = media;
            this.status = status;
            this.position = position;
            this.duration = duration;
            this.playbackSpeed = playbackSpeed;
        }

        public WidgetState(PlayerStatus status) {
            this(null, status, Playable.INVALID_TIME, Playable.INVALID_TIME, 1.0f);
        }
    }

    /**
     * Update the widgets with the given parameters. Must be called in a background thread.
     */
    public static void updateWidget(Context context, WidgetState widgetState) {

    }

    /**
     * Returns number of cells needed for given size of the widget.
     *
     * @param size Widget size in dp.
     * @return Size in number of cells.
     */
    private static int getCellsForSize(int size) {
        int n = 2;
        while (70 * n - 30 < size) {
            ++n;
        }
        return n - 1;
    }

    private static String getProgressString(int position, int duration, float speed) {
        if (position < 0 || duration <= 0) {
            return null;
        }
        TimeSpeedConverter converter = new TimeSpeedConverter(speed);
        if (UserPreferences.shouldShowRemainingTime()) {
            return Converter.getDurationStringLong(converter.convert(position)) + " / -"
                    + Converter.getDurationStringLong(converter.convert(Math.max(0, duration - position)));
        } else {
            return Converter.getDurationStringLong(converter.convert(position)) + " / "
                    + Converter.getDurationStringLong(converter.convert(duration));
        }
    }
}
