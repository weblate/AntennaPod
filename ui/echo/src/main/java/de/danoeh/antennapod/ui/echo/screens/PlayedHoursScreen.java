package de.danoeh.antennapod.ui.echo.screens;

import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;

import java.util.Locale;

public class PlayedHoursScreen extends BaseScreen {
    protected static final int NUM_PARTICLES = 40;
    private final Paint paintTextMain;

    public PlayedHoursScreen() {
        paintTextMain = new Paint();
        paintTextMain.setColor(0xffffffff);
        paintTextMain.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintTextMain.setStyle(Paint.Style.FILL);

        for (int i = 0; i < NUM_PARTICLES; i++) {
            particles.add(new Particle(1.1f + 1.1f * i / NUM_PARTICLES - 0.05f, 0, 0));
        }
    }

    @Override
    protected void drawParticle(@NonNull Canvas canvas, Particle p,
                                float innerBoxX, float innerBoxY, float innerBoxSize) {
        float width = getBounds().width();
        float x = (float) (width * p.positionX);
        canvas.drawRect(x, innerBoxY + 1.1f * innerBoxSize, x + (1.1f * width) / NUM_PARTICLES,
                (float) (innerBoxY + 1.1f * innerBoxSize - 0.6 * p.positionY * innerBoxSize), paintParticles);
    }

    @Override
    protected void particleTick(Particle p) {
        p.positionX += 0.002;
        if (p.positionY <= 0.2 || p.positionY >= 1) {
            p.speed = -p.speed;
            p.positionY -= p.speed;
        }
        p.positionY -= p.speed;
        if (p.positionX > 1.05f) {
            p.positionX -= 1.1;
            p.positionY = 0.2 + 0.8 * Math.random();
            p.speed = 0.008 * Math.random() - 0.004;
        }
    }

    @Override
    protected void drawInner(Canvas canvas, float innerBoxX, float innerBoxY, float innerBoxSize) {
        paintTextMain.setTextSize(innerBoxSize / 20);
        paintTextMain.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("You played", innerBoxX + 0.5f * innerBoxSize,
                innerBoxY + 0.1f * innerBoxSize, paintTextMain);
        canvas.drawText("hours of podcasts", innerBoxX + 0.5f * innerBoxSize,
                innerBoxY + 0.6f * innerBoxSize, paintTextMain);

        paintTextMain.setTextAlign(Paint.Align.CENTER);
        paintTextMain.setTextSize(innerBoxSize / 4);
        canvas.drawText(String.format(Locale.getDefault(), "%d", 4242),
                innerBoxX + 0.55f * innerBoxSize, innerBoxY + 0.4f * innerBoxSize, paintTextMain);
    }
}