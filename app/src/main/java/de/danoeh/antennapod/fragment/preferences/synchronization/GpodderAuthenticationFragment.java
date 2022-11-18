package de.danoeh.antennapod.fragment.preferences.synchronization;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ViewFlipper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.util.FileNameGenerator;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Guides the user through the authentication process.
 */
public class GpodderAuthenticationFragment extends DialogFragment {
    public static final String TAG = "GpodnetAuthActivity";

    private ViewFlipper viewFlipper;

    private static final int STEP_DEFAULT = -1;
    private static final int STEP_HOSTNAME = 0;
    private static final int STEP_LOGIN = 1;
    private static final int STEP_DEVICE = 2;
    private static final int STEP_FINISH = 3;

    private int currentStep = -1;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getContext());
        dialog.setTitle(R.string.gpodnetauth_login_butLabel);
        dialog.setNegativeButton(R.string.cancel_label, null);
        dialog.setCancelable(false);
        this.setCancelable(false);

        View root = View.inflate(getContext(), R.layout.gpodnetauth_dialog, null);
        viewFlipper = root.findViewById(R.id.viewflipper);
        advance();
        dialog.setView(root);

        return dialog.create();
    }

    private void setupHostView(View view) {

    }

    private void setupLoginView(View view) {

    }

    private void setupDeviceView(View view) {

    }

    private void createDevice(View view) {

    }

    private String generateDeviceName() {
        String baseName = getString(R.string.gpodnetauth_device_name_default, Build.MODEL);
        String name = baseName;
        int num = 1;
        while (isDeviceInList(name)) {
            name = baseName + " (" + num + ")";
            num++;
        }
        return name;
    }

    private String generateDeviceId(String name) {
        // devices names must be of a certain form:
        // https://gpoddernet.readthedocs.org/en/latest/api/reference/general.html#devices
        return FileNameGenerator.generateFileName(name).replaceAll("\\W", "_").toLowerCase(Locale.US);
    }

    private boolean isDeviceInList(String name) {

        return false;
    }

    private void setupFinishView(View view) {

    }

    private void advance() {

    }

    private boolean usernameHasUnwantedChars(String username) {
        Pattern special = Pattern.compile("[!@#$%&*()+=|<>?{}\\[\\]~]");
        Matcher containsUnwantedChars = special.matcher(username);
        return containsUnwantedChars.find();
    }
}
