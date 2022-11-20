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
    }

    public PlayButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setIsShowPlay();
    }

    public void setIsShowPlay() {
        AnimatedVectorDrawableCompat drawable =
                AnimatedVectorDrawableCompat.create(getContext(), R.drawable.ic_animate_pause_play);
        setImageDrawable(drawable);
        drawable.start();
        int horizontalSpacing = (int) getResources().getDimension(R.dimen.additional_horizontal_spacing);
        setPadding(horizontalSpacing, getPaddingTop(), horizontalSpacing, getPaddingBottom());
    }
}
