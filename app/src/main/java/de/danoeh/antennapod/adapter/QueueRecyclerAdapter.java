package de.danoeh.antennapod.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.fragment.swipeactions.SwipeActions;
import de.danoeh.antennapod.view.viewholder.EpisodeItemViewHolder;

/**
 * List adapter for the queue.
 */
public class QueueRecyclerAdapter extends EpisodeItemListAdapter {
    private static final String TAG = "QueueRecyclerAdapter";

    private final SwipeActions swipeActions;
    private boolean dragDropEnabled;


    public QueueRecyclerAdapter(MainActivity mainActivity, SwipeActions swipeActions) {
        super(mainActivity);
        this.swipeActions = swipeActions;
    }

    public void updateDragDropEnabled() {
        notifyDataSetChanged();
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void afterBindViewHolder(EpisodeItemViewHolder holder, int pos) {
        if (!dragDropEnabled || inActionMode()) {
            holder.dragHandle.setVisibility(View.GONE);
            holder.dragHandle.setOnTouchListener(null);
            holder.coverHolder.setOnTouchListener(null);
        } else {
            holder.dragHandle.setVisibility(View.VISIBLE);
            holder.dragHandle.setOnTouchListener((v1, event) -> {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "startDrag()");
                    swipeActions.startDrag(holder);
                }
                return false;
            });
            holder.coverHolder.setOnTouchListener((v1, event) -> {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    boolean isLtr = holder.itemView.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR;
                    float factor = isLtr ? 1 : -1;
                    if (factor * event.getX() < factor * 0.5 * v1.getWidth()) {
                        Log.d(TAG, "startDrag()");
                        swipeActions.startDrag(holder);
                    } else {
                        Log.d(TAG, "Ignoring drag in right half of the image");
                    }
                }
                return false;
            });
        }

        holder.isInQueue.setVisibility(View.GONE);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        super.onCreateContextMenu(menu, v, menuInfo);


    }
}
