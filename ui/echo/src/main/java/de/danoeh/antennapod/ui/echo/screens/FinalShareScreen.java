package de.danoeh.antennapod.ui.echo.screens;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Pair;

import java.util.ArrayList;

public class FinalShareScreen extends BubbleScreen {
    private final Paint paintTextCredits;
    private final Paint paintTextMain;
    private final Paint paintTextHeading;
    private final Paint paintCoverBorder;
    private final String heading;
    private final Drawable logo;
    private final ArrayList<Pair<String, Drawable>> favoritePods;

    public FinalShareScreen(String heading, Drawable logo, ArrayList<Pair<String, Drawable>> favoritePods) {
        this.heading = heading;
        this.logo = logo;
        this.favoritePods = favoritePods;
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
        paintTextHeading.setTypeface(Typeface.create(paintTextHeading.getTypeface(), Typeface.BOLD));
        paintTextHeading.setTextAlign(Paint.Align.LEFT);
        paintCoverBorder = new Paint();
        paintCoverBorder.setColor(0xffffffff);
        paintCoverBorder.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintCoverBorder.setStyle(Paint.Style.FILL);
        paintCoverBorder.setAlpha(70);
    }

    protected void drawInner(Canvas canvas, float innerBoxX, float innerBoxY, float innerBoxSize) {
        paintTextHeading.setTextSize(innerBoxSize / 14);
        canvas.drawText(heading, innerBoxX, innerBoxY, paintTextHeading);

        paintTextMain.setTextSize(innerBoxSize / 18);
        float lineHeight = 1.3f * (innerBoxSize / 18);
        float coverX = innerBoxX;
        for (int i = 0; i < favoritePods.size(); i++) {
            float coverSize;
            if (i == 0) {
                coverSize = 0.3f * innerBoxSize;
            } else if (i == 1) {
                coverSize = 0.22f * innerBoxSize;
            } else {
                coverSize = 0.13f * innerBoxSize;
            }
            Rect logo1Pos = new Rect((int) coverX, (int) (innerBoxY + 0.42f * innerBoxSize - coverSize),
                    (int) (coverX + coverSize), (int) (innerBoxY + 0.42f * innerBoxSize));
            canvas.drawRect(logo1Pos, paintCoverBorder);
            logo1Pos.inset((int) (0.003f * innerBoxSize), (int) (0.003f * innerBoxSize));
            favoritePods.get(i).second.setBounds(logo1Pos);
            favoritePods.get(i).second.draw(canvas);
            coverX += coverSize + 0.02f * innerBoxSize;

            canvas.drawText((i + 1) + ". " + favoritePods.get(i).first, innerBoxX,
                    innerBoxY + 0.55f * innerBoxSize + lineHeight * i, paintTextMain);
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