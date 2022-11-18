package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;

import java.lang.ref.WeakReference;

import com.bumptech.glide.request.transition.Transition;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.storage.preferences.UserPreferences;
import de.danoeh.antennapod.ui.common.ThemeUtils;

public class CoverLoader {
    private int resource = 0;
    private String uri;
    private String fallbackUri;
    private TextView txtvPlaceholder;
    private ImageView imgvCover;
    private boolean textAndImageCombined;
    private MainActivity activity;

    public CoverLoader(MainActivity activity) {
        this.activity = activity;
    }

    public CoverLoader withUri(String uri) {
        this.uri = uri;
        return this;
    }

    public CoverLoader withResource(int resource) {
        this.resource = resource;
        return this;
    }

    public CoverLoader withFallbackUri(String uri) {
        fallbackUri = uri;
        return this;
    }

    public CoverLoader withCoverView(ImageView coverView) {
        imgvCover = coverView;
        return this;
    }

    public CoverLoader withPlaceholderView(TextView placeholderView) {
        txtvPlaceholder = placeholderView;
        return this;
    }

    /**
     * Set cover text and if it should be shown even if there is a cover image.
     *
     * @param placeholderView      Cover text.
     * @param textAndImageCombined Show cover text even if there is a cover image?
     */
    @NonNull
    public CoverLoader withPlaceholderView(@NonNull TextView placeholderView, boolean textAndImageCombined) {
        this.txtvPlaceholder = placeholderView;
        this.textAndImageCombined = textAndImageCombined;
        return this;
    }

    public void load() {


    }


}