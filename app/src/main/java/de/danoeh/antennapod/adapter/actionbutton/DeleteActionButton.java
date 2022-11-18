package de.danoeh.antennapod.adapter.actionbutton;

import android.content.Context;
import android.view.View;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;

public class DeleteActionButton extends ItemActionButton {

    public DeleteActionButton(FeedItem item) {
        super(item);
    }

    @Override
    @StringRes
    public int getLabel() {
        return R.string.delete_label;
    }

    @Override
    @DrawableRes
    public int getDrawable() {
        return R.drawable.ic_delete;
    }

    @Override
    public void onClick(Context context) {

    }

    @Override
    public int getVisibility() {
        return (item.getMedia() != null && item.getMedia().isDownloaded()) ? View.VISIBLE : View.INVISIBLE;
    }
}
