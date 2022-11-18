package de.danoeh.antennapod.view.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.CoverLoader;
import de.danoeh.antennapod.core.util.PlaybackStatus;
import de.danoeh.antennapod.event.playback.PlaybackPositionEvent;
import de.danoeh.antennapod.model.feed.FeedItem;

public class HorizontalItemViewHolder extends RecyclerView.ViewHolder {
    public final View card;
    public final ImageView secondaryActionIcon;
    private final TextView title;
    private final TextView date;
    private final ProgressBar progressBar;

    private final MainActivity activity;
    private FeedItem item;

    public HorizontalItemViewHolder(MainActivity activity, ViewGroup parent) {
        super(LayoutInflater.from(activity).inflate(R.layout.horizontal_itemlist_item, parent, false));
        this.activity = activity;

        card = itemView.findViewById(R.id.card);
        title = itemView.findViewById(R.id.titleLabel);
        date = itemView.findViewById(R.id.dateLabel);
        secondaryActionIcon = itemView.findViewById(R.id.secondaryActionIcon);
        progressBar = itemView.findViewById(R.id.progressBar);
        itemView.setTag(this);
    }

    public void bind(FeedItem item) {
        this.item = item;

    }

    public void bindDummy() {
        card.setAlpha(0.1f);
        new CoverLoader(activity)
                .withResource(android.R.color.transparent)
                .load();
        title.setText("████ █████");
        date.setText("███");
        secondaryActionIcon.setImageDrawable(null);
        progressBar.setProgress(50);
    }

    public boolean isCurrentlyPlayingItem() {
        return item != null && item.getMedia() != null && PlaybackStatus.isCurrentlyPlaying(item.getMedia());
    }

    public void notifyPlaybackPositionUpdated(PlaybackPositionEvent event) {
        progressBar.setProgress((int) (100.0 * event.getPosition() / event.getDuration()));
    }
}
