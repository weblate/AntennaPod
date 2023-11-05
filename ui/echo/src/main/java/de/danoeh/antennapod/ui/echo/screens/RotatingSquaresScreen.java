package de.danoeh.antennapod.ui.echo.screens;

import android.graphics.Canvas;
import androidx.annotation.NonNull;

public class RotatingSquaresScreen extends BaseScreen {
    public RotatingSquaresScreen() {
        for (int i = 0; i < 16; i++) {
            particles.add(new Particle(0.33 * (i % 4) - 0.05 + 0.2 * Math.random(),
                    0.33 * (float) (i / 4) - 0.05 + 0.2 * Math.random(), Math.random()));
        }
    }

    @Override
    protected void drawParticle(@NonNull Canvas canvas, Particle p,
                                float innerBoxX, float innerBoxY, float innerBoxSize) {
        float x = (float) (p.positionX * getBounds().width());
        float y = (float) (p.positionY * getBounds().height());
        float size = innerBoxSize / 6;
        canvas.save();
        canvas.rotate((float) (360 * p.speed), x, y);
        canvas.drawRect(x - size, y - size, x + size, y + size, paintParticles);
        canvas.restore();
    }

    @Override
    protected void particleTick(Particle p, long timeSinceLastFrame) {
        p.speed += 0.0001 * timeSinceLastFrame;
        if (p.speed > 1) {
            p.speed -= 1;
        }
    }
}