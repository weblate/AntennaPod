package de.danoeh.antennapod.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import de.danoeh.antennapod.databinding.SettingsActivityBinding;
import de.danoeh.antennapod.fragment.preferences.SwipePreferencesFragment;

public class PreferenceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsActivityBinding binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .replace(binding.settingsContainer.getId(), new SwipePreferencesFragment(), null)
                .commit();
    }
}
