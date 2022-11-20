package de.danoeh.antennapod.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import de.danoeh.antennapod.R;

public class PlaybackSpeedSeekBar extends FrameLayout {
    private SeekBar seekBar;
    private Consumer<Float> progressChangedListener;

    public PlaybackSpeedSeekBar(@NonNull Context context) {
        super(context);
        setup();
    }

    public PlaybackSpeedSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public PlaybackSpeedSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {

    }

    public void updateSpeed(float speedMultiplier) {
        seekBar.setProgress(Math.round((20 * speedMultiplier) - 10));
    }

    public void setProgressChangedListener(Consumer<Float> progressChangedListener) {
        this.progressChangedListener = progressChangedListener;
    }

    public float getCurrentSpeed() {
        return (seekBar.getProgress() + 10) / 20.0f;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        seekBar.setEnabled(enabled);
    }
}
