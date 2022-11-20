package de.danoeh.antennapod.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import de.danoeh.antennapod.R;

public class PreferenceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        findViewById(R.id.settingsContainer).setBackground(AnimatedVectorDrawableCompat.create(this, R.drawable.ic_animate_pause_play));
        int horizontalSpacing = (int) getResources().getDimension(R.dimen.additional_horizontal_spacing);
        findViewById(R.id.settingsContainer).setPadding(horizontalSpacing, 0, horizontalSpacing, 0);

        Toast.makeText(this, R.string.this_string_is_used, Toast.LENGTH_SHORT).show();
    }
}
