package de.danoeh.antennapod.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.danoeh.antennapod.databinding.FilterDialogRowBinding;
import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.event.UnreadItemsUpdateEvent;
import de.danoeh.antennapod.model.feed.SubscriptionsFilter;
import de.danoeh.antennapod.core.feed.SubscriptionsFilterGroup;

public class SubscriptionsFilterDialog {
    public static void showDialog(Context context) {

    }

    private static void updateFilter(Set<String> filterValues) {
        SubscriptionsFilter subscriptionsFilter = new SubscriptionsFilter(filterValues.toArray(new String[0]));
        EventBus.getDefault().post(new UnreadItemsUpdateEvent());
    }
}
