package de.danoeh.antennapod.core.feed;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.documentfile.provider.DocumentFile;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.util.FastDocumentFile;
import de.danoeh.antennapod.model.download.DownloadError;
import de.danoeh.antennapod.model.download.DownloadStatus;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LocalFeedUpdater {
    private static final String TAG = "LocalFeedUpdater";

    static final String[] PREFERRED_FEED_IMAGE_FILENAMES = {"folder.jpg", "Folder.jpg", "folder.png", "Folder.png"};

    public static void updateFeed(Feed feed, Context context,
                                  @Nullable UpdaterProgressListener updaterProgressListener) {
        try {
            String uriString = feed.getDownload_url().replace(Feed.PREFIX_LOCAL_FOLDER, "");
            DocumentFile documentFolder = DocumentFile.fromTreeUri(context, Uri.parse(uriString));
            if (documentFolder == null) {
                throw new IOException("Unable to retrieve document tree. "
                        + "Try re-connecting the folder on the podcast info page.");
            }
            if (!documentFolder.exists() || !documentFolder.canRead()) {
                throw new IOException("Cannot read local directory. "
                        + "Try re-connecting the folder on the podcast info page.");
            }
            tryUpdateFeed(feed, context, documentFolder.getUri(), updaterProgressListener);

            if (mustReportDownloadSuccessful(feed)) {
                reportSuccess(feed);
            }
        } catch (Exception e) {
            e.printStackTrace();
            reportError(feed, e.getMessage());
        }
    }

    @VisibleForTesting
    static void tryUpdateFeed(Feed feed, Context context, Uri folderUri,
                              UpdaterProgressListener updaterProgressListener) {

    }

    /**
     * Returns the image URL for the local feed.
     */
    @NonNull
    static String getImageUrl(List<FastDocumentFile> files, Uri folderUri) {
        // look for special file names
        for (String iconLocation : PREFERRED_FEED_IMAGE_FILENAMES) {
            for (FastDocumentFile file : files) {
                if (iconLocation.equals(file.getName())) {
                    return file.getUri().toString();
                }
            }
        }

        // use the first image in the folder if existing
        for (FastDocumentFile file : files) {
            String mime = file.getType();
            if (mime != null && (mime.startsWith("image/jpeg") || mime.startsWith("image/png"))) {
                return file.getUri().toString();
            }
        }

        // use default icon as fallback
        return Feed.PREFIX_GENERATIVE_COVER + folderUri;
    }

    private static FeedItem feedContainsFile(Feed feed, String filename) {
        List<FeedItem> items = feed.getItems();
        for (FeedItem i : items) {
            if (i.getMedia() != null && i.getLink().equals(filename)) {
                return i;
            }
        }
        return null;
    }

    private static FeedItem createFeedItem(Feed feed, FastDocumentFile file, Context context) {
        FeedItem item = new FeedItem(0, file.getName(), UUID.randomUUID().toString(),
                file.getName(), new Date(file.getLastModified()), FeedItem.UNPLAYED, feed);
        item.disableAutoDownload();

        long size = file.getLength();
        FeedMedia media = new FeedMedia(0, item, 0, 0, size, file.getType(),
                file.getUri().toString(), file.getUri().toString(), false, null, 0, 0);
        item.setMedia(media);

        for (FeedItem existingItem : feed.getItems()) {
            if (existingItem.getMedia() != null
                    && existingItem.getMedia().getDownload_url().equals(file.getUri().toString())
                    && file.getLength() == existingItem.getMedia().getSize()) {
                // We found an old file that we already scanned. Re-use metadata.
                item.updateFromOther(existingItem);
                return item;
            }
        }

        // Did not find existing item. Scan metadata.
        try {
            loadMetadata(item, file, context);
        } catch (Exception e) {
            item.setDescriptionIfLonger(e.getMessage());
        }
        return item;
    }

    private static void loadMetadata(FeedItem item, FastDocumentFile file, Context context) {

    }

    private static void reportError(Feed feed, String reasonDetailed) {
        DownloadStatus status = new DownloadStatus(feed, feed.getTitle(),
                DownloadError.ERROR_IO_ERROR, false, reasonDetailed, true);
        DBWriter.addDownloadStatus(status);
        DBWriter.setFeedLastUpdateFailed(feed.getId(), true);
    }

    /**
     * Reports a successful download status.
     */
    private static void reportSuccess(Feed feed) {
        DownloadStatus status = new DownloadStatus(feed, feed.getTitle(),
                DownloadError.SUCCESS, true, null, true);
        DBWriter.addDownloadStatus(status);
        DBWriter.setFeedLastUpdateFailed(feed.getId(), false);
    }

    /**
     * Answers if reporting success is needed for the given feed.
     */
    private static boolean mustReportDownloadSuccessful(Feed feed) {
        List<DownloadStatus> downloadStatuses = DBReader.getFeedDownloadLog(feed.getId());

        if (downloadStatuses.isEmpty()) {
            // report success if never reported before
            return true;
        }

        Collections.sort(downloadStatuses, (downloadStatus1, downloadStatus2) ->
                downloadStatus1.getCompletionDate().compareTo(downloadStatus2.getCompletionDate()));

        DownloadStatus lastDownloadStatus = downloadStatuses.get(downloadStatuses.size() - 1);

        // report success if the last update was not successful
        // (avoid logging success again if the last update was ok)
        return !lastDownloadStatus.isSuccessful();
    }

    @FunctionalInterface
    public interface UpdaterProgressListener {
        void onLocalFileScanned(int scanned, int totalFiles);
    }
}
