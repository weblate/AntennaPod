package de.danoeh.antennapod.core.storage;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;

/**
 * Implementation of the EpisodeCleanupAlgorithm interface used by AntennaPod.
 */
public class APCleanupAlgorithm extends EpisodeCleanupAlgorithm {

    private static final String TAG = "APCleanupAlgorithm";
    /** the number of days after playback to wait before an item is eligible to be cleaned up.
        Fractional for number of hours, e.g., 0.5 = 12 hours, 0.0416 = 1 hour.  */
    private final int numberOfHoursAfterPlayback;

    public APCleanupAlgorithm(int numberOfHoursAfterPlayback) {
        this.numberOfHoursAfterPlayback = numberOfHoursAfterPlayback;
    }

    /**
     * @return the number of episodes that *could* be cleaned up, if needed
     */
    public int getReclaimableItems()
    {
        return getCandidates().size();
    }

    @Override
    public int performCleanup(Context context, int numberOfEpisodesToDelete) {
        List<FeedItem> candidates = getCandidates();

        return 0;
    }

    @VisibleForTesting
    Date calcMostRecentDateForDeletion(@NonNull Date currentDate) {
        return minusHours(currentDate, numberOfHoursAfterPlayback);
    }

    @NonNull
    private List<FeedItem> getCandidates() {
        List<FeedItem> candidates = new ArrayList<>();

        return candidates;
    }

    @Override
    public int getDefaultCleanupParameter() {
        return getNumEpisodesToCleanup(0);
    }

    @VisibleForTesting
    public int getNumberOfHoursAfterPlayback() { return numberOfHoursAfterPlayback; }

    private static Date minusHours(Date baseDate, int numberOfHours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate);

        cal.add(Calendar.HOUR_OF_DAY, -1 * numberOfHours);

        return cal.getTime();
    }

}
