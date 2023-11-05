package de.danoeh.antennapod.ui.echo.screens;

import android.graphics.Canvas;
import androidx.annotation.NonNull;

public class RotatingSquaresScreen extends BaseScreen {
    public RotatingSquaresScreen() {
        for (int i = 0; i < 16; i++) {
            particles.add(new Particle(
                    0.3 * (float) (i % 4) + 0.05 + 0.1 * Math.random() - 0.05,
                    0.2 * (float) (i / 4) + 0.20 + 0.1 * Math.random() - 0.05,
                    Math.random(), 0.0001 * Math.random() + 0.00005));
        }
    }

    @Override
    protected void drawParticle(@NonNull Canvas canvas, Particle p,
                                float innerBoxX, float innerBoxY, float innerBoxSize) {
        float x = (float) (p.positionX * getBounds().width());
        float y = (float) (p.positionY * getBounds().height());
        float size = innerBoxSize / 6;
        canvas.save();
        canvas.rotate((float) (360 * p.positionZ), x, y);
        canvas.drawRect(x - size, y - size, x + size, y + size, paintParticles);
        canvas.restore();
    }

    @Override
    protected void particleTick(Particle p, long timeSinceLastFrame) {
        p.positionZ += p.speed * timeSinceLastFrame;
        if (p.positionZ > 1) {
            p.positionZ -= 1;
        }
    }
}