package de.danoeh.antennapod.core.util.playback;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import androidx.core.content.ContextCompat;

import de.danoeh.antennapod.model.playback.Playable;

public class PlaybackServiceStarter {
    private final Context context;
    private final Playable media;
    private boolean shouldStreamThisTime = false;
    private boolean callEvenIfRunning = false;

    public PlaybackServiceStarter(Context context, Playable media) {
        this.context = context;
        this.media = media;
    }

    /**
     * Default value: false
     */
    public PlaybackServiceStarter callEvenIfRunning(boolean callEvenIfRunning) {
        this.callEvenIfRunning = callEvenIfRunning;
        return this;
    }

    public PlaybackServiceStarter shouldStreamThisTime(boolean shouldStreamThisTime) {
        this.shouldStreamThisTime = shouldStreamThisTime;
        return this;
    }

    public Intent getIntent() {
        return null;
    }

    public void start() {
        ContextCompat.startForegroundService(context, getIntent());
    }
}
