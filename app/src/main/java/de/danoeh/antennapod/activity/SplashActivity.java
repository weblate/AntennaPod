package de.danoeh.antennapod.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.error.CrashReportWriter;
import de.danoeh.antennapod.storage.database.PodDBAdapter;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

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

        File backupFolder = getExternalFilesDir(null);
        File backupFile = new File(backupFolder, "CorruptedDatabaseBackup.db");
        File currentDB = getDatabasePath(PodDBAdapter.DATABASE_NAME);

        if (backupFile.exists()) {

            String output = "";
            try {
                PodDBAdapter.getInstance().open(); // Create database to insert into
                PodDBAdapter.getInstance().close();

                Runtime rt = Runtime.getRuntime();
                Process process = rt.exec(new String[]{ "/system/bin/sh" });
                OutputStreamWriter os = new OutputStreamWriter(process.getOutputStream());

                /*os.write("rm -f \"" + backupFolder.getAbsolutePath() + "/" + "temp_recover.db\"\n");
                os.flush();
                os.write("sqlite3 \"" + backupFile.getAbsolutePath() + "\" \".dump\""
                        + " | grep -v \"TRANSACTION\" | grep -v \"ROLLBACK\" | grep -v \"COMMIT\" | sqlite3 \""
                        + backupFolder.getAbsolutePath() + "/" + "temp_recover.db\"\n");
                os.write("sqlite3 \"" + backupFolder.getAbsolutePath() + "/" + "temp_recover.db\" \".tables\"\n");
                os.flush();*/
                os.write("sqlite3 \"" + currentDB.getAbsolutePath() + "\" -cmd ");
                os.write("\"attach '" + backupFolder.getAbsolutePath() + "/" + "temp_recover.db' as old;\" ");
                os.write("\".tables old\" ");
                os.write("\".print '-----'\" ");
                os.write("\".tables\" ");
                os.write("\"insert into Feeds select * from old.Feeds;\" ");
                os.write("\"insert into FeedItems select * from old.FeedItems;\" ");
                os.write("\"insert into Favorites select * from old.Favorites;\" ");
                os.write("\"insert into FeedMedia select * from old.FeedMedia;\" ");
                os.write("\"insert into Queue select * from old.Queue;\" ");
                os.write("\"insert into sqlite_sequence select * from old.sqlite_sequence;\" ");
                os.write("\n");
                os.flush();
                os.write("exit\n");
                os.flush();

                String stdout = IOUtils.toString(process.getInputStream(), "UTF-8");
                String stderr = IOUtils.toString(process.getErrorStream(), "UTF-8");
                output = stdout + "\n\n\n---\n\n" + stderr;

            } catch (IOException e) {
                output = e.getMessage();
            }
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setMessage(output);
            builder.show();

            //backupFile.delete();
        } else {
            triggerSchemaUpdatesAndLaunch();
        }
    }

    private void triggerSchemaUpdatesAndLaunch() {
        Completable.create(subscriber -> {
            // Trigger schema updates
            PodDBAdapter.getInstance().open();
            PodDBAdapter.getInstance().close();
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
                        CrashReportWriter.write(error);
                        Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    });
    }
}
