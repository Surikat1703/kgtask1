package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Фонарный столб точно по концепту:
 * - Чёрный вертикальный столб
 * - Чёрный светильник наверху с жёлтым светящимся плафоном
 * - Мерцает со случайной периодичностью
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

        // Расчёт мерцания
        double pulse = 0.7 + 0.3 * Math.sin(time * speed + phase);
        boolean glitch = Math.sin(time * (Math.PI * 2 / glitchInterval) + phase) > 0.93;
        double brightness = glitch ? (0.2 + 0.3 * Math.sin(time * 40.0)) : pulse;
        brightness = Math.clamp(brightness, 0.05, 1.0);

        // 1. Чёрный столб
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3.5);
        gc.strokeLine(x, groundY, x, lampY + 4);

        // Расширение опоры внизу
        gc.setFill(Color.BLACK);
        gc.fillRect(x - 3.5, groundY - 8, 7, 8);

        // 2. Светильник наверху (прямоугольный/Т-образный плафон как на концепте)
        gc.fillRect(x - 6, lampY, 12, 5);

        // Светящаяся лампа внизу светильника
        Color glowColor = Color.rgb(255, 230, 80, brightness);
        gc.setFill(glowColor);
        gc.fillRect(x - 4.5, lampY + 5, 9, 4);

        // 3. Мягкое круглое пятно света на тротуаре от фонаря
        gc.setFill(Color.rgb(255, 235, 120, 0.25 * brightness));
        gc.fillOval(x - 28, groundY - 14, 56, 26);
    }
}
