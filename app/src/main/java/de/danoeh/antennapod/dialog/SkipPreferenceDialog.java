package de.danoeh.antennapod.dialog;

import android.content.Context;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.util.Locale;

import de.danoeh.antennapod.R;

/**
 * Shows the dialog that allows setting the skip time.
 */
public class SkipPreferenceDialog {
    public static void showSkipPreference(Context context, SkipDirection direction, TextView textView) {
        int checked = 0;

        int skipSecs;


        final int[] values = context.getResources().getIntArray(R.array.seek_delta_values);
        final String[] choices = new String[values.length];


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(direction == SkipDirection.SKIP_FORWARD ? R.string.pref_fast_forward : R.string.pref_rewind);
        builder.setSingleChoiceItems(choices, checked, null);
        builder.setNegativeButton(R.string.cancel_label, null);
        builder.setPositiveButton(R.string.confirm_label, (dialog, which) -> {
            int choice = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
            if (choice < 0 || choice >= values.length) {
                System.err.printf("Choice in showSkipPreference is out of bounds %d", choice);
            } else {

            }
        });
        builder.create().show();
    }

    public enum SkipDirection {
        SKIP_FORWARD, SKIP_REWIND
    }
}
