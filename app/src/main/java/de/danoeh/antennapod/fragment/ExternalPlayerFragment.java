package de.danoeh.antennapod.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.event.playback.PlaybackPositionEvent;
import de.danoeh.antennapod.event.playback.PlaybackServiceEvent;
import de.danoeh.antennapod.model.playback.MediaType;
import de.danoeh.antennapod.core.feed.util.ImageResourceUtils;
import de.danoeh.antennapod.model.playback.Playable;
import de.danoeh.antennapod.core.util.playback.PlaybackController;
import de.danoeh.antennapod.view.PlayButton;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private PlaybackController controller;
    private Disposable disposable;

    public ExternalPlayerFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.external_player_fragment, container, false);
        imgvCover = root.findViewById(R.id.imgvCover);
        txtvTitle = root.findViewById(R.id.txtvTitle);
        butPlay = root.findViewById(R.id.butPlay);
        feedName = root.findViewById(R.id.txtvAuthor);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMediaInfo();
    }

    private PlaybackController setupPlaybackController() {
        return new PlaybackController(getActivity()) {
            @Override
            protected void updatePlayButtonShowsPlay(boolean showPlay) {
                butPlay.setIsShowPlay(showPlay);
            }

            @Override
            public void loadMediaInfo() {
                ExternalPlayerFragment.this.loadMediaInfo();
            }

            @Override
            public void onPlaybackEnd() {
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        controller = setupPlaybackController();
        controller.init();
        loadMediaInfo();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (controller != null) {
            controller.release();
            controller = null;
        }
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPositionObserverUpdate(PlaybackPositionEvent event) {
        if (controller == null) {
            return;
        } else if (controller.getPosition() == Playable.INVALID_TIME
                || controller.getDuration() == Playable.INVALID_TIME) {
            return;
        }
        progressBar.setProgress((int)
                ((double) controller.getPosition() / controller.getDuration() * 100));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlaybackServiceChanged(PlaybackServiceEvent event) {
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
        if (controller != null) {
            controller.pause();
        }
    }

    private void loadMediaInfo() {

    }

    private void updateUi(Playable media) {

    }
}
