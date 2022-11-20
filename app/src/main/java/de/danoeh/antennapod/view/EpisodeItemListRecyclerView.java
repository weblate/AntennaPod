package de.danoeh.antennapod.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.danoeh.antennapod.R;
import io.reactivex.annotations.Nullable;

public class EpisodeItemListRecyclerView extends RecyclerView {
    public EpisodeItemListRecyclerView(Context context) {
        super(context);
    }

    public EpisodeItemListRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EpisodeItemListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int horizontalSpacing = (int) getResources().getDimension(R.dimen.additional_horizontal_spacing);
        setPadding(horizontalSpacing, getPaddingTop(), horizontalSpacing, getPaddingBottom());
    }
}
