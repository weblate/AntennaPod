package de.danoeh.antennapod.view;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import de.danoeh.antennapod.R;

public class PlayButton extends AppCompatImageButton {

    public PlayButton(@NonNull Context context) {
        super(context);
    }

    public PlayButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setIsShowPlay(false);
    }

    public PlayButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setIsShowPlay(true);
    }

    public void setIsShowPlay(boolean showPlay) {
        AnimatedVectorDrawableCompat drawable;
        if (showPlay) {
            drawable = AnimatedVectorDrawableCompat.create(
                    getContext(), R.drawable.ic_animate_pause_play);
        } else {
            drawable = AnimatedVectorDrawableCompat.create(
                    getContext(), R.drawable.ic_animate_play_pause);
        }
        setImageDrawable(drawable);
        drawable.start();
    }
}
