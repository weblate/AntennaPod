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
        paintTextMain.setTextSize(innerBoxSize / 18);
        paintTextMain.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Your favorite podcasts 2023", innerBoxX, innerBoxY, paintTextMain);

        float coverSize = innerBoxSize / 3;
        canvas.drawRect(innerBoxX, innerBoxY + 0.1f * innerBoxSize, innerBoxX + coverSize, innerBoxY + 0.1f * innerBoxSize + coverSize, paintParticles);

        float lineHeight = 1.3f * (innerBoxSize / 18);
        for (int i = 0; i < 5; i++) {
            canvas.drawText((i + 1) + ". Lorem ipsum", innerBoxX,
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