package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.model.playback.MediaType;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.model.playback.RemoteMedia;
import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.core.util.DateFormatter;
import de.danoeh.antennapod.model.playback.Playable;
import de.danoeh.antennapod.core.util.playback.PlaybackServiceStarter;
import de.danoeh.antennapod.core.util.syndication.HtmlToPlainText;
import de.danoeh.antennapod.dialog.StreamingConfirmationDialog;

import java.util.List;

/**
 * List adapter for showing a list of FeedItems with their title and description.
 */
public class FeedItemlistDescriptionAdapter extends ArrayAdapter<FeedItem> {
    private static final int MAX_LINES_COLLAPSED = 3;

    public FeedItemlistDescriptionAdapter(Context context, int resource, List<FeedItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Holder holder;


        return convertView;
    }

    static class Holder {
        TextView title;
        TextView pubDate;
        TextView description;
        Button preview;
    }
}
