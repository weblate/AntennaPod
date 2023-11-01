package de.danoeh.antennapod.ui.home.sections;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.danoeh.antennapod.databinding.HomeSectionEchoBinding;
import de.danoeh.antennapod.ui.echo.EchoActivity;

public class EchoSection extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeSectionEchoBinding viewBinding = HomeSectionEchoBinding.inflate(inflater);
        viewBinding.openEchoButton.setOnClickListener(v -> startActivity(new Intent(getContext(), EchoActivity.class)));
        return viewBinding.getRoot();
    }
}
