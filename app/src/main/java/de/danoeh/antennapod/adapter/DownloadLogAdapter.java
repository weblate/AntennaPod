package de.danoeh.antennapod.adapter;

import android.app.Activity;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import de.danoeh.antennapod.R;

import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.util.DownloadErrorLabel;
import de.danoeh.antennapod.model.download.DownloadError;
import de.danoeh.antennapod.model.download.DownloadStatus;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;
import de.danoeh.antennapod.ui.common.ThemeUtils;
import de.danoeh.antennapod.view.viewholder.DownloadLogItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of DownloadStatus entries.
 */
public class DownloadLogAdapter extends BaseAdapter {
    private static final String TAG = "DownloadLogAdapter";

    private final Activity context;
    private List<DownloadStatus> downloadLog = new ArrayList<>();

    public DownloadLogAdapter(Activity context) {
        super();
        this.context = context;
    }

    public void setDownloadLog(List<DownloadStatus> downloadLog) {
        this.downloadLog = downloadLog;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DownloadLogItemViewHolder holder;
        if (convertView == null) {
            holder = new DownloadLogItemViewHolder(context, parent);
            holder.itemView.setTag(holder);
        } else {
            holder = (DownloadLogItemViewHolder) convertView.getTag();
        }
        return holder.itemView;
    }


    @Override
    public int getCount() {
        return downloadLog.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
