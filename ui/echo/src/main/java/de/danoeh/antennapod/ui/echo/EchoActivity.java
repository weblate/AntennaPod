package de.danoeh.antennapod.ui.echo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;
import de.danoeh.antennapod.storage.preferences.UserPreferences;
import de.danoeh.antennapod.ui.echo.databinding.EchoActivityBinding;
import de.danoeh.antennapod.ui.echo.screens.IntroScreen;
import de.danoeh.antennapod.ui.echo.screens.PlayedEpisodesScreen;
import de.danoeh.antennapod.ui.echo.screens.PlayedHoursScreen;

import java.io.File;
import java.io.FileOutputStream;

public class EchoActivity extends AppCompatActivity {
    private static final int NUM_SCREENS = 3;

    private EchoActivityBinding viewBinding;
    private int currentScreen = -1;
    private float progress = 0;
    private Drawable currentDrawable;
    private EchoProgress echoProgress;
    private Thread progressUpdateThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        viewBinding = EchoActivityBinding.inflate(getLayoutInflater());
        viewBinding.echoImage.setOnClickListener(v -> {
            int newScreen = (currentScreen + 1) % NUM_SCREENS;
            progress = newScreen;
            echoProgress.setProgress(progress);
            loadScreen(newScreen);
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

    private void loadScreen(int screen) {
        if (screen == currentScreen) {
            return;
        }
        currentScreen = screen;
        switch (currentScreen) {
            case 0:
                currentDrawable = new IntroScreen();
                break;
            case 1:
                currentDrawable = new PlayedHoursScreen();
                break;
            case 2:
                currentDrawable = new PlayedEpisodesScreen();
                break;
            default: // Keep
        }
        runOnUiThread(() -> viewBinding.echoImage.setImageDrawable(currentDrawable));
    }

    private class ProgressUpdateThread extends Thread {
        private static final int TIME_PER_SCREEN = 5000;

        @Override
        public void run() {
            while (!isInterrupted()) {
                progress += 1.0 / 100;
                if (progress > NUM_SCREENS) {
                    progress -= NUM_SCREENS;
                }
                echoProgress.setProgress(progress);
                loadScreen((int) progress);
                try {
                    Thread.sleep(TIME_PER_SCREEN / 100);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}