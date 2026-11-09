package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.Random;

public class Lantern implements Drawable {
    private static final double PULSE_BASE = 0.65;
    private static final double PULSE_AMPLITUDE = 0.35;
    private static final double GLITCH_THRESHOLD = 0.92;
    private static final double GLITCH_BASE = 0.12;
    private static final double GLITCH_AMPLITUDE = 0.20;
    private static final double GLITCH_FREQUENCY = 35.0;
    private static final double MIN_BRIGHTNESS = 0.05;
    private static final double MAX_BRIGHTNESS = 1.0;

    private static final double GROUND_GLOW_RADIUS_X = 36.0;
    private static final double GROUND_GLOW_RADIUS_X_AMPLITUDE = 4.0;
    private static final double GROUND_GLOW_RADIUS_Y = 16.0;
    private static final double GROUND_GLOW_RADIUS_Y_AMPLITUDE = 2.0;
    private static final double GROUND_GLOW_Y_OFFSET = -2.0;

    private static final double BULB_GLOW_RADIUS = 18.0;
    private static final double BULB_GLOW_Y_OFFSET = 6.0;

    private final double x;
    private final double groundY;
    private final double height;
    private final double speed;
    private final double phase;
    private final double glitchInterval;

    public Lantern(double x, double groundY, double height, Random rng) {
        this.x = x;
        this.groundY = groundY;
        this.height = height;
        this.speed = 1.8 + rng.nextDouble() * 2.5;
        this.phase = rng.nextDouble() * Math.PI * 2;
        this.glitchInterval = 2.5 + rng.nextDouble() * 3.5;
    }

    private double flickerBrightness(double time) {
        double pulse = PULSE_BASE + PULSE_AMPLITUDE * Math.sin(time * speed + phase);
        boolean glitch = Math.sin(time * (Math.PI * 2 / glitchInterval) + phase) > GLITCH_THRESHOLD;
        double brightness = glitch ? GLITCH_BASE + GLITCH_AMPLITUDE * Math.sin(time * GLITCH_FREQUENCY) : pulse;
        return Math.clamp(brightness, MIN_BRIGHTNESS, MAX_BRIGHTNESS);
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        double lampY = groundY - height;
        double brightness = flickerBrightness(time);

        drawGroundGlow(gc, time, brightness);
        drawBulbGlow(gc, lampY, brightness);
        drawPost(gc, lampY);
        drawLamp(gc, lampY, brightness);
    }

    private void drawGroundGlow(GraphicsContext gc, double time, double brightness) {
        double pulse = Math.sin(time * speed + phase);
        double radiusX = GROUND_GLOW_RADIUS_X + GROUND_GLOW_RADIUS_X_AMPLITUDE * pulse;
        double radiusY = GROUND_GLOW_RADIUS_Y + GROUND_GLOW_RADIUS_Y_AMPLITUDE * pulse;

        RadialGradient groundLight = new RadialGradient(
                0, 0, x, groundY + GROUND_GLOW_Y_OFFSET, radiusX, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(255, 235, 110, 0.35 * brightness)),
                new Stop(0.5, Color.rgb(255, 215, 80, 0.14 * brightness)),
                new Stop(1.0, Color.TRANSPARENT)
        );
        gc.setFill(groundLight);
        gc.fillOval(x - radiusX, groundY - radiusY, radiusX * 2, radiusY * 2);
    }

    private void drawBulbGlow(GraphicsContext gc, double lampY, double brightness) {
        RadialGradient bulbGlow = new RadialGradient(
                0, 0, x, lampY + BULB_GLOW_Y_OFFSET, BULB_GLOW_RADIUS, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(255, 245, 160, 0.50 * brightness)),
                new Stop(0.6, Color.rgb(255, 220, 90, 0.15 * brightness)),
                new Stop(1.0, Color.TRANSPARENT)
        );
        gc.setFill(bulbGlow);
        gc.fillOval(x - BULB_GLOW_RADIUS, lampY - 12, BULB_GLOW_RADIUS * 2, BULB_GLOW_RADIUS * 2);
    }

    private void drawPost(GraphicsContext gc, double lampY) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3.5);
        gc.strokeLine(x, groundY, x, lampY + 4);

        gc.setFill(Color.BLACK);
        gc.fillRect(x - 3.5, groundY - 8, 7, 8);
        gc.fillRect(x - 6, lampY, 12, 4);
    }

    private void drawLamp(GraphicsContext gc, double lampY, double brightness) {
        gc.setFill(Color.rgb(255, 235, 90, brightness));
        gc.fillRect(x - 4.5, lampY + 4, 9, 4);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokeRect(x - 4.5, lampY + 4, 9, 4);
    }
}
