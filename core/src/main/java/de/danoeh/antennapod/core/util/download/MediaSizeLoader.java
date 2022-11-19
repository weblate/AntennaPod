package de.danoeh.antennapod.core.util.download;

import de.danoeh.antennapod.model.feed.FeedMedia;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public abstract class MediaSizeLoader {
    private static final String TAG = "MediaSizeLoader";

    public static Single<Long> getFeedMediaSizeObservable(FeedMedia media) {
        return Single.create((SingleOnSubscribe<Long>) emitter -> {

        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }
}
