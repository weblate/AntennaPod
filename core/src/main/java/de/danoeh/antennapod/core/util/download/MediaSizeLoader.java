package de.danoeh.antennapod.core.util.download;

import android.text.TextUtils;
import de.danoeh.antennapod.core.service.download.AntennapodHttpClient;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.model.feed.FeedMedia;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import android.util.Log;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;

public abstract class MediaSizeLoader {
    private static final String TAG = "MediaSizeLoader";

    public static Single<Long> getFeedMediaSizeObservable(FeedMedia media) {
        return Single.create((SingleOnSubscribe<Long>) emitter -> {

        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }
}
