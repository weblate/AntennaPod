package de.danoeh.antennapod.ui.echo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;
import de.danoeh.antennapod.storage.preferences.UserPreferences;
import de.danoeh.antennapod.ui.echo.databinding.EchoActivityBinding;
import de.danoeh.antennapod.ui.echo.databinding.EchoBaseBinding;
import de.danoeh.antennapod.ui.echo.databinding.EchoSubscriptionsBinding;
import de.danoeh.antennapod.ui.echo.screens.BubbleScreen;
import de.danoeh.antennapod.ui.echo.screens.FinalShareScreen;
import de.danoeh.antennapod.ui.echo.screens.RotatingSquaresScreen;
import de.danoeh.antennapod.ui.echo.screens.StripesScreen;
import de.danoeh.antennapod.ui.echo.screens.WaveformScreen;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EchoActivity extends AppCompatActivity {
    private static final int NUM_SCREENS = 7;

    private EchoActivityBinding viewBinding;
    private int currentScreen = -1;
    private boolean progressPaused = false;
    private float progress = 0;
    private Drawable currentDrawable;
    private EchoProgress echoProgress;
    private Disposable redrawTimer;
    private long timeTouchDown;
    private long lastFrame;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        viewBinding = EchoActivityBinding.inflate(getLayoutInflater());
        viewBinding.closeButton.setOnClickListener(v -> finish());
        viewBinding.echoImage.setOnTouchListener((v, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                progressPaused = true;
                timeTouchDown = System.currentTimeMillis();
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                progressPaused = false;
                if (timeTouchDown + 500 > System.currentTimeMillis()) {
                    int newScreen = (currentScreen + 1) % NUM_SCREENS;
                    progress = newScreen;
                    echoProgress.setProgress(progress);
                    loadScreen(newScreen);
                }
            }
            return true;
        });
        echoProgress = new EchoProgress(NUM_SCREENS);
        viewBinding.echoProgressImage.setImageDrawable(echoProgress);
        setContentView(viewBinding.getRoot());
        loadScreen(0);
    }

    private void share() {
        try {
            Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            currentDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            currentDrawable.draw(canvas);
            viewBinding.echoImage.setImageDrawable(null);
            viewBinding.echoImage.setImageDrawable(currentDrawable);
            File file = new File(UserPreferences.getDataFolder(null), "AntennaPodEcho.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();

            Uri fileUri = FileProvider.getUriForFile(this, getString(R.string.provider_authority), file);
            new ShareCompat.IntentBuilder(this)
                    .setType("image/png")
                    .addStream(fileUri)
                    .setChooserTitle(R.string.share_file_label)
                    .startChooser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        redrawTimer = Flowable.timer(10, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .repeat()
                .subscribe(i -> {
                    if (!progressPaused && progress < NUM_SCREENS - 0.001f) {
                        long timePassed = System.currentTimeMillis() - lastFrame;
                        lastFrame = System.currentTimeMillis();
                        if (timePassed > 500) {
                            timePassed = 0;
                        }
                        progress = Math.min(NUM_SCREENS - 0.001f, progress + timePassed / 10000.0f);
                        echoProgress.setProgress(progress);
                        viewBinding.echoProgressImage.postInvalidate();
                        loadScreen((int) progress);
                    }
                    viewBinding.echoImage.postInvalidate();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        redrawTimer.dispose();
    }

    private void loadScreen(int screen) {
        if (screen == currentScreen) {
            return;
        }
        currentScreen = screen;
        runOnUiThread(() -> {
            viewBinding.screenContainer.removeAllViews();
            switch (currentScreen) {
                case 0:
                    EchoBaseBinding introBinding = EchoBaseBinding.inflate(getLayoutInflater());
                    introBinding.aboveLabel.setText(R.string.echo_intro_your_year);
                    introBinding.largeLabel.setText(String.format(Locale.getDefault(), "%d", 2023));
                    introBinding.belowLabel.setText(R.string.echo_intro_in_podcasts);
                    introBinding.smallLabel.setText(R.string.echo_intro_locally);
                    introBinding.echoLogo.setVisibility(View.VISIBLE);
                    viewBinding.screenContainer.addView(introBinding.getRoot());
                    currentDrawable = new BubbleScreen();
                    break;
                case 1:
                    EchoBaseBinding hoursPlayedBinding = EchoBaseBinding.inflate(getLayoutInflater());
                    hoursPlayedBinding.aboveLabel.setText(R.string.echo_hours_this_year);
                    hoursPlayedBinding.largeLabel.setText(String.format(Locale.getDefault(), "%d", 323));
                    hoursPlayedBinding.belowLabel.setText(R.string.echo_hours_episodes);
                    hoursPlayedBinding.smallLabel.setText(getResources()
                            .getQuantityString(R.plurals.echo_hours_podcasts, 71, 71));
                    viewBinding.screenContainer.addView(hoursPlayedBinding.getRoot());
                    currentDrawable = new WaveformScreen();
                    break;
                case 2:
                    EchoBaseBinding queueBinding = EchoBaseBinding.inflate(getLayoutInflater());
                    queueBinding.aboveLabel.setText(R.string.echo_queue_title_many);
                    queueBinding.largeLabel.setText(String.format(Locale.getDefault(), "%d", 33));
                    queueBinding.belowLabel.setText(R.string.echo_queue_hours_waiting);
                    String hours = getResources().getQuantityString(R.plurals.time_hours_quantified, 1, 1);
                    queueBinding.smallLabel.setText(getResources()
                            .getQuantityString(R.plurals.echo_queue_episodes, 50, 50, hours));
                    viewBinding.screenContainer.addView(queueBinding.getRoot());
                    currentDrawable = new StripesScreen();
                    break;
                case 3:
                    EchoBaseBinding listenedAfterBinding = EchoBaseBinding.inflate(getLayoutInflater());
                    listenedAfterBinding.aboveLabel.setText(R.string.echo_listened_after_title);
                    listenedAfterBinding.largeLabel.setText(R.string.echo_listened_after_emoji_yoga);
                    listenedAfterBinding.belowLabel.setText(R.string.echo_listened_after_comment_easy);
                    listenedAfterBinding.smallLabel.setText(getString(R.string.echo_listened_after_time,
                            "2 days, 7 hours and 43 minutes"));
                    viewBinding.screenContainer.addView(listenedAfterBinding.getRoot());
                    currentDrawable = new RotatingSquaresScreen();
                    break;
                case 4:
                    EchoBaseBinding hoarderBinding = EchoBaseBinding.inflate(getLayoutInflater());
                    hoarderBinding.aboveLabel.setText(R.string.echo_hoarder_title);
                    hoarderBinding.largeLabel.setText(R.string.echo_hoarder_emoji_cabinet);
                    hoarderBinding.belowLabel.setText(R.string.echo_hoarder_subtitle_hoarder);
                    hoarderBinding.smallLabel.setText(getString(R.string.echo_hoarder_comment_hoarder, 62, 131));
                    viewBinding.screenContainer.addView(hoarderBinding.getRoot());
                    currentDrawable = new StripesScreen();
                    break;
                case 5:
                    EchoBaseBinding thanksBinding = EchoBaseBinding.inflate(getLayoutInflater());
                    thanksBinding.largeLabel.setText(R.string.echo_thanks);
                    thanksBinding.belowLabel.setText(R.string.echo_thanks_we_are_glad);
                    thanksBinding.smallLabel.setText(R.string.echo_thanks_now_favorite);
                    viewBinding.screenContainer.addView(thanksBinding.getRoot());
                    currentDrawable = new RotatingSquaresScreen();
                    break;
                case 6:
                    EchoSubscriptionsBinding subsBinding = EchoSubscriptionsBinding.inflate(getLayoutInflater());
                    subsBinding.shareButton.setOnClickListener(v -> share());
                    viewBinding.screenContainer.addView(subsBinding.getRoot());
                    currentDrawable = new FinalShareScreen(AppCompatResources.getDrawable(this, R.drawable.echo));
                    break;
                default: // Keep
            }
            viewBinding.echoImage.setImageDrawable(currentDrawable);
        });
    }
}