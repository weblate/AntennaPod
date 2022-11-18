package de.danoeh.antennapod.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.util.download.AutoUpdateManager;
import de.danoeh.antennapod.databinding.FeedRefreshDialogBinding;
import org.apache.commons.lang3.ArrayUtils;

import java.util.concurrent.TimeUnit;

public class FeedRefreshIntervalDialog {
    private static final int[] INTERVAL_VALUES_HOURS = {1, 2, 4, 8, 12, 24, 72};
    private final Context context;
    private FeedRefreshDialogBinding viewBinding;

    public FeedRefreshIntervalDialog(Context context) {
        this.context = context;
    }

    public void show() {
    }

    private String[] buildSpinnerEntries() {
        final Resources res = context.getResources();
        String[] entries = new String[INTERVAL_VALUES_HOURS.length];
        for (int i = 0; i < INTERVAL_VALUES_HOURS.length; i++) {
            int hours = INTERVAL_VALUES_HOURS[i];
            entries[i] = res.getQuantityString(R.plurals.feed_refresh_every_x_hours, hours, hours);
        }
        return entries;
    }

    private void updateVisibility() {
        viewBinding.spinner.setVisibility(viewBinding.intervalRadioButton.isChecked() ? View.VISIBLE : View.GONE);
        viewBinding.timePicker.setVisibility(viewBinding.timeRadioButton.isChecked() ? View.VISIBLE : View.GONE);
    }
}
