package de.danoeh.antennapod.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.databinding.SettingsActivityBinding;

public class PreferenceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsActivityBinding binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .replace(binding.settingsContainer.getId(), new SwipePreferencesFragment(), null)
                .commit();
        binding.getRoot().setBackground(AnimatedVectorDrawableCompat.create(this, R.drawable.ic_animate_pause_play));
        int horizontalSpacing = (int) getResources().getDimension(R.dimen.additional_horizontal_spacing);
        binding.getRoot().setPadding(horizontalSpacing, 0, horizontalSpacing, 0);
    }

    public static class SwipePreferencesFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences_swipe);
        }
    }
}
