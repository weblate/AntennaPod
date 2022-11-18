package de.danoeh.antennapod.adapter;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import de.danoeh.antennapod.activity.MainActivity;

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