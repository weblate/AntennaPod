package de.danoeh.antennapod.fragment;

import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.service.playback.EasyPlaybackService;
import de.danoeh.antennapod.event.playback.PlaybackPositionEvent;
import de.danoeh.antennapod.event.playback.PlaybackServiceEvent;
import de.danoeh.antennapod.view.PlayButton;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.ExecutionException;

/**
 * Fragment which is supposed to be displayed outside of the MediaplayerActivity.
 */
public class ExternalPlayerFragment extends Fragment {
    public static final String TAG = "ExternalPlayerFragment";

    private ImageView imgvCover;
    private TextView txtvTitle;
    private PlayButton butPlay;
    private TextView feedName;
    private ProgressBar progressBar;
    private Disposable disposable;
    private MediaController mediaController;
    ListenableFuture<MediaController> mediacontrollerFuture;

    public ExternalPlayerFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.external_player_fragment, container, false);
        imgvCover = root.findViewById(R.id.imgvCover);
        txtvTitle = root.findViewById(R.id.txtvTitle);
        butPlay = root.findViewById(R.id.butPlay);
        feedName = root.findViewById(R.id.txtvAuthor);
        progressBar = root.findViewById(R.id.episodeProgress);

        root.findViewById(R.id.fragmentLayout).setOnClickListener(v -> {
            Log.d(TAG, "layoutInfo was clicked");
            if (mediaController != null && mediaController.getMediaMetadata().mediaType != null) {
                 if (mediaController.getMediaMetadata().mediaType == MediaMetadata.MEDIA_TYPE_VIDEO) {
                    //Intent intent = PlaybackServiceOld.getPlayerActivityIntent(getActivity(), controller.getMedia());
                    //startActivity(intent);
                } else {
                    ((MainActivity) getActivity()).getBottomSheet().setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        butPlay.setOnClickListener(v -> {
            if (mediaController.isPlaying()) {
                mediaController.pause();
            } else {
                mediaController.play();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        SessionToken sessionToken = new SessionToken(getContext().getApplicationContext(),
                new ComponentName(getContext(), EasyPlaybackService.class));
        mediacontrollerFuture = new MediaController.Builder(getContext(), sessionToken).buildAsync();
        mediacontrollerFuture.addListener(() -> {
            try {
                mediaController = mediacontrollerFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            mediaController.addListener(new Player.Listener() {
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    butPlay.setIsShowPlay(!isPlaying);
                }
            });
            butPlay.setIsShowPlay(!mediaController.isPlaying());
            loadMediaInfo();
        }, MoreExecutors.directExecutor());
        butPlay.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).setPlayerVisible(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        MediaController.releaseFuture(mediacontrollerFuture);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPositionObserverUpdate(PlaybackPositionEvent event) {
        progressBar.setProgress((int) ((double) mediaController.getCurrentPosition() / mediaController.getDuration() * 100));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlaybackServiceChanged(PlaybackServiceEvent event) {
        if (event.action == PlaybackServiceEvent.Action.SERVICE_SHUT_DOWN) {
            ((MainActivity) getActivity()).setPlayerVisible(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment is about to be destroyed");
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void loadMediaInfo() {
        Log.d(TAG, "Loading media info");
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = Maybe.fromCallable(() -> mediaController.getMediaMetadata())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateUi,
                        error -> Log.e(TAG, Log.getStackTraceString(error)),
                        () -> ((MainActivity) getActivity()).setPlayerVisible(false));
    }

    private void updateUi(MediaMetadata mediaMetadata) {
        ((MainActivity) getActivity()).setPlayerVisible(true);
        txtvTitle.setText(mediaMetadata.title);
        feedName.setText(mediaMetadata.albumArtist);
        onPositionObserverUpdate(new PlaybackPositionEvent(
                (int) mediaController.getCurrentPosition(), (int) mediaController.getDuration()));

        RequestOptions options = new RequestOptions()
                .placeholder(R.color.light_gray)
                .error(R.color.light_gray)
                .fitCenter()
                .dontAnimate();

        Glide.with(this)
                .load(mediaMetadata.artworkUri)
                //.error(Glide.with(this)
                //        .load(ImageResourceUtils.getFallbackImageLocation(media))
                //        .apply(options))
                .apply(options)
                .into(imgvCover);

        if (mediaController != null && mediaController.getMediaMetadata().mediaType != null
                && mediaController.getMediaMetadata().mediaType == MediaMetadata.MEDIA_TYPE_VIDEO) {
            ((MainActivity) getActivity()).getBottomSheet().setLocked(true);
            ((MainActivity) getActivity()).getBottomSheet().setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            butPlay.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).getBottomSheet().setLocked(false);
        }
    }
}
