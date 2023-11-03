package de.danoeh.antennapod.ui.echo.screens;

import android.graphics.Canvas;
import android.graphics.Paint;

public class FinalShareScreen extends BubbleScreen {
    private final Paint paintTextCredits;

    public FinalShareScreen() {
        paintTextCredits = new Paint();
        paintTextCredits.setColor(0xffffffff);
        paintTextCredits.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintTextCredits.setStyle(Paint.Style.FILL);
        paintTextCredits.setAlpha(200);
    }

    protected void drawInner(Canvas canvas, float innerBoxX, float innerBoxY, float innerBoxSize) {
        paintTextCredits.setTextSize(innerBoxSize / 30);
        paintTextCredits.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("#AntennaPodEcho", innerBoxX, innerBoxY + innerBoxSize, paintTextCredits);
        paintTextCredits.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("antennapod.org/s/echo", innerBoxX + innerBoxSize, innerBoxY + innerBoxSize, paintTextCredits);
    }
}