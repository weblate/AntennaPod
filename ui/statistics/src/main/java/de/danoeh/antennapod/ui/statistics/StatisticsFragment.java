package de.danoeh.antennapod.ui.statistics;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;

/**
 * Displays the 'statistics' screen
 */
public class StatisticsFragment extends Fragment {
    public static final String TAG = "StatisticsFragment";
    public static final String PREF_NAME = "StatisticsActivityPrefs";
    public static final String PREF_INCLUDE_MARKED_PLAYED = "countAll";
    public static final String PREF_FILTER_FROM = "filterFrom";
    public static final String PREF_FILTER_TO = "filterTo";


    private static final int POS_SUBSCRIPTIONS = 0;
    private static final int POS_YEARS = 1;
    private static final int POS_SPACE_TAKEN = 2;
    private static final int TOTAL_COUNT = 3;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private MaterialToolbar toolbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);


        return container;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void confirmResetStatistics() {

    }

    private void doResetStatistics() {
        getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
                .putBoolean(PREF_INCLUDE_MARKED_PLAYED, false)
                .putLong(PREF_FILTER_FROM, 0)
                .putLong(PREF_FILTER_TO, Long.MAX_VALUE)
                .apply();

    }

}
