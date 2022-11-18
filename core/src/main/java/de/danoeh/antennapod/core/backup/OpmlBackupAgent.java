package de.danoeh.antennapod.core.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInputStream;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupHelper;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

public class OpmlBackupAgent extends BackupAgentHelper {
    private static final String OPML_BACKUP_KEY = "opml";

    @Override
    public void onCreate() {
        addHelper(OPML_BACKUP_KEY, new OpmlBackupHelper(this));
    }

    /**
     * Class for backing up and restoring the OPML file.
     */
    private static class OpmlBackupHelper implements BackupHelper {
        private static final String TAG = "OpmlBackupHelper";

        private static final String OPML_ENTITY_KEY = "antennapod-feeds.opml";

        private final Context mContext;

        /**
         * Checksum of restored OPML file
         */
        private byte[] mChecksum;

        public OpmlBackupHelper(Context context) {
            mContext = context;
        }

        @Override
        public void performBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) {

        }

        @Override
        public void restoreEntity(BackupDataInputStream data) {

        }

        @Override
        public void writeNewStateDescription(ParcelFileDescriptor newState) {
            writeNewStateDescription(newState, mChecksum);
        }

        /**
         * Writes the new state description, which is the checksum of the OPML file.
         *
         * @param newState
         * @param checksum
         */
        private void writeNewStateDescription(ParcelFileDescriptor newState, byte[] checksum) {
            if (checksum == null) {
                return;
            }

            try {
                FileOutputStream outState = new FileOutputStream(newState.getFileDescriptor());
                outState.write(checksum.length);
                outState.write(checksum);
                outState.flush();
                outState.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to write new state description", e);
            }
        }
    }
}
