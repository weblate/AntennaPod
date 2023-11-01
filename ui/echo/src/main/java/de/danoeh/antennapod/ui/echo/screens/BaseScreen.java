package de.danoeh.antennapod.ui.echo.screens;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import java.util.ArrayList;

public abstract class BaseScreen extends Drawable {
    private final Paint paintBackground;
    protected final Paint paintParticles;
    private final Paint paintTextCredits;
    protected final ArrayList<Particle> particles = new ArrayList<>();

    public BaseScreen() {
        paintBackground = new Paint();
        paintParticles = new Paint();
        paintParticles.setColor(0xffffffff);
        paintParticles.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintParticles.setStyle(Paint.Style.FILL);
        paintParticles.setAlpha(35);
        paintTextCredits = new Paint();
        paintTextCredits.setColor(0xffffffff);
        paintTextCredits.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintTextCredits.setStyle(Paint.Style.FILL);
        paintTextCredits.setAlpha(200);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float width = getBounds().width();
        float height = getBounds().height();
        paintBackground.setShader(new LinearGradient(0, 0, 0, height, 0xff364ff3, 0xff16d0ff, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, height, paintBackground);

        final float innerBoxSize = 0.9f * Math.min(width, height);
        final float innerBoxX = (width - innerBoxSize) / 2;
        final float innerBoxY = (height - innerBoxSize) / 2;

        for (Particle p : particles) {
            drawParticle(canvas, p, innerBoxX, innerBoxY, innerBoxSize);
            particleTick(p);
        }

        drawInner(canvas, innerBoxX, innerBoxY, innerBoxSize);

        paintTextCredits.setTextSize(innerBoxSize / 30);
        paintTextCredits.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("#AntennaPodEcho", innerBoxX, innerBoxY + innerBoxSize, paintTextCredits);
        paintTextCredits.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("antennapod.org/s/echo", innerBoxX + innerBoxSize, innerBoxY + innerBoxSize, paintTextCredits);

        invalidateSelf();
    }

    protected abstract void particleTick(Particle p);

    protected abstract void drawInner(Canvas canvas, float innerBoxX, float innerBoxY, float innerBoxSize);

    protected abstract void drawParticle(@NonNull Canvas canvas, Particle p,
                                         float innerBoxX, float innerBoxY, float innerBoxSize);

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    protected static class Particle {
        double positionX;
        double positionY;
        double speed;

        public Particle(double positionX, double positionY, double speed) {
            this.positionX = positionX;
            this.positionY = positionY;
            this.speed = speed;
        }
    }
}