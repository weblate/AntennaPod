package de.danoeh.antennapod.menuhandler;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.storage.preferences.UserPreferences;
import de.danoeh.antennapod.core.receiver.MediaButtonReceiver;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.sync.SynchronizationSettings;
import de.danoeh.antennapod.core.sync.queue.SynchronizationQueueSink;
import de.danoeh.antennapod.core.util.FeedItemUtil;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.PlaybackStatus;
import de.danoeh.antennapod.core.util.ShareUtils;
import de.danoeh.antennapod.dialog.ShareDialog;
import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;

/**
 * Handles interactions with the FeedItemMenu.
 */
public class FeedItemMenuHandler {

    private static final String TAG = "FeedItemMenuHandler";

    private FeedItemMenuHandler() {
    }

    /**
     * This method should be called in the prepare-methods of menus. It changes
     * the visibility of the menu items depending on a FeedItem's attributes.
     *
     * @param menu               An instance of Menu
     * @param selectedItem     The FeedItem for which the menu is supposed to be prepared
     * @return Returns true if selectedItem is not null.
     */
    public static boolean onPrepareMenu(Menu menu, FeedItem selectedItem) {
        if (menu == null || selectedItem == null) {
            return false;
        }
        final boolean hasMedia = selectedItem.getMedia() != null;
        final boolean isPlaying = hasMedia && PlaybackStatus.isPlaying(selectedItem.getMedia());
        final boolean isInQueue = selectedItem.isTagged(FeedItem.TAG_QUEUE);
        final boolean fileDownloaded = hasMedia && selectedItem.getMedia().fileExists();
        final boolean isFavorite = selectedItem.isTagged(FeedItem.TAG_FAVORITE);

        setItemVisibility(menu, R.id.skip_episode_item, isPlaying);
        setItemVisibility(menu, R.id.remove_from_queue_item, isInQueue);
        setItemVisibility(menu, R.id.add_to_queue_item, !isInQueue && selectedItem.getMedia() != null);
        setItemVisibility(menu, R.id.visit_website_item, !selectedItem.getFeed().isLocalFeed()
                && ShareUtils.hasLinkToShare(selectedItem));
        setItemVisibility(menu, R.id.share_item, !selectedItem.getFeed().isLocalFeed());
        setItemVisibility(menu, R.id.remove_inbox_item, selectedItem.isNew());
        setItemVisibility(menu, R.id.mark_read_item, !selectedItem.isPlayed());
        setItemVisibility(menu, R.id.mark_unread_item, selectedItem.isPlayed());
        setItemVisibility(menu, R.id.reset_position, hasMedia && selectedItem.getMedia().getPosition() != 0);

        // Display proper strings when item has no media
        if (hasMedia) {
            setItemTitle(menu, R.id.mark_read_item, R.string.mark_read_label);
            setItemTitle(menu, R.id.mark_unread_item, R.string.mark_unread_label);
        } else {
            setItemTitle(menu, R.id.mark_read_item, R.string.mark_read_no_media_label);
            setItemTitle(menu, R.id.mark_unread_item, R.string.mark_unread_label_no_media);
        }

        setItemVisibility(menu, R.id.add_to_favorites_item, !isFavorite);
        setItemVisibility(menu, R.id.remove_from_favorites_item, isFavorite);
        setItemVisibility(menu, R.id.remove_item, fileDownloaded);
        return true;
    }

    /**
     * Used to set the viability of a menu item.
     * This method also does some null-checking so that neither menu nor the menu item are null
     * in order to prevent nullpointer exceptions.
     * @param menu The menu that should be used
     * @param menuId The id of the menu item that will be used
     * @param visibility The new visibility status of given menu item
     * */
    private static void setItemVisibility(Menu menu, int menuId, boolean visibility) {
        if (menu == null) {
            return;
        }
        MenuItem item = menu.findItem(menuId);
        if (item != null) {
            item.setVisible(visibility);
        }
    }

    /**
     * This method allows to replace to String of a menu item with a different one.
     * @param menu Menu item that should be used
     * @param id The id of the string that is going to be replaced.
     * @param noMedia The id of the new String that is going to be used.
     * */
    public static void setItemTitle(Menu menu, int id, int noMedia) {
        MenuItem item = menu.findItem(id);
        if (item != null) {
            item.setTitle(noMedia);
        }
    }

    /**
     * The same method as {@link #onPrepareMenu(Menu, FeedItem)}, but lets the
     * caller also specify a list of menu items that should not be shown.
     *
     * @param excludeIds Menu item that should be excluded
     * @return true if selectedItem is not null.
     */
    public static boolean onPrepareMenu(Menu menu, FeedItem selectedItem, int... excludeIds) {
        if (menu == null || selectedItem == null) {
            return false;
        }
        boolean rc = onPrepareMenu(menu, selectedItem);
        if (rc && excludeIds != null) {
            for (int id : excludeIds) {
                setItemVisibility(menu, id, false);
            }
        }
        return rc;
    }

    /**
     * Default menu handling for the given FeedItem.
     *
     * A Fragment instance, (rather than the more generic Context), is needed as a parameter
     * to support some UI operations, e.g., creating a Snackbar.
     */
    public static boolean onMenuItemClicked(@NonNull Fragment fragment, int menuItemId,
                                            @NonNull FeedItem selectedItem) {

        @NonNull Context context = fragment.requireContext();


        return true;
    }

    /**
     * Remove new flag with additional UI logic to allow undo with Snackbar.
     *
     * Undo is useful for Remove new flag, given there is no UI to undo it otherwise
     * ,i.e., there is (context) menu item for add new flag
     */
    public static void markReadWithUndo(@NonNull Fragment fragment, FeedItem item,
                                        int playState, boolean showSnackbar) {
    }

    public static void removeNewFlagWithUndo(@NonNull Fragment fragment, FeedItem item) {
        markReadWithUndo(fragment, item, FeedItem.UNPLAYED, false);
    }

}
