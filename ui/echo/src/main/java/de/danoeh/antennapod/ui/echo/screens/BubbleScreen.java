package de.danoeh.antennapod.ui.echo.screens;

import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.NonNull;

public class BubbleScreen extends BaseScreen {
    protected static final double PARTICLE_SPEED = 0.00002;
    protected static final int NUM_PARTICLES = 15;

    public BubbleScreen(Context context) {
        super(context);
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
        canvas.drawCircle((float) (width * p.positionX), (float) (p.positionY * height),
                innerBoxSize / 5, paintParticles);
    }

    @Override
    protected void particleTick(Particle p, long timeSinceLastFrame) {
        p.positionY -= p.speed * timeSinceLastFrame;
        if (p.positionY < -0.5) {
            p.positionX = Math.random();
            p.positionY = 1.5f;
            p.speed = PARTICLE_SPEED + 2 * PARTICLE_SPEED * Math.random();
        }
    }
}