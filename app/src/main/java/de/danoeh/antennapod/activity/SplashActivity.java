package de.danoeh.antennapod.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import de.danoeh.antennapod.R;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Shows the AntennaPod logo while waiting for the main activity to start.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, 0xffffffff);
            progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            progressBar.getIndeterminateDrawable().setColorFilter(
                    new PorterDuffColorFilter(0xffffffff, PorterDuff.Mode.SRC_IN));
        }

        Completable.create(subscriber -> {
            // Trigger schema updates
            subscriber.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    () -> {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish();
                    }, error -> {
                        error.printStackTrace();
                        Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    });
    }
}
