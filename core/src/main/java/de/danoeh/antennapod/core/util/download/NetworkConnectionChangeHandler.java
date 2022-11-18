package de.danoeh.antennapod.core.util.download;

import android.content.Context;
import com.google.android.exoplayer2.util.Log;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.util.NetworkUtils;

public abstract class NetworkConnectionChangeHandler {
    private static final String TAG = "NetworkConnectionChangeHandler";
    private static Context context;

    public static void init(Context context) {
        NetworkConnectionChangeHandler.context = context;
    }

    public static void networkChangedDetected() {
    }
}
