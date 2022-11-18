package de.danoeh.antennapod.core.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import de.danoeh.antennapod.core.feed.ChapterMerger;
import de.danoeh.antennapod.core.service.download.AntennapodHttpClient;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.model.feed.Chapter;
import de.danoeh.antennapod.model.feed.FeedMedia;
import de.danoeh.antennapod.model.playback.Playable;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.input.CountingInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for getting chapter data from media files.
 */
public class ChapterUtils {

    private static final String TAG = "ChapterUtils";

    private ChapterUtils() {
    }

    public static int getCurrentChapterIndex(Playable media, int position) {
        if (media == null || media.getChapters() == null || media.getChapters().size() == 0) {
            return -1;
        }
        List<Chapter> chapters = media.getChapters();
        for (int i = 0; i < chapters.size(); i++) {
            if (chapters.get(i).getStart() > position) {
                return i - 1;
            }
        }
        return chapters.size() - 1;
    }

    public static void loadChapters(Playable playable, Context context) {
        if (playable.getChapters() != null) {
            // Already loaded
            return;
        }

        List<Chapter> chaptersFromDatabase = null;
        List<Chapter> chaptersFromPodcastIndex = null;
        if (playable instanceof FeedMedia) {
            FeedMedia feedMedia = (FeedMedia) playable;
            if (feedMedia.getItem() == null) {
                feedMedia.setItem(DBReader.getFeedItem(feedMedia.getItemId()));
            }
            if (feedMedia.getItem().hasChapters()) {
                chaptersFromDatabase = DBReader.loadChaptersOfFeedItem(feedMedia.getItem());
            }

            if (!TextUtils.isEmpty(feedMedia.getItem().getPodcastIndexChapterUrl())) {
                chaptersFromPodcastIndex = ChapterUtils.loadChaptersFromUrl(
                        feedMedia.getItem().getPodcastIndexChapterUrl());
            }

        }

        List<Chapter> chaptersFromMediaFile = ChapterUtils.loadChaptersFromMediaFile(playable, context);
        List<Chapter> chaptersMergePhase1 = ChapterMerger.merge(chaptersFromDatabase, chaptersFromMediaFile);
        List<Chapter> chapters = ChapterMerger.merge(chaptersMergePhase1, chaptersFromPodcastIndex);
        if (chapters == null) {
            // Do not try loading again. There are no chapters.
            playable.setChapters(Collections.emptyList());
        } else {
            playable.setChapters(chapters);
        }
    }

    public static List<Chapter> loadChaptersFromMediaFile(Playable playable, Context context) {
        return null;
    }

    private static CountingInputStream openStream(Playable playable, Context context) throws IOException {
        if (playable.localFileAvailable()) {
            if (playable.getLocalMediaUrl() == null) {
                throw new IOException("No local url");
            }
            File source = new File(playable.getLocalMediaUrl());
            if (!source.exists()) {
                throw new IOException("Local file does not exist");
            }
            return new CountingInputStream(new BufferedInputStream(new FileInputStream(source)));
        } else if (playable.getStreamUrl().startsWith(ContentResolver.SCHEME_CONTENT)) {
            Uri uri = Uri.parse(playable.getStreamUrl());
            return new CountingInputStream(new BufferedInputStream(context.getContentResolver().openInputStream(uri)));
        } else {
            Request request = new Request.Builder().url(playable.getStreamUrl()).build();
            Response response = AntennapodHttpClient.getHttpClient().newCall(request).execute();
            if (response.body() == null) {
                throw new IOException("Body is null");
            }
            return new CountingInputStream(new BufferedInputStream(response.body().byteStream()));
        }
    }

    public static List<Chapter> loadChaptersFromUrl(String url) {
        return null;
    }

    @NonNull
    private static List<Chapter> readId3ChaptersFrom(CountingInputStream in) throws IOException {
            return Collections.emptyList();
    }

    @NonNull
    private static List<Chapter> readOggChaptersFromInputStream(InputStream input)  {
        return Collections.emptyList();
    }

    /**
     * Makes sure that chapter does a title and an item attribute.
     */
    private static void enumerateEmptyChapterTitles(List<Chapter> chapters) {
        for (int i = 0; i < chapters.size(); i++) {
            Chapter c = chapters.get(i);
            if (c.getTitle() == null) {
                c.setTitle(Integer.toString(i));
            }
        }
    }

    private static boolean chaptersValid(List<Chapter> chapters) {
        if (chapters.isEmpty()) {
            return false;
        }
        for (Chapter c : chapters) {
            if (c.getStart() < 0) {
                return false;
            }
        }
        return true;
    }
}
