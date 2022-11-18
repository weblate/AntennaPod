package de.danoeh.antennapod.menuhandler;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.ShareUtils;
import de.danoeh.antennapod.dialog.IntraFeedSortDialog;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.model.feed.SortOrder;
import org.apache.commons.lang3.StringUtils;

/**
 * Handles interactions with the FeedItemMenu.
 */
public class FeedMenuHandler {

    private FeedMenuHandler(){ }

    private static final String TAG = "FeedMenuHandler";

    public static boolean onPrepareOptionsMenu(Menu menu, Feed selectedFeed) {
        if (selectedFeed == null) {
            return true;
        }



        return true;
    }

    /**
     * NOTE: This method does not handle clicks on the 'remove feed' - item.
     */
    public static boolean onOptionsItemClicked(final Context context, final MenuItem item, final Feed selectedFeed) {

        return true;
    }



}
