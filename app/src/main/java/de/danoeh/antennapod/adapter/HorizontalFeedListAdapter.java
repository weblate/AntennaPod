package de.danoeh.antennapod.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.model.feed.Feed;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class HorizontalFeedListAdapter extends RecyclerView.Adapter<HorizontalFeedListAdapter.Holder> {
    private final WeakReference<MainActivity> mainActivityRef;
    private final List<Feed> data = new ArrayList<>();
    private int dummyViews = 0;

    public HorizontalFeedListAdapter(MainActivity mainActivity) {
        this.mainActivityRef = new WeakReference<>(mainActivity);
    }

    public void setDummyViews(int dummyViews) {
        this.dummyViews = dummyViews;
    }

    public void updateData(List<Feed> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = View.inflate(mainActivityRef.get(), R.layout.horizontal_feed_item, null);
        return new Holder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

    }

    @Override
    public long getItemId(int position) {
        if (position >= data.size()) {
            return RecyclerView.NO_ID; // Dummy views
        }
        return data.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return dummyViews + data.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
