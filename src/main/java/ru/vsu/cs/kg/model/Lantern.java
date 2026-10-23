package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Фонарный столб:
 * - Убран второй/статичный источник света: остался единственный мерцающий свет
 * - Мерцает лампа и синхронно с ней мерцает пятно света под фонарём
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
        this.speed = 1.5 + rng.nextDouble() * 3.0;
        this.phase = rng.nextDouble() * Math.PI * 2;
        this.glitchInterval = 2.0 + rng.nextDouble() * 4.0;
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        double lampY = groundY - height;

        // Расчёт единого коэффициента мерцания
        double pulse = 0.7 + 0.3 * Math.sin(time * speed + phase);
        boolean glitch = Math.sin(time * (Math.PI * 2 / glitchInterval) + phase) > 0.93;
        double brightness = glitch ? (0.15 + 0.25 * Math.sin(time * 35.0)) : pulse;
        brightness = Math.clamp(brightness, 0.05, 1.0);

        // 1. Опоры фонаря (чёрный столб)
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3.5);
        gc.strokeLine(x, groundY, x, lampY + 4);

        // Тумба опоры
        gc.setFill(Color.BLACK);
        gc.fillRect(x - 3.5, groundY - 8, 7, 8);

        // Чёрный козырёк светильника
        gc.fillRect(x - 6, lampY, 12, 4);

        // 2. ЕДИНСТВЕННЫЙ свет фонаря:
        // Сама лампа
        Color bulbColor = Color.rgb(255, 230, 80, brightness);
        gc.setFill(bulbColor);
        gc.fillRect(x - 4.5, lampY + 4, 9, 4);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokeRect(x - 4.5, lampY + 4, 9, 4);

        // Пятно света на тротуаре, строго синхронизированное с лампой
        gc.setFill(Color.rgb(255, 230, 80, 0.22 * brightness));
        gc.fillOval(x - 24, groundY - 12, 48, 22);
    }
}
