package de.danoeh.antennapod.core.util.playback;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import androidx.annotation.NonNull;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.event.playback.PlaybackServiceEvent;
import de.danoeh.antennapod.model.playback.Playable;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

/**
 * Communicates with the playback service. GUI classes should use this class to
 * control playback instead of communicating with the PlaybackService directly.
 */
public abstract class PlaybackController {

    private static final String TAG = "PlaybackController";

    private final Activity activity;
    private Playable media;

    private boolean mediaInfoLoaded = false;
    private boolean released = false;
    private boolean initialized = false;
    private boolean eventsRegistered = false;
    private long loadedFeedMedia = -1;

    public PlaybackController(@NonNull Activity activity) {
        this.activity = activity;
    }

    /**
     * Creates a new connection to the playbackService.
     */
    public synchronized void init() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PlaybackServiceEvent event) {
        if (event.action == PlaybackServiceEvent.Action.SERVICE_STARTED) {
            init();
        }
    }

    private synchronized void initServiceRunning() {

    }

    /**
     * Should be called if the PlaybackController is no longer needed, for
     * example in the activity's onStop() method.
     */
    public void release() {
        Log.d(TAG, "Releasing PlaybackController");
    }


    /**
     * Should be called in the activity's onPause() method.
     */
    public void pause() {
        mediaInfoLoaded = false;
    }




    public void onPlaybackEnd() {}

    /**
     * Is called whenever the PlaybackService changes its status. This method
     * should be used to update the GUI or start/cancel background threads.
     */
    private void handleStatus() {

    }

    private void checkMediaInfoLoaded() {
        if (!mediaInfoLoaded || loadedFeedMedia != PlaybackPreferences.getCurrentlyPlayingFeedMediaId()) {
            loadedFeedMedia = PlaybackPreferences.getCurrentlyPlayingFeedMediaId();
            loadMediaInfo();
        }
        mediaInfoLoaded = true;
    }

    protected void updatePlayButtonShowsPlay(boolean showPlay) {

    }

    public abstract void loadMediaInfo();

    /**
     * Called when connection to playback service has been established or
     * information has to be refreshed
     */
    private void queryService() {

    }

    public void playPause() {

    }

    public int getPosition() {

            return Playable.INVALID_TIME;
    }

    public int getDuration() {

            return Playable.INVALID_TIME;
    }

    public Playable getMedia() {
        if (media == null) {
            media = PlaybackPreferences.createInstanceFromPreferences(activity);
        }
        return media;
    }

    public boolean sleepTimerActive() {
        return true;
    }

    public void disableSleepTimer() {
    }

    public long getSleepTimerTimeLeft() {
            return Playable.INVALID_TIME;
    }

    public void extendSleepTimer(long extendTime) {

    }

    public void setSleepTimer(long time) {

    }

    public void seekTo(int time) {

    }

    public void setVideoSurface(SurfaceHolder holder) {

    }

    public void setPlaybackSpeed(float speed) {

    }

    public void setSkipSilence(boolean skipSilence) {

    }

    public float getCurrentPlaybackSpeedMultiplier() {
        return 0;
    }

    public boolean canDownmix() {
        return true;
    }

    public void setDownmix(boolean enable) {
    }

    public List<String> getAudioTracks() {
            return Collections.emptyList();
    }

    public int getSelectedAudioTrack() {
            return -1;
    }

    public void setAudioTrack(int track) {

    }

    public boolean isPlayingVideoLocally() {
            return false;
    }

    public Pair<Integer, Integer> getVideoSize() {
            return null;
    }

    public void notifyVideoSurfaceAbandoned() {
    }

    public boolean isStreaming() {
        return false;
    }
}
