package de.danoeh.antennapod.activity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.preferences.ThemeSwitcher;
import de.danoeh.antennapod.core.receiver.PlayerWidget;
import de.danoeh.antennapod.core.widget.WidgetUpdaterWorker;

public class WidgetConfigActivity extends AppCompatActivity {
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private SeekBar opacitySeekBar;
    private TextView opacityTextView;
    private View widgetPreview;
    private CheckBox ckPlaybackSpeed;
    private CheckBox ckRewind;
    private CheckBox ckFastForward;
    private CheckBox ckSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeSwitcher.getTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

    }

    private void displayPreviewPanel() {

    }

    private void confirmCreateWidget() {

    }

    private int getColorWithAlpha(int color, int opacity) {
        return (int) Math.round(0xFF * (0.01 * opacity)) * 0x1000000 + color;
    }
}
