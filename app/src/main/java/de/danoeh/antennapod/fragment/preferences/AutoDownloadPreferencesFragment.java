package de.danoeh.antennapod.fragment.preferences;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.PreferenceActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AutoDownloadPreferencesFragment extends PreferenceFragmentCompat {
    private static final String TAG = "AutoDnldPrefFragment";

    private CheckBoxPreference[] selectedNetworks;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_autodownload);

        setupAutoDownloadScreen();
        buildAutodownloadSelectedNetworksPreference();
        buildEpisodeCleanupPreference();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((PreferenceActivity) getActivity()).getSupportActionBar().setTitle(R.string.pref_automatic_download_title);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setupAutoDownloadScreen() {

    }


    private static String blankIfNull(String val) {
        return val == null ? "" : val;
    }

    @SuppressLint("MissingPermission") // getConfiguredNetworks needs location permission starting with API 29
    private void buildAutodownloadSelectedNetworksPreference() {
        if (Build.VERSION.SDK_INT >= 29) {
            return;
        }

    }

    private void clearAutodownloadSelectedNetworsPreference() {
        if (selectedNetworks != null) {
            PreferenceScreen prefScreen = getPreferenceScreen();

            for (CheckBoxPreference network : selectedNetworks) {
                if (network != null) {
                    prefScreen.removePreference(network);
                }
            }
        }
    }

    private void buildEpisodeCleanupPreference() {
        final Resources res = getActivity().getResources();


    }

    private void setSelectedNetworksEnabled(boolean b) {
        if (selectedNetworks != null) {
            for (Preference p : selectedNetworks) {
                p.setEnabled(b);
            }
        }
    }
}
