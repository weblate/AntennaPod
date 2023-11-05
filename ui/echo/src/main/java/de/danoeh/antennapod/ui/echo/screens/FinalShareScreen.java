package de.danoeh.antennapod.ui.echo.screens;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class FinalShareScreen extends BubbleScreen {
    private final Paint paintTextCredits;
    private final Paint paintTextMain;
    private final Drawable logo;

    public FinalShareScreen(Drawable logo) {
        paintTextCredits = new Paint();
        paintTextCredits.setColor(0xffffffff);
        paintTextCredits.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintTextCredits.setStyle(Paint.Style.FILL);
        paintTextCredits.setAlpha(200);
        paintTextMain = new Paint();
        paintTextMain.setColor(0xffffffff);
        paintTextMain.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintTextMain.setStyle(Paint.Style.FILL);
        this.logo = logo;
    }

    protected void drawInner(Canvas canvas, float innerBoxX, float innerBoxY, float innerBoxSize) {
        paintTextMain.setTextSize(innerBoxSize / 20);
        paintTextMain.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("In 2023, you played 100 hours of podcasts", innerBoxX, innerBoxY, paintTextMain);

        for (int i = 0; i < 5; i++) {
            canvas.drawText("10 hours: Podcast " + i, innerBoxX,
                    innerBoxY + 0.25f * innerBoxSize + 0.6f * (innerBoxSize / 5) * i, paintTextMain);
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