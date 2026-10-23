package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Фонарный столб:
 * - СТОЛБ ЧЁРНЫЙ.
 * - НА ЗЕМЛЕ НИКАКИХ ПЯТЕН, НАСЫПЕЙ И КРУГОВ НЕТ ВООБЩЕ (полностью удалено).
 * - Мерцает ТОЛЬКО лампа на самом столбе под козырьком.
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

        // Расчёт мерцания лампы
        double pulse = 0.65 + 0.35 * Math.sin(time * speed + phase);
        boolean glitch = Math.sin(time * (Math.PI * 2 / glitchInterval) + phase) > 0.92;
        double brightness = glitch ? (0.15 + 0.25 * Math.sin(time * 30.0)) : pulse;
        brightness = Math.clamp(brightness, 0.05, 1.0);

        // 1. Чёрный столб
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3.5);
        gc.strokeLine(x, groundY, x, lampY + 4);

        // Тумба опоры
        gc.setFill(Color.BLACK);
        gc.fillRect(x - 3.5, groundY - 8, 7, 8);

        // Чёрный козырёк светильника
        gc.fillRect(x - 6, lampY, 12, 4);

        // 2. Светящаяся мерцающая лампа под плафоном (и ничего больше на земле!)
        Color bulbColor = Color.rgb(255, 235, 90, brightness);
        gc.setFill(bulbColor);
        gc.fillRect(x - 4.5, lampY + 4, 9, 4);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokeRect(x - 4.5, lampY + 4, 9, 4);
    }
}
