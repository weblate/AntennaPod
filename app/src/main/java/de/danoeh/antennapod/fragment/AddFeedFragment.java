package de.danoeh.antennapod.fragment;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.GetContent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.activity.OpmlImportActivity;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.model.feed.SortOrder;
import de.danoeh.antennapod.databinding.AddfeedBinding;
import de.danoeh.antennapod.databinding.EditTextDialogBinding;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.Collections;

/**
 * Provides actions for adding new podcast subscriptions.
 */
public class AddFeedFragment extends Fragment {

    public static final String TAG = "AddFeedFragment";
    private static final String KEY_UP_ARROW = "up_arrow";

    private AddfeedBinding viewBinding;
    private MainActivity activity;
    private boolean displayUpArrow;

    private final ActivityResultLauncher<String> chooseOpmlImportPathLauncher =
            registerForActivityResult(new GetContent(), this::chooseOpmlImportPathResult);
    private final ActivityResultLauncher<Uri> addLocalFolderLauncher =
            registerForActivityResult(new AddLocalFolder(), this::addLocalFolderResult);

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        viewBinding = AddfeedBinding.inflate(inflater);
        activity = (MainActivity) getActivity();

        displayUpArrow = getParentFragmentManager().getBackStackEntryCount() != 0;
        if (savedInstanceState != null) {
            displayUpArrow = savedInstanceState.getBoolean(KEY_UP_ARROW);
        }
        ((MainActivity) getActivity()).setupToolbarToggle(viewBinding.toolbar, displayUpArrow);


        viewBinding.combinedFeedSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });

        viewBinding.addViaUrlButton.setOnClickListener(v
                -> showAddViaUrlDialog());

        viewBinding.opmlImportButton.setOnClickListener(v -> {
            try {
                chooseOpmlImportPathLauncher.launch("*/*");
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                ((MainActivity) getActivity())
                        .showSnackbarAbovePlayer(R.string.unable_to_start_system_file_manager, Snackbar.LENGTH_LONG);
            }
        });

        viewBinding.addLocalFolderButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < 21) {
                return;
            }
            try {
                addLocalFolderLauncher.launch(null);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                ((MainActivity) getActivity())
                        .showSnackbarAbovePlayer(R.string.unable_to_start_system_file_manager, Snackbar.LENGTH_LONG);
            }
        });
        if (Build.VERSION.SDK_INT < 21) {
            viewBinding.addLocalFolderButton.setVisibility(View.GONE);
        }

        viewBinding.searchButton.setOnClickListener(view -> performSearch());

        return viewBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(KEY_UP_ARROW, displayUpArrow);
        super.onSaveInstanceState(outState);
    }

    private void showAddViaUrlDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle(R.string.add_podcast_by_url);
        final EditTextDialogBinding dialogBinding = EditTextDialogBinding.inflate(getLayoutInflater());
        dialogBinding.urlEditText.setHint(R.string.add_podcast_by_url_hint);

        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0 && clipData.getItemAt(0).getText() != null) {
            final String clipboardContent = clipData.getItemAt(0).getText().toString();
            if (clipboardContent.trim().startsWith("http")) {
                dialogBinding.urlEditText.setText(clipboardContent.trim());
            }
        }
        builder.setView(dialogBinding.getRoot());
        builder.setPositiveButton(R.string.confirm_label,
                (dialog, which) -> addUrl(dialogBinding.urlEditText.getText().toString()));
        builder.setNegativeButton(R.string.cancel_label, null);
        builder.show();
    }

    private void addUrl(String url) {

    }

    private void performSearch() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void chooseOpmlImportPathResult(final Uri uri) {
        if (uri == null) {
            return;
        }
        final Intent intent = new Intent(getContext(), OpmlImportActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }

    private void addLocalFolderResult(final Uri uri) {
        if (uri == null) {
            return;
        }

    }



    private static class AddLocalFolder extends ActivityResultContracts.OpenDocumentTree {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @NonNull
        @Override
        public Intent createIntent(@NonNull final Context context, @Nullable final Uri input) {
            return super.createIntent(context, input)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }
}
