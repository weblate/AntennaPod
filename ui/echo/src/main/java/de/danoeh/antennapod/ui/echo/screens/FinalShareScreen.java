package de.danoeh.antennapod.ui.echo.screens;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import de.danoeh.antennapod.ui.echo.R;
import java.util.ArrayList;

public class FinalShareScreen extends BubbleScreen {
    private final Paint paintTextCredits;
    private final Paint paintTextMain;
    private final Paint paintTextHeading;
    private final Paint paintCoverBorder;
    private final String heading;
    private final Drawable logo;
    private final ArrayList<Pair<String, Drawable>> favoritePods;
    private final Typeface typefaceNormal;
    private final Typeface typefaceBold;

    public FinalShareScreen(Context context, ArrayList<Pair<String, Drawable>> favoritePods) {
        this.heading = context.getString(R.string.echo_share_heading);
        this.logo = AppCompatResources.getDrawable(context, R.drawable.echo);
        this.favoritePods = favoritePods;
        typefaceNormal = ResourcesCompat.getFont(context, R.font.sarabun_regular);
        typefaceBold = ResourcesCompat.getFont(context, R.font.sarabun_semi_bold);
        paintTextCredits = new Paint();
        paintTextCredits.setColor(0xffffffff);
        paintTextCredits.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintTextCredits.setStyle(Paint.Style.FILL);
        paintTextCredits.setAlpha(200);
        paintTextMain = new Paint();
        paintTextMain.setColor(0xffffffff);
        paintTextMain.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintTextMain.setStyle(Paint.Style.FILL);
        paintTextMain.setTextAlign(Paint.Align.LEFT);
        paintTextHeading = new Paint();
        paintTextHeading.setColor(0xffffffff);
        paintTextHeading.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintTextHeading.setStyle(Paint.Style.FILL);
        paintTextHeading.setTextAlign(Paint.Align.LEFT);
        paintTextHeading.setTypeface(typefaceBold);
        paintCoverBorder = new Paint();
        paintCoverBorder.setColor(0xffffffff);
        paintCoverBorder.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintCoverBorder.setStyle(Paint.Style.FILL);
        paintCoverBorder.setAlpha(70);
    }

    protected void drawInner(Canvas canvas, float innerBoxX, float innerBoxY, float innerBoxSize) {
        float headingSize = innerBoxSize / 14;
        paintTextHeading.setTextSize(headingSize);
        canvas.drawText(heading, innerBoxX, innerBoxY + headingSize, paintTextHeading);

        float fontSizePods = innerBoxSize / 18; // First one only
        paintTextMain.setTypeface(typefaceBold);
        float coverX = innerBoxX;
        float textY = innerBoxY + 0.55f * innerBoxSize;
        for (int i = 0; i < favoritePods.size(); i++) {
            float coverSize;
            if (i == 0) {
                coverSize = 0.3f * innerBoxSize;
            } else if (i == 1) {
                coverSize = 0.22f * innerBoxSize;
            } else {
                coverSize = 0.13f * innerBoxSize;
            }
            Rect logo1Pos = new Rect((int) coverX, (int) (innerBoxY + 0.45f * innerBoxSize - coverSize),
                    (int) (coverX + coverSize), (int) (innerBoxY + 0.45f * innerBoxSize));
            canvas.drawRect(logo1Pos, paintCoverBorder);
            logo1Pos.inset((int) (0.003f * innerBoxSize), (int) (0.003f * innerBoxSize));
            favoritePods.get(i).second.setBounds(logo1Pos);
            favoritePods.get(i).second.draw(canvas);
            coverX += coverSize + 0.02f * innerBoxSize;

            paintTextMain.setTextSize(fontSizePods);
            canvas.drawText((i + 1) + ".", innerBoxX, textY, paintTextMain);
            canvas.drawText(favoritePods.get(i).first, innerBoxX + 0.055f * innerBoxSize, textY, paintTextMain);
            fontSizePods = innerBoxSize / 24; // Starting with second text is smaller
            textY += 1.3f * fontSizePods;
            paintTextMain.setTypeface(typefaceNormal);
        }

        float fontSize = innerBoxSize / 30;
        paintTextCredits.setTextSize(fontSize);
        paintTextCredits.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("#AntennaPodEcho", innerBoxX, innerBoxY + innerBoxSize - fontSize, paintTextCredits);
        canvas.drawText("antennapod.org/s/echo", innerBoxX, innerBoxY + innerBoxSize, paintTextCredits);

        int height = (int) (2 * fontSize);
        int width = (int) (2 * fontSize * (1.0 * logo.getIntrinsicWidth()) / logo.getIntrinsicHeight());
        logo.setBounds((int) (innerBoxX + innerBoxSize - width), (int) (innerBoxY + innerBoxSize - height),
                (int) (innerBoxX + innerBoxSize), (int) (innerBoxY + innerBoxSize));
        logo.draw(canvas);
    }
}