package de.danoeh.antennapod.core.util;

import androidx.annotation.StringRes;
import de.danoeh.antennapod.core.BuildConfig;
import de.danoeh.antennapod.core.R;
import de.danoeh.antennapod.model.download.DownloadError;

/**
 * Provides user-visible labels for download errors.
 */
public class DownloadErrorLabel {

    @StringRes
    public static int from(DownloadError error) {
            return R.string.app_name;
    }
}
