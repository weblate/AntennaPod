package de.danoeh.antennapod.dialog;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.event.UnreadItemsUpdateEvent;

public class FeedSortDialog {
    public static void showDialog(Context context) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
        dialog.setTitle(context.getString(R.string.pref_nav_drawer_feed_order_title));
        dialog.setNegativeButton(android.R.string.cancel, (d, listener) -> d.dismiss());


    }
}
