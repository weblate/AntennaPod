package de.danoeh.antennapod.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import de.danoeh.antennapod.model.download.DownloadStatus;
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
