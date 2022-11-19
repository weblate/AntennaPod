package de.danoeh.antennapod.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import de.danoeh.antennapod.core.preferences.ThemeSwitcher;
import de.danoeh.antennapod.databinding.SettingsActivityBinding;
import de.danoeh.antennapod.fragment.preferences.SwipePreferencesFragment;

public class PreferenceActivity extends AppCompatActivity {
    private static final String FRAGMENT_TAG = "tag_preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeSwitcher.getTheme(this));
        super.onCreate(savedInstanceState);

        SettingsActivityBinding binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .replace(binding.settingsContainer.getId(), new SwipePreferencesFragment(), FRAGMENT_TAG)
                .commit();
    }
}
