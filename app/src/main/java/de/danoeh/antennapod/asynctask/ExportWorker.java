package de.danoeh.antennapod.asynctask;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import de.danoeh.antennapod.core.export.ExportWriter;
import io.reactivex.Observable;

/**
 * Writes an OPML file into the export directory in the background.
 */
public class ExportWorker {

    private static final String EXPORT_DIR = "export/";
    private static final String TAG = "ExportWorker";
    private static final String DEFAULT_OUTPUT_NAME = "antennapod-feeds";

    private final @NonNull ExportWriter exportWriter;
    private final @NonNull File output;
    private final Context context;

    public ExportWorker(@NonNull ExportWriter exportWriter, Context context) {
        this(exportWriter, new File(
                DEFAULT_OUTPUT_NAME + "." + exportWriter.fileExtension()), context);
    }

    private ExportWorker(@NonNull ExportWriter exportWriter, @NonNull File output, Context context) {
        this.exportWriter = exportWriter;
        this.output = output;
        this.context = context;
    }

    public Observable<File> exportObservable() {
        if (output.exists()) {
            boolean success = output.delete();
            Log.w(TAG, "Overwriting previously exported file: " + success);
        }
        return Observable.create(subscriber -> {
                subscriber.onComplete();
        });
    }

}
