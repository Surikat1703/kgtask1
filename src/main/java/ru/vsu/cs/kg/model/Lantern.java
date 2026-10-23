package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.Random;

/**
 * Фонарный столб:
 * - Чёрный столб и козырёк
 * - Мерцающая жёлтая лампа
 * - ДИНАМИЧЕСКОЕ ОСВЕЩЕНИЕ: мягкий световой конус под фонарём на тротуаре,
 *   яркость и радиус которого в реальном времени анимированы и пульсируют
 *   строго синхронно с мерцанием лампы.
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

        // Расчёт мерцания
        double pulse = 0.65 + 0.35 * Math.sin(time * speed + phase);
        boolean glitch = Math.sin(time * (Math.PI * 2 / glitchInterval) + phase) > 0.92;
        double brightness = glitch ? (0.12 + 0.20 * Math.sin(time * 35.0)) : pulse;
        brightness = Math.clamp(brightness, 0.05, 1.0);

        // 1. ДИНАМИЧЕСКИЙ СВЕТ НА ТРОТУАРЕ ПОД ФОНАРЁМ
        // Пульсирует синхронно с лампой (RadialGradient с мягким спадом)
        double radiusX = 36.0 + 4.0 * Math.sin(time * speed + phase);
        double radiusY = 16.0 + 2.0 * Math.sin(time * speed + phase);
        RadialGradient dynamicLight = new RadialGradient(
                0, 0, x, groundY - 2, radiusX, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(255, 235, 110, 0.35 * brightness)),
                new Stop(0.5, Color.rgb(255, 215, 80, 0.14 * brightness)),
                new Stop(1.0, Color.TRANSPARENT)
        );
        gc.setFill(dynamicLight);
        gc.fillOval(x - radiusX, groundY - radiusY, radiusX * 2, radiusY * 2);

        // Мягкий ореол вокруг самой лампы на столбе
        RadialGradient bulbGlow = new RadialGradient(
                0, 0, x, lampY + 6, 18.0, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(255, 245, 160, 0.50 * brightness)),
                new Stop(0.6, Color.rgb(255, 220, 90, 0.15 * brightness)),
                new Stop(1.0, Color.TRANSPARENT)
        );
        gc.setFill(bulbGlow);
        gc.fillOval(x - 18, lampY - 12, 36, 36);

        // 2. Чёрный столб
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3.5);
        gc.strokeLine(x, groundY, x, lampY + 4);

        // Тумба опоры у основания
        gc.setFill(Color.BLACK);
        gc.fillRect(x - 3.5, groundY - 8, 7, 8);

        // Чёрный светильник/козырёк
        gc.fillRect(x - 6, lampY, 12, 4);

        // 3. Лампа фонаря (мерцает)
        Color bulbColor = Color.rgb(255, 235, 90, brightness);
        gc.setFill(bulbColor);
        gc.fillRect(x - 4.5, lampY + 4, 9, 4);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokeRect(x - 4.5, lampY + 4, 9, 4);
    }
}
