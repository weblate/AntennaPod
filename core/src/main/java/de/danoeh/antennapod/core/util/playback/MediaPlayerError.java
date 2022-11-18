package de.danoeh.antennapod.core.util.playback;

import android.content.Context;
import android.media.MediaPlayer;
import com.google.android.exoplayer2.ExoPlaybackException;
import de.danoeh.antennapod.core.R;

/** Utility class for MediaPlayer errors. */
public class MediaPlayerError {
    private MediaPlayerError(){}

    /** Get a human-readable string for a specific error code. */
    public static String getErrorString(Context context, int code) {
        return "";
    }
}
