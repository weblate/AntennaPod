package de.danoeh.antennapod.core.preferences;

import android.content.Context;
import androidx.annotation.StyleRes;
import de.danoeh.antennapod.core.R;

public abstract class ThemeSwitcher {
    @StyleRes
    public static int getTheme(Context context) {

                return R.style.Theme_AntennaPod_Light;
    }

    @StyleRes
    public static int getNoTitleTheme(Context context) {
                return R.style.Theme_AntennaPod_Light_NoTitle;
    }

    @StyleRes
    public static int getTranslucentTheme(Context context) {

                return R.style.Theme_AntennaPod_Light_Translucent;
    }


}
