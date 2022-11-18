package de.danoeh.antennapod.fragment;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.content.res.AppCompatResources;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.joanzapata.iconify.Iconify;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.syndication.HtmlToPlainText;
import de.danoeh.antennapod.menuhandler.FeedMenuHandler;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.model.feed.FeedFunding;
import de.danoeh.antennapod.ui.statistics.StatisticsFragment;
import de.danoeh.antennapod.view.ToolbarIconTintManager;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Displays information about a feed.
 */
public class FeedInfoFragment extends Fragment implements MaterialToolbar.OnMenuItemClickListener {

    private static final String EXTRA_FEED_ID = "de.danoeh.antennapod.extra.feedId";
    private static final String TAG = "FeedInfoActivity";
    private final ActivityResultLauncher<Uri> addLocalFolderLauncher =
            registerForActivityResult(new AddLocalFolder(), this::addLocalFolderResult);

    private Feed feed;
    private Disposable disposable;
    private ImageView imgvCover;
    private TextView txtvTitle;
    private TextView txtvDescription;
    private TextView txtvFundingUrl;
    private TextView lblSupport;
    private TextView txtvUrl;
    private TextView txtvAuthorHeader;
    private ImageView imgvBackground;
    private View infoContainer;
    private View header;
    private MaterialToolbar toolbar;

    public static FeedInfoFragment newInstance(Feed feed) {
        FeedInfoFragment fragment = new FeedInfoFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(EXTRA_FEED_ID, feed.getId());
        fragment.setArguments(arguments);
        return fragment;
    }

    private final View.OnClickListener copyUrlToClipboard = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (feed != null && feed.getDownload_url() != null) {
                String url = feed.getDownload_url();
                ClipData clipData = ClipData.newPlainText(url, url);
                android.content.ClipboardManager cm = (android.content.ClipboardManager) getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(clipData);
                ((MainActivity) getActivity()).showSnackbarAbovePlayer(R.string.copied_url_msg, Snackbar.LENGTH_SHORT);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.feedinfo, null);
        toolbar = root.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.inflateMenu(R.menu.feedinfo);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        toolbar.setOnMenuItemClickListener(this);
        refreshToolbarState();

        AppBarLayout appBar = root.findViewById(R.id.appBar);
        CollapsingToolbarLayout collapsingToolbar = root.findViewById(R.id.collapsing_toolbar);
        ToolbarIconTintManager iconTintManager = new ToolbarIconTintManager(getContext(), toolbar, collapsingToolbar) {
            @Override
            protected void doTint(Context themedContext) {
                toolbar.getMenu().findItem(R.id.visit_website_item)
                        .setIcon(AppCompatResources.getDrawable(themedContext, R.drawable.ic_web));
                toolbar.getMenu().findItem(R.id.share_item)
                        .setIcon(AppCompatResources.getDrawable(themedContext, R.drawable.ic_share));
            }
        };
        iconTintManager.updateTint();
        appBar.addOnOffsetChangedListener(iconTintManager);

