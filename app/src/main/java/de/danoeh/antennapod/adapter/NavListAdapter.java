package de.danoeh.antennapod.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.ContextMenu;
import android.view.InputDevice;
import android.view.LayoutInflater;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.joanzapata.iconify.Iconify;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.PreferenceActivity;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.core.storage.NavDrawerData;
import de.danoeh.antennapod.fragment.NavDrawerFragment;
import de.danoeh.antennapod.ui.home.HomeFragment;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * BaseAdapter for the navigation drawer
 */
public class NavListAdapter extends RecyclerView.Adapter<NavListAdapter.Holder>
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int VIEW_TYPE_NAV = 0;
    public static final int VIEW_TYPE_SECTION_DIVIDER = 1;
    private static final int VIEW_TYPE_SUBSCRIPTION = 2;

    /**
     * a tag used as a placeholder to indicate if the subscription list should be displayed or not
     * This tag doesn't correspond to any specific activity.
     */
    public static final String SUBSCRIPTION_LIST_TAG = "SubscriptionList";

    private final List<String> fragmentTags = new ArrayList<>();
    private final String[] titles;
    private final ItemAccess itemAccess;
    private final WeakReference<Activity> activity;
    public boolean showSubscriptionList = true;

    public NavListAdapter(ItemAccess itemAccess, Activity context) {
        this.itemAccess = itemAccess;
        this.activity = new WeakReference<>(context);

        titles = context.getResources().getStringArray(R.array.nav_drawer_titles);
        loadItems();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private void loadItems() {

    }

    public String getLabel(String tag) {
        int index = ArrayUtils.indexOf(NavDrawerFragment.NAV_DRAWER_TAGS, tag);
        return titles[index];
    }

    private @DrawableRes int getDrawable(String tag) {
        switch (tag) {
            case HomeFragment.TAG:
                return R.drawable.ic_home;
            default:
                return 0;
        }
    }

    public List<String> getFragmentTags() {
        return Collections.unmodifiableList(fragmentTags);
    }

    @Override
    public int getItemCount() {
        int baseCount = getSubscriptionOffset();
        if (showSubscriptionList) {
            baseCount += itemAccess.getCount();
        }
        return baseCount;
    }

    @Override
    public long getItemId(int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_SUBSCRIPTION) {
            return itemAccess.getItem(position - getSubscriptionOffset()).id;
        } else if (viewType == VIEW_TYPE_NAV) {
            return -Math.abs((long) fragmentTags.get(position).hashCode()) - 1; // Folder IDs are >0
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (0 <= position && position < fragmentTags.size()) {
            return VIEW_TYPE_NAV;
        } else if (position < getSubscriptionOffset()) {
            return VIEW_TYPE_SECTION_DIVIDER;
        } else {
            return VIEW_TYPE_SUBSCRIPTION;
        }
    }

    public int getSubscriptionOffset() {
        return fragmentTags.size() > 0 ? fragmentTags.size() + 1 : 0;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity.get());
        if (viewType == VIEW_TYPE_NAV) {
            return new NavHolder(inflater.inflate(R.layout.nav_listitem, parent, false));
        } else if (viewType == VIEW_TYPE_SECTION_DIVIDER) {
            return new DividerHolder(inflater.inflate(R.layout.nav_section_item, parent, false));
        } else {
            return new FeedHolder(inflater.inflate(R.layout.nav_listitem, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        int viewType = getItemViewType(position);

        holder.itemView.setOnCreateContextMenuListener(null);
        if (viewType == VIEW_TYPE_NAV) {
            bindNavView(getLabel(fragmentTags.get(position)), position, (NavHolder) holder);
        } else if (viewType == VIEW_TYPE_SECTION_DIVIDER) {
            bindSectionDivider((DividerHolder) holder);
        } else {
            int itemPos = position - getSubscriptionOffset();
            NavDrawerData.DrawerItem item = itemAccess.getItem(itemPos);
            bindListItem(item, (FeedHolder) holder);
            if (item.type == NavDrawerData.DrawerItem.Type.FEED) {
                bindFeedView((NavDrawerData.FeedDrawerItem) item, (FeedHolder) holder);
            } else {
                bindTagView((NavDrawerData.TagDrawerItem) item, (FeedHolder) holder);
            }
            holder.itemView.setOnCreateContextMenuListener(itemAccess);
        }
        if (viewType != VIEW_TYPE_SECTION_DIVIDER) {
            holder.itemView.setSelected(itemAccess.isSelected(position));
            holder.itemView.setOnClickListener(v -> itemAccess.onItemClick(position));
            holder.itemView.setOnLongClickListener(v -> itemAccess.onItemLongClick(position));
            holder.itemView.setOnTouchListener((v, e) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (e.isFromSource(InputDevice.SOURCE_MOUSE)
                            && e.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        itemAccess.onItemLongClick(position);
                        return false;
                    }
                }
                return false;
            });
        }
    }

    private void bindNavView(String title, int position, NavHolder holder) {

    }

    private void bindSectionDivider(DividerHolder holder) {
        Activity context = activity.get();
        if (context == null) {
            return;
        }


    }

    private void bindListItem(NavDrawerData.DrawerItem item, FeedHolder holder) {
        if (item.getCounter() > 0) {
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(NumberFormat.getInstance().format(item.getCounter()));
        } else {
            holder.count.setVisibility(View.GONE);
        }
        holder.title.setText(item.getTitle());
        int padding = (int) (activity.get().getResources().getDimension(R.dimen.thumbnail_length_navlist) / 2);
        holder.itemView.setPadding(item.getLayer() * padding, 0, 0, 0);
    }

    private void bindFeedView(NavDrawerData.FeedDrawerItem drawerItem, FeedHolder holder) {
        Feed feed = drawerItem.feed;
        Activity context = activity.get();
        if (context == null) {
            return;
        }

        Glide.with(context)
                .load(feed.getImageUrl())
                .apply(new RequestOptions()
                    .placeholder(R.color.light_gray)
                    .error(R.color.light_gray)
                    .transform(new FitCenter(),
                            new RoundedCorners((int) (4 * context.getResources().getDisplayMetrics().density)))
                    .dontAnimate())
                .into(holder.image);

        if (feed.hasLastUpdateFailed()) {
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) holder.title.getLayoutParams();
            p.addRule(RelativeLayout.LEFT_OF, R.id.itxtvFailure);
            holder.failure.setVisibility(View.VISIBLE);
        } else {
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) holder.title.getLayoutParams();
            p.addRule(RelativeLayout.LEFT_OF, R.id.txtvCount);
            holder.failure.setVisibility(View.GONE);
        }
    }

    private void bindTagView(NavDrawerData.TagDrawerItem tag, FeedHolder holder) {
        Activity context = activity.get();
        if (context == null) {
            return;
        }
        if (tag.isOpen) {
            holder.count.setVisibility(View.GONE);
        }
        Glide.with(context).clear(holder.image);
        holder.image.setImageResource(R.drawable.ic_tag);
        holder.failure.setVisibility(View.GONE);
    }

    static class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class DividerHolder extends Holder {
        final TextView feedsFilteredMsg;

        public DividerHolder(@NonNull View itemView) {
            super(itemView);
            feedsFilteredMsg = itemView.findViewById(R.id.nav_feeds_filtered_message);
        }
    }

    static class NavHolder extends Holder {
        final ImageView image;
        final TextView title;
        final TextView count;

        public NavHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgvCover);
            title = itemView.findViewById(R.id.txtvTitle);
            count = itemView.findViewById(R.id.txtvCount);
        }
    }

    static class FeedHolder extends Holder {
        final ImageView image;
        final TextView title;
        final ImageView failure;
        final TextView count;

        public FeedHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgvCover);
            title = itemView.findViewById(R.id.txtvTitle);
            failure = itemView.findViewById(R.id.itxtvFailure);
            count = itemView.findViewById(R.id.txtvCount);
        }
    }

    public interface ItemAccess extends View.OnCreateContextMenuListener {
        int getCount();

        NavDrawerData.DrawerItem getItem(int position);

        boolean isSelected(int position);

        int getQueueSize();

        int getNumberOfNewItems();

        int getNumberOfDownloadedItems();

        int getReclaimableItems();

        int getFeedCounterSum();

        void onItemClick(int position);

        boolean onItemLongClick(int position);

        @Override
        void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo);
    }

}
