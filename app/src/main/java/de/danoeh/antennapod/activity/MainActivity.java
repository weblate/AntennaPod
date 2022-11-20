package de.danoeh.antennapod.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.receiver.MediaButtonReceiver;
import de.danoeh.antennapod.event.MessageEvent;
import de.danoeh.antennapod.ui.home.HomeFragment;
import de.danoeh.antennapod.view.LockableBottomSheetBehavior;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * The activity that is shown when the user launches the app.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String MAIN_FRAGMENT_TAG = "main";

    public static final String PREF_NAME = "MainActivityPrefs";
    public static final String PREF_IS_FIRST_LAUNCH = "prefMainActivityIsFirstLaunch";

    public static final String EXTRA_FRAGMENT_TAG = "fragment_tag";
    public static final String EXTRA_FRAGMENT_ARGS = "fragment_args";
    public static final String EXTRA_FEED_ID = "fragment_feed_id";
    public static final String EXTRA_REFRESH_ON_START = "refresh_on_start";
    public static final String EXTRA_STARTED_FROM_SEARCH = "started_from_search";
    public static final String EXTRA_ADD_TO_BACK_STACK = "add_to_back_stack";
    public static final String KEY_GENERATED_VIEW_ID = "generated_view_id";

    private @Nullable DrawerLayout drawerLayout;
    private @Nullable ActionBarDrawerToggle drawerToggle;
    private View navDrawer;
    private LockableBottomSheetBehavior sheetBehavior;
    private RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
    private int lastTheme = 0;

    @NonNull
    public static Intent getIntentToOpenFeed(@NonNull Context context, long feedId) {
        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_FEED_ID, feedId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(lastTheme);
        if (savedInstanceState != null) {
            ensureGeneratedViewIdGreaterThan(savedInstanceState.getInt(KEY_GENERATED_VIEW_ID, 0));
        }
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        recycledViewPool.setMaxRecycledViews(R.id.view_type_episode_item, 25);

        drawerLayout = findViewById(R.id.drawer_layout);
        navDrawer = findViewById(R.id.navDrawerFragment);
        setNavDrawerSize();

        final FragmentManager fm = getSupportFragmentManager();

        checkFirstLaunch();
        View bottomSheet = findViewById(R.id.audioplayerFragment);
        sheetBehavior = (LockableBottomSheetBehavior) BottomSheetBehavior.from(bottomSheet);
        sheetBehavior.setHideable(false);
        sheetBehavior.setBottomSheetCallback(bottomSheetCallback);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        int playerHeight = (int) getResources().getDimension(R.dimen.external_player_height);
        sheetBehavior.setPeekHeight(playerHeight + getBottomInset());
    }

    /**
     * View.generateViewId stores the current ID in a static variable.
     * When the process is killed, the variable gets reset.
     * This makes sure that we do not get ID collisions
     * and therefore errors when trying to restore state from another view.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    private void ensureGeneratedViewIdGreaterThan(int minimum) {
        while (View.generateViewId() <= minimum) {
            // Generate new IDs
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_GENERATED_VIEW_ID, View.generateViewId());
    }

    private final BottomSheetBehavior.BottomSheetCallback bottomSheetCallback =
            new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View view, int state) {
            if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                onSlide(view, 0.0f);
            } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
                onSlide(view, 1.0f);
            }
        }

        @Override
        public void onSlide(@NonNull View view, float slideOffset) {

        }
    };

    public void setupToolbarToggle(@NonNull MaterialToolbar toolbar, boolean displayUpArrow) {
        if (drawerLayout != null) { // Tablet layout does not have a drawer
            if (drawerToggle != null) {
                drawerLayout.removeDrawerListener(drawerToggle);
            }
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                    R.string.drawer_open, R.string.drawer_close);
            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
            drawerToggle.setDrawerIndicatorEnabled(!displayUpArrow);
            drawerToggle.setToolbarNavigationClickListener(v -> getSupportFragmentManager().popBackStack());
        } else if (!displayUpArrow) {
            toolbar.setNavigationIcon(null);
        } else {
            toolbar.setNavigationOnClickListener(v -> getSupportFragmentManager().popBackStack());
        }
    }

    private void checkFirstLaunch() {

    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && navDrawer != null && drawerLayout.isDrawerOpen(navDrawer);
    }

    public LockableBottomSheetBehavior getBottomSheet() {
        return sheetBehavior;
    }

    private int getBottomInset() {
        WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(getWindow().getDecorView());
        return insets == null ? 0 : insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
    }

    public void setPlayerVisible(boolean visible) {
        getBottomSheet().setLocked(!visible);
        if (visible) {
            bottomSheetCallback.onStateChanged(null, getBottomSheet().getState()); // Update toolbar visibility
        } else {
            getBottomSheet().setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        FragmentContainerView mainView = findViewById(R.id.main_view);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainView.getLayoutParams();
        int externalPlayerHeight = (int) getResources().getDimension(R.dimen.external_player_height);
        params.setMargins(0, 0, 0, getBottomInset() + (visible ? externalPlayerHeight : 0));
        mainView.setLayoutParams(params);
        findViewById(R.id.audioplayerFragment).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public RecyclerView.RecycledViewPool getRecycledViewPool() {
        return recycledViewPool;
    }

    public void loadFragment(String tag, Bundle args) {
        Log.d(TAG, "loadFragment(tag: " + tag + ", args: " + args + ")");
        Fragment fragment;
        switch (tag) {
            case HomeFragment.TAG:
                fragment = new HomeFragment();
                break;
            default:
                // default to home screen
                fragment = new HomeFragment();
                tag = HomeFragment.TAG;
                args = null;
                break;
        }

        if (args != null) {
            fragment.setArguments(args);
        }
        loadFragment(fragment);
    }

    public void loadFeedFragmentById(long feedId, Bundle args) {
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // clear back stack
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
        FragmentTransaction t = fragmentManager.beginTransaction();
        t.replace(R.id.main_view, fragment, MAIN_FRAGMENT_TAG);
        fragmentManager.popBackStack();
        // TODO: we have to allow state loss here
        // since this function can get called from an AsyncTask which
        // could be finishing after our app has already committed state
        // and is about to get shutdown.  What we *should* do is
        // not commit anything in an AsyncTask, but that's a bigger
        // change than we want now.
        t.commitAllowingStateLoss();

        if (drawerLayout != null) { // Tablet layout does not have a drawer
            drawerLayout.closeDrawer(navDrawer);
        }
    }

    public void loadChildFragment(Fragment fragment) {
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle != null) { // Tablet layout does not have a drawer
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (drawerToggle != null) { // Tablet layout does not have a drawer
            drawerToggle.onConfigurationChanged(newConfig);
        }
        setNavDrawerSize();
    }

    private void setNavDrawerSize() {
        if (drawerToggle == null) { // Tablet layout does not have a drawer
            return;
        }
        float screenPercent = getResources().getInteger(R.integer.nav_drawer_screen_size_percent) * 0.01f;
        int width = (int) (getScreenWidth() * screenPercent);
        int maxWidth = (int) getResources().getDimension(R.dimen.nav_drawer_max_screen_size);

        navDrawer.getLayoutParams().width = Math.min(width, maxWidth);
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (getBottomSheet().getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetCallback.onSlide(null, 1.0f);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleNavIntent();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) { // Tablet layout does not have a drawer
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        Log.d(TAG, "onEvent(" + event + ")");

        Snackbar snackbar = showSnackbarAbovePlayer(event.message, Snackbar.LENGTH_SHORT);
        if (event.action != null) {
        }
    }

    private void handleNavIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_FEED_ID) || intent.hasExtra(EXTRA_FRAGMENT_TAG) || intent.hasExtra(EXTRA_REFRESH_ON_START)) {
            Log.d(TAG, "handleNavIntent()");
            String tag = intent.getStringExtra(EXTRA_FRAGMENT_TAG);
            Bundle args = intent.getBundleExtra(EXTRA_FRAGMENT_ARGS);
            boolean refreshOnStart = intent.getBooleanExtra(EXTRA_REFRESH_ON_START, false);
            if (refreshOnStart) {
            }

            long feedId = intent.getLongExtra(EXTRA_FEED_ID, 0);
            if (tag != null) {
                loadFragment(tag, args);
            } else if (feedId > 0) {
                boolean startedFromSearch = intent.getBooleanExtra(EXTRA_STARTED_FROM_SEARCH, false);
                boolean addToBackStack = intent.getBooleanExtra(EXTRA_ADD_TO_BACK_STACK, false);
                if (startedFromSearch || addToBackStack) {
                } else {
                    loadFeedFragmentById(feedId, args);
                }
            }
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            handleDeeplink(intent.getData());
        }
        // to avoid handling the intent twice when the configuration changes
        setIntent(new Intent(MainActivity.this, MainActivity.class));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNavIntent();
    }

    public Snackbar showSnackbarAbovePlayer(CharSequence text, int duration) {
        Snackbar s;
        if (getBottomSheet().getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            s = Snackbar.make(findViewById(R.id.main_view), text, duration);
            if (findViewById(R.id.audioplayerFragment).getVisibility() == View.VISIBLE) {
                s.setAnchorView(findViewById(R.id.audioplayerFragment));
            }
        } else {
            s = Snackbar.make(findViewById(android.R.id.content), text, duration);
        }
        s.show();
        return s;
    }

    public Snackbar showSnackbarAbovePlayer(int text, int duration) {
        return showSnackbarAbovePlayer(getResources().getText(text), duration);
    }

    /**
     * Handles the deep link incoming via App Actions.
     * Performs an in-app search or opens the relevant feature of the app
     * depending on the query.
     *
     * @param uri incoming deep link
     */
    private void handleDeeplink(Uri uri) {

    }
  
    //Hardware keyboard support
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        View currentFocus = getCurrentFocus();
        if (currentFocus instanceof EditText) {
            return super.onKeyUp(keyCode, event);
        }

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        Integer customKeyCode = null;
        EventBus.getDefault().post(event);

        switch (keyCode) {
            case KeyEvent.KEYCODE_P:
                customKeyCode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
                break;
            case KeyEvent.KEYCODE_J: //Fallthrough
            case KeyEvent.KEYCODE_A:
            case KeyEvent.KEYCODE_COMMA:
                customKeyCode = KeyEvent.KEYCODE_MEDIA_REWIND;
                break;
            case KeyEvent.KEYCODE_K: //Fallthrough
            case KeyEvent.KEYCODE_D:
            case KeyEvent.KEYCODE_PERIOD:
                customKeyCode = KeyEvent.KEYCODE_MEDIA_FAST_FORWARD;
                break;
            case KeyEvent.KEYCODE_PLUS: //Fallthrough
            case KeyEvent.KEYCODE_W:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_MINUS: //Fallthrough
            case KeyEvent.KEYCODE_S:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_M:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_TOGGLE_MUTE, AudioManager.FLAG_SHOW_UI);
                    return true;
                }
                break;
        }

        if (customKeyCode != null) {
            sendBroadcast(MediaButtonReceiver.createIntent(this, customKeyCode));
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