        imgvCover = root.findViewById(R.id.imgvCover);
        txtvTitle = root.findViewById(R.id.txtvTitle);
        txtvAuthorHeader = root.findViewById(R.id.txtvAuthor);
        imgvBackground = root.findViewById(R.id.imgvBackground);
        header = root.findViewById(R.id.headerContainer);
        infoContainer = root.findViewById(R.id.infoContainer);
        root.findViewById(R.id.butShowInfo).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.butShowSettings).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.butFilter).setVisibility(View.INVISIBLE);
        // https://github.com/bumptech/glide/issues/529
        imgvBackground.setColorFilter(new LightingColorFilter(0xff828282, 0x000000));

        txtvDescription = root.findViewById(R.id.txtvDescription);
        txtvUrl = root.findViewById(R.id.txtvUrl);
        lblSupport = root.findViewById(R.id.lblSupport);
        txtvFundingUrl = root.findViewById(R.id.txtvFundingUrl);

        txtvUrl.setOnClickListener(copyUrlToClipboard);

        long feedId = getArguments().getLong(EXTRA_FEED_ID);

        root.findViewById(R.id.btnvOpenStatistics).setOnClickListener(view -> {
            StatisticsFragment fragment = new StatisticsFragment();
            ((MainActivity) getActivity()).loadChildFragment(fragment, TransitionEffect.SLIDE);
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (header == null || infoContainer == null) {
            return;
        }
        int horizontalSpacing = (int) getResources().getDimension(R.dimen.additional_horizontal_spacing);
        header.setPadding(horizontalSpacing, header.getPaddingTop(), horizontalSpacing, header.getPaddingBottom());
        infoContainer.setPadding(horizontalSpacing, infoContainer.getPaddingTop(),
                horizontalSpacing, infoContainer.getPaddingBottom());
    }

    private void showFeed() {
        Log.d(TAG, "Language is " + feed.getLanguage());
        Log.d(TAG, "Author is " + feed.getAuthor());
        Log.d(TAG, "URL is " + feed.getDownload_url());
        Glide.with(getContext())
                .load(feed.getImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.color.light_gray)
                        .error(R.color.light_gray)
                        .fitCenter()
                        .dontAnimate())
                .into(imgvCover);
        Glide.with(getContext())
                .load(feed.getImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.color.image_readability_tint)
                        .error(R.color.image_readability_tint)
                        .dontAnimate())
                .into(imgvBackground);

        txtvTitle.setText(feed.getTitle());
        txtvTitle.setMaxLines(6);

        String description = HtmlToPlainText.getPlainText(feed.getDescription());

        txtvDescription.setText(description);

        if (!TextUtils.isEmpty(feed.getAuthor())) {
            txtvAuthorHeader.setText(feed.getAuthor());
        }

        txtvUrl.setText(feed.getDownload_url() + " {fa-paperclip}");

        if (feed.getPaymentLinks() == null || feed.getPaymentLinks().size() == 0) {
            lblSupport.setVisibility(View.GONE);
            txtvFundingUrl.setVisibility(View.GONE);
        } else {
            lblSupport.setVisibility(View.VISIBLE);
            ArrayList<FeedFunding> fundingList = feed.getPaymentLinks();

            // Filter for duplicates, but keep items in the order that they have in the feed.
            Iterator<FeedFunding> i = fundingList.iterator();
            while (i.hasNext()) {
                FeedFunding funding = i.next();
                for (FeedFunding other : fundingList) {
                    if (TextUtils.equals(other.url, funding.url)) {
                        if (other.content != null && funding.content != null
                                && other.content.length() > funding.content.length()) {
                            i.remove();
                            break;
                        }
                    }
                }
            }

            StringBuilder str = new StringBuilder();
            for (FeedFunding funding : fundingList) {
                str.append(funding.content.isEmpty()
                        ? getContext().getResources().getString(R.string.support_podcast)
                        : funding.content).append(" ").append(funding.url);
                str.append("\n");
            }
            str = new StringBuilder(StringUtils.trim(str.toString()));
            txtvFundingUrl.setText(str.toString());
        }

        Iconify.addIcons(txtvUrl);
        refreshToolbarState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private void refreshToolbarState() {
        toolbar.getMenu().findItem(R.id.reconnect_local_folder).setVisible(feed != null && feed.isLocalFeed());
        toolbar.getMenu().findItem(R.id.share_item).setVisible(feed != null && !feed.isLocalFeed());
        toolbar.getMenu().findItem(R.id.visit_website_item).setVisible(feed != null && feed.getLink() != null
                && IntentUtils.isCallable(getContext(), new Intent(Intent.ACTION_VIEW, Uri.parse(feed.getLink()))));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (feed == null) {
            ((MainActivity) getActivity()).showSnackbarAbovePlayer(
                    R.string.please_wait_for_data, Toast.LENGTH_LONG);
            return false;
        }
        boolean handled = FeedMenuHandler.onOptionsItemClicked(getContext(), item, feed);

        if (item.getItemId() == R.id.reconnect_local_folder && Build.VERSION.SDK_INT >= 21) {
            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(getContext());
            alert.setMessage(R.string.reconnect_local_folder_warning);
            alert.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                try {
                    addLocalFolderLauncher.launch(null);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "No activity found. Should never happen...");
                }
            });
            alert.setNegativeButton(android.R.string.cancel, null);
            alert.show();
            return true;
        }

        return handled;
    }

    private void addLocalFolderResult(final Uri uri) {
        if (uri == null) {
            return;
        }
        reconnectLocalFolder(uri);
    }

    private void reconnectLocalFolder(Uri uri) {

    }

    private static class AddLocalFolder extends ActivityResultContracts.OpenDocumentTree {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @NonNull
        @Override
        public Intent createIntent(@NonNull final Context context, @Nullable final Uri input) {
            return super.createIntent(context, input)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }
}
