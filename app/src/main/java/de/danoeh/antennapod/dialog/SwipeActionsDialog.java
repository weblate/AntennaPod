package de.danoeh.antennapod.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.databinding.SwipeactionsDialogBinding;
import de.danoeh.antennapod.databinding.SwipeactionsRowBinding;

public class SwipeActionsDialog {
    private static final int LEFT = 1;
    private static final int RIGHT = 0;

    private final Context context;
    private final String tag;


    public SwipeActionsDialog(Context context, String tag) {
        this.context = context;
        this.tag = tag;
    }

    public void show(Callback prefsChanged) {

        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);


        builder.setTitle(context.getString(R.string.swipeactions_label) + " - ");
        SwipeactionsDialogBinding viewBinding = SwipeactionsDialogBinding.inflate(LayoutInflater.from(context));
        builder.setView(viewBinding.getRoot());

        viewBinding.enableSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            viewBinding.actionLeftContainer.getRoot().setAlpha(b ? 1.0f : 0.4f);
            viewBinding.actionRightContainer.getRoot().setAlpha(b ? 1.0f : 0.4f);
        });


        setupSwipeDirectionView(viewBinding.actionLeftContainer, LEFT);
        setupSwipeDirectionView(viewBinding.actionRightContainer, RIGHT);

        builder.setPositiveButton(R.string.confirm_label, (dialog, which) -> {
            saveActionsEnabledPrefs(viewBinding.enableSwitch.isChecked());
            prefsChanged.onCall();
        });

        builder.setNegativeButton(R.string.cancel_label, null);
        builder.create().show();
    }

    private void setupSwipeDirectionView(SwipeactionsRowBinding view, int direction) {

    }



    private void saveActionsEnabledPrefs(Boolean enabled) {

    }

    public interface Callback {
        void onCall();
    }
}
