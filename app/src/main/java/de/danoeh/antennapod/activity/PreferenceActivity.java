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
        findViewById(android.R.id.content).setBackground(AnimatedVectorDrawableCompat.create(this, R.drawable.ic_animate_pause_play));
        Toast.makeText(this, R.string.this_string_is_used, Toast.LENGTH_SHORT).show();
    }
}
