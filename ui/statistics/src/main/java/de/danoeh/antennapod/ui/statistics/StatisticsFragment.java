package de.danoeh.antennapod.ui.statistics;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;
import de.danoeh.antennapod.ui.common.PagedToolbarFragment;

/**
 * Displays the 'statistics' screen
 */
public class StatisticsFragment extends PagedToolbarFragment {
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

        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        viewPager = rootView.findViewById(R.id.viewpager);
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.statistics_label));
        toolbar.inflateMenu(R.menu.statistics);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        // Give the TabLayout the ViewPager
        tabLayout = rootView.findViewById(R.id.sliding_tabs);
        super.setupPagedToolbar(toolbar, viewPager);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case POS_SUBSCRIPTIONS:
                    tab.setText(R.string.subscriptions_label);
                    break;
                case POS_YEARS:
                    tab.setText(R.string.years_statistics_label);
                    break;
                case POS_SPACE_TAKEN:
                    tab.setText(R.string.downloads_label);
                    break;
                default:
                    break;
            }
        }).attach();
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.statistics_reset) {
            confirmResetStatistics();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmResetStatistics() {
        ConfirmationDialog conDialog = new ConfirmationDialog(
                getActivity(),
                R.string.statistics_reset_data,
                R.string.statistics_reset_data_msg) {

            @Override
            public void onConfirmButtonPressed(DialogInterface dialog) {
                dialog.dismiss();
                doResetStatistics();
            }
        };
        conDialog.createNewDialog().show();
    }

    private void doResetStatistics() {
        getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
                .putBoolean(PREF_INCLUDE_MARKED_PLAYED, false)
                .putLong(PREF_FILTER_FROM, 0)
                .putLong(PREF_FILTER_TO, Long.MAX_VALUE)
                .apply();

    }

}
