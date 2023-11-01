package de.danoeh.antennapod.ui.echo.screens;

import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;

import java.util.Locale;

public class IntroScreen extends BaseScreen {
    protected static final double PARTICLE_SPEED = 0.0005;
    protected static final int NUM_PARTICLES = 20;
    private final Paint paintTextMain;

    public IntroScreen() {
        paintTextMain = new Paint();
        paintTextMain.setColor(0xffffffff);
        paintTextMain.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintTextMain.setStyle(Paint.Style.FILL);

        for (int i = 0; i < NUM_PARTICLES; i++) {
            particles.add(new Particle(Math.random(), 2.0 * Math.random() - 0.5, // Could already be off-screen
                    PARTICLE_SPEED + 2 * PARTICLE_SPEED * Math.random()));
        }
    }

    @Override
    protected void drawParticle(@NonNull Canvas canvas, Particle p,
                                float innerBoxX, float innerBoxY, float innerBoxSize) {
        float width = getBounds().width();
        float height = getBounds().height();
        canvas.drawCircle((float) (width * p.positionX), (float) (p.positionY * height), width / 5, paintParticles);
    }

    @Override
    protected void particleTick(Particle p) {
        p.positionY -= p.speed;
        if (p.positionY < -0.5) {
            p.positionX = Math.random();
            p.positionY = 1.5f;
            p.speed = PARTICLE_SPEED + 2 * PARTICLE_SPEED * Math.random();
        }
    }

    @Override
    protected void drawInner(Canvas canvas, float innerBoxX, float innerBoxY, float innerBoxSize) {
        paintTextMain.setTextSize(innerBoxSize / 20);
        paintTextMain.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Your year", innerBoxX + 0.5f * innerBoxSize,
                innerBoxY + 0.1f * innerBoxSize, paintTextMain);
        canvas.drawText("in AntennaPod", innerBoxX + 0.5f * innerBoxSize,
                innerBoxY + 0.6f * innerBoxSize, paintTextMain);
        canvas.drawText("- generated privately on your device -", innerBoxX + 0.5f * innerBoxSize,
                innerBoxY + 0.7f * innerBoxSize, paintTextMain);

        paintTextMain.setTextAlign(Paint.Align.CENTER);
        paintTextMain.setTextSize(innerBoxSize / 4);
        canvas.drawText(String.format(Locale.getDefault(), "%d", 2023),
                innerBoxX + 0.55f * innerBoxSize, innerBoxY + 0.4f * innerBoxSize, paintTextMain);
    }
}