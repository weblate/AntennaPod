package de.danoeh.antennapod.ui.echo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;
import de.danoeh.antennapod.storage.preferences.UserPreferences;
import de.danoeh.antennapod.ui.echo.databinding.EchoActivityBinding;
import de.danoeh.antennapod.ui.echo.screens.BubbleScreen;
import de.danoeh.antennapod.ui.echo.screens.FinalShareScreen;
import de.danoeh.antennapod.ui.echo.screens.RotatingSquaresScreen;
import de.danoeh.antennapod.ui.echo.screens.StripesScreen;
import de.danoeh.antennapod.ui.echo.screens.WaveformScreen;

import java.io.File;
import java.io.FileOutputStream;

public class EchoActivity extends AppCompatActivity {
    private static final int NUM_SCREENS = 5;

    private EchoActivityBinding viewBinding;
    private int currentScreen = -1;
    private boolean progressPaused = false;
    private float progress = 0;
    private Drawable currentDrawable;
    private EchoProgress echoProgress;
    private Thread progressUpdateThread;
    long timeTouchDown;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        viewBinding = EchoActivityBinding.inflate(getLayoutInflater());
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
        viewBinding.shareButton.setOnClickListener(v -> share());
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
        progressUpdateThread = new ProgressUpdateThread();
        progressUpdateThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressUpdateThread.interrupt();
        progressUpdateThread = null;
    }

    private String big(String text) {
        return "<big><big><big><big>" + text + "</big></big></big></big>";
    }

    @SuppressWarnings("deprecation")
    private void loadScreen(int screen) {
        if (screen == currentScreen) {
            return;
        }
        currentScreen = screen;
        runOnUiThread(() -> {
            switch (currentScreen) {
                case 0:
                    currentDrawable = new BubbleScreen();
                    viewBinding.echoText.setText(Html.fromHtml("Your year<br /><br />" + big("2023") + "<br /><br />in AntennaPod.<br />- Generated locally on your device -"));
                    break;
                case 1:
                    currentDrawable = new WaveformScreen();
                    viewBinding.echoText.setText(Html.fromHtml("You played<br /><br />" + big("4242") + "<br /><br />hours of podcasts."));
                    break;
                case 2:
                    currentDrawable = new RotatingSquaresScreen();
                    viewBinding.echoText.setText(Html.fromHtml("You played<br /><br />" + big("42 / 53") + "<br /><br />of the episodes released this year. Hoarder vs not"));
                    break;
                case 3:
                    currentDrawable = new StripesScreen();
                    viewBinding.echoText.setText(Html.fromHtml("Time between release and listening"));
                    break;
                case 4:
                    currentDrawable = new FinalShareScreen();
                    viewBinding.echoText.setText(Html.fromHtml("Hours, top5 podcasts"));
                    break;
                default: // Keep
            }
            viewBinding.shareButton.setVisibility((currentScreen == NUM_SCREENS - 1) ? View.VISIBLE : View.GONE);
            viewBinding.echoImage.setImageDrawable(currentDrawable);
        });
    }

    private class ProgressUpdateThread extends Thread {
        private static final int TIME_PER_SCREEN = 5000;

        @Override
        public void run() {
            while (!isInterrupted()) {
                if (!progressPaused && progress < NUM_SCREENS - 0.001f) {
                    progress = Math.min(NUM_SCREENS - 0.001f, progress + 1.0f / 100);
                    echoProgress.setProgress(progress);
                    loadScreen((int) progress);
                }
                try {
                    Thread.sleep(TIME_PER_SCREEN / 100);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}