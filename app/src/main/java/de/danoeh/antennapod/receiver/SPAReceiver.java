package de.danoeh.antennapod.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receives intents from AntennaPod Single Purpose apps
 */
public class SPAReceiver extends BroadcastReceiver{
    private static final String TAG = "SPAReceiver";

    public static final String ACTION_SP_APPS_QUERY_FEEDS = "de.danoeh.antennapdsp.intent.SP_APPS_QUERY_FEEDS";
    private static final String ACTION_SP_APPS_QUERY_FEEDS_REPSONSE = "de.danoeh.antennapdsp.intent.SP_APPS_QUERY_FEEDS_RESPONSE";
    private static final String ACTION_SP_APPS_QUERY_FEEDS_REPSONSE_FEEDS_EXTRA = "feeds";

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
