package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.Random;

/**
 * Фонарный столб:
 * - Столб чёрный, лампа под плафоном.
 * - НАСТОЯЩИЙ анимированный свет: мягкое рассеянное пятно света под фонарём,
 *   которое плавно и синхронно пульсирует и мерцает вместе с лампой.
 * - Никаких лишних статичных пятен.
 */
public class Lantern implements Drawable {
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

    @Override
    public void draw(GraphicsContext gc, double time) {
        double lampY = groundY - height;

        // Расчёт пульсации и мерцания
        double pulse = 0.65 + 0.35 * Math.sin(time * speed + phase);
        boolean glitch = Math.sin(time * (Math.PI * 2 / glitchInterval) + phase) > 0.92;
        double brightness = glitch ? (0.15 + 0.25 * Math.sin(time * 30.0)) : pulse;
        brightness = Math.clamp(brightness, 0.05, 1.0);

        // 1. АНИМИРОВАННОЕ световое пятно на тротуаре под фонарём (RadialGradient с альфа-каналом)
        // Мерцает строго вместе с лампой!
        double radiusX = 35.0;
        double radiusY = 16.0;
        RadialGradient groundGlow = new RadialGradient(
                0, 0, x, groundY - 4, radiusX, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(255, 230, 100, 0.38 * brightness)),
                new Stop(0.6, Color.rgb(255, 210, 80, 0.15 * brightness)),
                new Stop(1.0, Color.TRANSPARENT)
        );
        gc.setFill(groundGlow);
        gc.fillOval(x - radiusX, groundY - radiusY, radiusX * 2, radiusY * 2);

        // 2. Чёрный столб фонаря
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3.5);
        gc.strokeLine(x, groundY, x, lampY + 4);

        // Чугунная опора у земли
        gc.setFill(Color.BLACK);
        gc.fillRect(x - 3.5, groundY - 8, 7, 8);

        // Чёрный козырёк светильника
        gc.fillRect(x - 6, lampY, 12, 4);

        // 3. Светящаяся мерцающая лампа под плафоном
        Color bulbColor = Color.rgb(255, 235, 90, brightness);
        gc.setFill(bulbColor);
        gc.fillRect(x - 4.5, lampY + 4, 9, 4);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokeRect(x - 4.5, lampY + 4, 9, 4);
    }
}
