package de.danoeh.antennapod.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;
import de.danoeh.antennapod.model.feed.Feed;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RemoveFeedDialog {
    private static final String TAG = "RemoveFeedDialog";

    public static void show(Context context, Feed feed) {
        List<Feed> feeds = Collections.singletonList(feed);
        String message = getMessageId(context, feeds);
        showDialog(context, feeds, message);
    }

    public static void show(Context context, List<Feed> feeds) {
        String message = getMessageId(context, feeds);
        showDialog(context, feeds, message);
    }

    private static void showDialog(Context context, List<Feed> feeds, String message) {

    }

    private static String getMessageId(Context context, List<Feed> feeds) {
        if (feeds.size() == 1) {
            if (feeds.get(0).isLocalFeed()) {
                return context.getString(R.string.feed_delete_confirmation_local_msg, feeds.get(0).getTitle());
            } else {
                return context.getString(R.string.feed_delete_confirmation_msg, feeds.get(0).getTitle());
            }
        } else {
            return context.getString(R.string.feed_delete_confirmation_msg_batch);
        }

    }
}
