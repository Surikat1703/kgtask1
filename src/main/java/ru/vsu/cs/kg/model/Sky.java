package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sky implements Drawable {
    private static final Color SKY_COLOR = Color.web("#0e1329");
    private static final Color MOON_COLOR = Color.web("#d7dadf");
    private static final Color MOON_OUTLINE = Color.web("#10121a");
    private static final Color CRATER_COLOR = Color.web("#b8bdc7");
    private static final Color CRATER_SHADOW = Color.web("#9ea4b0");

    private static final double MOON_CENTER_X = 212.0;
    private static final double MOON_CENTER_Y = 56.0;
    private static final double MOON_RADIUS = 22.0;
    private static final double MOON_CLEARANCE = 4.0;

    private static final double STAR_BAND_HEIGHT = 250.0;
    private static final double STAR_SIZE_MIN = 1.0;
    private static final double STAR_SIZE_RANGE = 1.5;
    private static final double STAR_SPEED_MIN = 1.0;
    private static final double STAR_SPEED_RANGE = 3.5;
    private static final double STAR_ALPHA_MIN = 0.35;
    private static final double STAR_ALPHA_RANGE = 0.65;

    private static final double MOON_OUTLINE_WIDTH = 1.2;
    private static final double CRATER_OUTLINE_WIDTH = 0.6;

    private record Crater(double dx, double dy, double width, double height, boolean outlined) {}

    private static final List<Crater> CRATERS = List.of(
            new Crater(-9, -11, 6.5, 6.0, true),
            new Crater(-14, -2, 7.5, 7.0, true),
            new Crater(-6, 4, 8.5, 8.0, true),
            new Crater(4, -8, 6.0, 5.5, false),
            new Crater(5, 2, 7.0, 6.5, true),
            new Crater(-4, -5, 4.5, 4.0, false),
            new Crater(9, 9, 4.5, 4.0, false)
    );

    private record Star(double x, double y, double size, double speed, double phase) {}

    private final double width;
    private final double height;
    private final List<Star> stars = new ArrayList<>();

    public Sky(double width, double height, int starCount, Random rng) {
        this.width = width;
        this.height = height;
        createStars(starCount, rng);
    }

    private void createStars(int starCount, Random rng) {
        for (int i = 0; i < starCount; i++) {
            double starX = rng.nextDouble() * width;
            double starY = rng.nextDouble() * STAR_BAND_HEIGHT;
            if (!isClearOfMoon(starX, starY)) {
                continue;
            }

            double size = STAR_SIZE_MIN + rng.nextDouble() * STAR_SIZE_RANGE;
            double speed = STAR_SPEED_MIN + rng.nextDouble() * STAR_SPEED_RANGE;
            double phase = rng.nextDouble() * Math.PI * 2;
            stars.add(new Star(starX, starY, size, speed, phase));
        }
    }

    private boolean isClearOfMoon(double starX, double starY) {
        return Math.hypot(starX - MOON_CENTER_X, starY - MOON_CENTER_Y)
                >= MOON_RADIUS + MOON_CLEARANCE;
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        gc.setFill(SKY_COLOR);
        gc.fillRect(0, 0, width, height);

        drawStars(gc, time);
        drawMoon(gc);
    }

    private void drawStars(GraphicsContext gc, double time) {
        for (Star star : stars) {
            double twinkle = 0.5 + 0.5 * Math.sin(time * star.speed() + star.phase());
            double alpha = STAR_ALPHA_MIN + STAR_ALPHA_RANGE * twinkle;
            gc.setFill(Color.rgb(255, 255, 255, alpha));
            gc.fillOval(star.x(), star.y(), star.size(), star.size());
        }
    }

    private void drawMoon(GraphicsContext gc) {
        double moonX = MOON_CENTER_X - MOON_RADIUS;
        double moonY = MOON_CENTER_Y - MOON_RADIUS;
        double moonSize = MOON_RADIUS * 2;

        gc.setFill(MOON_COLOR);
        gc.fillOval(moonX, moonY, moonSize, moonSize);

        gc.setStroke(MOON_OUTLINE);
        gc.setLineWidth(MOON_OUTLINE_WIDTH);
        gc.strokeOval(moonX, moonY, moonSize, moonSize);

        drawCraters(gc);
    }

    private void drawCraters(GraphicsContext gc) {
        gc.setFill(CRATER_COLOR);
        for (Crater crater : CRATERS) {
            gc.fillOval(MOON_CENTER_X + crater.dx(), MOON_CENTER_Y + crater.dy(),
                    crater.width(), crater.height());
        }

        gc.setStroke(CRATER_SHADOW);
        gc.setLineWidth(CRATER_OUTLINE_WIDTH);
        for (Crater crater : CRATERS) {
            if (crater.outlined()) {
                gc.strokeOval(MOON_CENTER_X + crater.dx(), MOON_CENTER_Y + crater.dy(),
                        crater.width(), crater.height());
            }
        }
    }
}
