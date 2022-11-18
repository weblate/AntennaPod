package de.danoeh.antennapod.fragment.preferences.synchronization;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.fragment.app.DialogFragment;
import de.danoeh.antennapod.R;

import de.danoeh.antennapod.databinding.NextcloudAuthDialogBinding;

/**
 * Guides the user through the authentication process.
 */
public class NextcloudAuthenticationFragment extends DialogFragment
         {
    public static final String TAG = "NextcloudAuthenticationFragment";
    private NextcloudAuthDialogBinding viewBinding;
    private boolean shouldDismiss = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getContext());
        dialog.setTitle(R.string.gpodnetauth_login_butLabel);
        dialog.setNegativeButton(R.string.cancel_label, null);
        dialog.setCancelable(false);
        this.setCancelable(false);



        return dialog.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldDismiss) {
            dismiss();
        }
    }
}
