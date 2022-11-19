package de.danoeh.antennapod.activity;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import de.danoeh.antennapod.core.preferences.ThemeSwitcher;
import de.danoeh.antennapod.databinding.SettingsActivityBinding;
import de.danoeh.antennapod.fragment.preferences.SwipePreferencesFragment;

/**
 * PreferenceActivity for API 11+. In order to change the behavior of the preference UI, see
 * PreferenceController.
 */
public class PreferenceActivity extends AppCompatActivity {
    private static final String FRAGMENT_TAG = "tag_preferences";
    private SettingsActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeSwitcher.getTheme(this));
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

            getSupportFragmentManager().beginTransaction()
                    .replace(binding.settingsContainer.getId(), new SwipePreferencesFragment(), FRAGMENT_TAG)
                    .commit();
    }
}
