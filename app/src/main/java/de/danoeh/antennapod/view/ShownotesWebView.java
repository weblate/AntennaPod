package de.danoeh.antennapod.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import androidx.core.util.Consumer;

public class ShownotesWebView extends WebView implements View.OnLongClickListener {
    private static final String TAG = "ShownotesWebView";

    /**
     * URL that was selected via long-press.
     */
    private String selectedUrl;
    private Consumer<Integer> timecodeSelectedListener;
    private Runnable pageFinishedListener;

    public ShownotesWebView(Context context) {
        super(context);
        setup();
    }

    public ShownotesWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public ShownotesWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {

    }

    @Override
    public boolean onLongClick(View v) {

        return false;
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (selectedUrl == null) {
            return false;
        }


        return true;
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
        if (selectedUrl == null) {
            return;
        }

    }

    public void setTimecodeSelectedListener(Consumer<Integer> timecodeSelectedListener) {
        this.timecodeSelectedListener = timecodeSelectedListener;
    }

    public void setPageFinishedListener(Runnable pageFinishedListener) {
        this.pageFinishedListener = pageFinishedListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(Math.max(getMeasuredWidth(), getMinimumWidth()),
                Math.max(getMeasuredHeight(), getMinimumHeight()));
    }
}
