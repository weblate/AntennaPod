package de.danoeh.antennapod.ui.echo.screens;

import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;

import java.util.Locale;

public class PlayedEpisodesScreen extends BaseScreen {
    private final Paint paintTextMain;

    public PlayedEpisodesScreen() {
        paintTextMain = new Paint();
        paintTextMain.setColor(0xffffffff);
        paintTextMain.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintTextMain.setStyle(Paint.Style.FILL);

        for (int i = 0; i < 16; i++) {
            particles.add(new Particle(0.33 * (i % 4) - 0.05 + 0.1 * Math.random(),
                    0.33 * (float) (i / 4) - 0.05 + 0.1 * Math.random(), Math.random()));
        }
    }

    @Override
    protected void drawParticle(@NonNull Canvas canvas, Particle p,
                                float innerBoxX, float innerBoxY, float innerBoxSize) {
        float x = (float) (innerBoxX + p.positionX * innerBoxSize);
        float y = (float) (innerBoxY + p.positionY * innerBoxSize);
        float size = innerBoxSize / 6;
        canvas.save();
        canvas.rotate((float) (360 * p.speed), x, y);
        canvas.drawRect(x - size, y - size, x + size, y + size, paintParticles);
        canvas.restore();
    }

    @Override
    protected void particleTick(Particle p) {
        p.speed += 0.001;
        if (p.speed > 1) {
            p.speed -= 1;
        }
    }

    @Override
    protected void drawInner(Canvas canvas, float innerBoxX, float innerBoxY, float innerBoxSize) {
        paintTextMain.setTextSize(innerBoxSize / 20);
        paintTextMain.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("You played", innerBoxX + 0.5f * innerBoxSize,
                innerBoxY + 0.1f * innerBoxSize, paintTextMain);
        canvas.drawText("of the episodes released this year", innerBoxX + 0.5f * innerBoxSize,
                innerBoxY + 0.6f * innerBoxSize, paintTextMain);

        paintTextMain.setTextAlign(Paint.Align.CENTER);
        paintTextMain.setTextSize(innerBoxSize / 4);
        canvas.drawText(String.format(Locale.getDefault(), "%d / %d", 42, 53),
                innerBoxX + 0.55f * innerBoxSize, innerBoxY + 0.4f * innerBoxSize, paintTextMain);
    }
}