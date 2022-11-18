package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.dialog.FeedPreferenceSkipDialog;
import de.danoeh.antennapod.event.settings.SkipIntroEndingChangedEvent;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.model.feed.FeedPreferences;
import io.reactivex.disposables.Disposable;
import org.greenrobot.eventbus.EventBus;

public class FeedSettingsFragment extends Fragment {
    private static final String TAG = "FeedSettingsFragment";
    private static final String EXTRA_FEED_ID = "de.danoeh.antennapod.extra.feedId";

    private Disposable disposable;

    public static FeedSettingsFragment newInstance(Feed feed) {
        FeedSettingsFragment fragment = new FeedSettingsFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(EXTRA_FEED_ID, feed.getId());
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.feedsettings, container, false);
        long feedId = getArguments().getLong(EXTRA_FEED_ID);


        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public static class FeedSettingsPreferenceFragment extends PreferenceFragmentCompat {
        private static final CharSequence PREF_EPISODE_FILTER = "episodeFilter";
        private static final CharSequence PREF_SCREEN = "feedSettingsScreen";
        private static final CharSequence PREF_AUTHENTICATION = "authentication";
        private static final CharSequence PREF_AUTO_DELETE = "autoDelete";
        private static final CharSequence PREF_CATEGORY_AUTO_DOWNLOAD = "autoDownloadCategory";
        private static final String PREF_FEED_PLAYBACK_SPEED = "feedPlaybackSpeed";
        private static final String PREF_AUTO_SKIP = "feedAutoSkip";
        private static final String PREF_TAGS = "tags";

        private Feed feed;
        private Disposable disposable;
        private FeedPreferences feedPreferences;

        public static FeedSettingsPreferenceFragment newInstance(long feedId) {
            FeedSettingsPreferenceFragment fragment = new FeedSettingsPreferenceFragment();
            Bundle arguments = new Bundle();
            arguments.putLong(EXTRA_FEED_ID, feedId);
            fragment.setArguments(arguments);
            return fragment;
        }

        @Override
        public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
            final RecyclerView view = super.onCreateRecyclerView(inflater, parent, state);
            // To prevent transition animation because of summary update
            view.setItemAnimator(null);
            view.setLayoutAnimation(null);
            return view;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.feed_settings);
            // To prevent displaying partially loaded data
            findPreference(PREF_SCREEN).setVisible(false);


        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (disposable != null) {
                disposable.dispose();
            }
        }

        private void setupFeedAutoSkipPreference() {
            findPreference(PREF_AUTO_SKIP).setOnPreferenceClickListener(preference -> {
                new FeedPreferenceSkipDialog(getContext(),
                        feedPreferences.getFeedSkipIntro(),
                        feedPreferences.getFeedSkipEnding()) {
                    @Override
                    protected void onConfirmed(int skipIntro, int skipEnding) {
                        feedPreferences.setFeedSkipIntro(skipIntro);
                        feedPreferences.setFeedSkipEnding(skipEnding);
                        EventBus.getDefault().post(
                                new SkipIntroEndingChangedEvent(feedPreferences.getFeedSkipIntro(),
                                        feedPreferences.getFeedSkipEnding(),
                                        feed.getId()));
                    }
                }.show();
                return false;
            });
        }
    }
}
