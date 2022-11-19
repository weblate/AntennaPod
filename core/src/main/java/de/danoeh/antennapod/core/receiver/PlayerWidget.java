package de.danoeh.antennapod.core.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;

public class PlayerWidget extends AppWidgetProvider {
    private static final String TAG = "PlayerWidget";
    public static final String PREFS_NAME = "PlayerWidgetPrefs";
    private static final String KEY_WORKAROUND_ENABLED = "WorkaroundEnabled";
    private static final String KEY_ENABLED = "WidgetEnabled";
    public static final String KEY_WIDGET_COLOR = "widget_color";
    public static final String KEY_WIDGET_PLAYBACK_SPEED = "widget_playback_speed";
    public static final String KEY_WIDGET_SKIP = "widget_skip";
    public static final String KEY_WIDGET_FAST_FORWARD = "widget_fast_forward";
    public static final String KEY_WIDGET_REWIND = "widget_rewind";
    public static final int DEFAULT_COLOR = 0x00262C31;
    private static final String WORKAROUND_WORK_NAME = "WidgetUpdaterWorkaround";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "Widget enabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        super.onDeleted(context, appWidgetIds);
    }

}
