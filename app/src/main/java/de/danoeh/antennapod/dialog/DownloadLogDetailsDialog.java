package de.danoeh.antennapod.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.model.download.DownloadStatus;
import de.danoeh.antennapod.event.MessageEvent;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.model.feed.FeedMedia;
import org.greenrobot.eventbus.EventBus;

public class DownloadLogDetailsDialog extends MaterialAlertDialogBuilder {

    public DownloadLogDetailsDialog(@NonNull Context context, DownloadStatus status) {
        super(context);

        String url = "unknown";
        String message = context.getString(R.string.download_successful);

        if (!status.isSuccessful()) {
            message = status.getReasonDetailed();
        }

        String messageFull = context.getString(R.string.download_error_details_message, message, url);
        setTitle(R.string.download_error_details);
        setMessage(messageFull);
        setPositiveButton(android.R.string.ok, null);
        setNeutralButton(R.string.copy_to_clipboard, (dialog, which) -> {
            ClipboardManager clipboard = (ClipboardManager) getContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(context.getString(R.string.download_error_details), messageFull);
            clipboard.setPrimaryClip(clip);
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.copied_to_clipboard)));
        });
    }

    @Override
    public AlertDialog show() {
        AlertDialog dialog = super.show();
        ((TextView) dialog.findViewById(android.R.id.message)).setTextIsSelectable(true);
        return dialog;
    }
}
