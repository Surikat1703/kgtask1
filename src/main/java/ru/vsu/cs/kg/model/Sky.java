package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Небо точь-в-точь по концепту:
 * - Глубокий ровный тёмно-синий/чёрно-синий цвет (#0e1329)
 * - Луна: круглая, серая (#d5d8de), с 7 кратерами в точных координатах эскиза
 * - Звёзды: мелкие точки (#ffffff) случайного расположения и количества, мерцающие по синусоиде
 */
public class Sky implements Drawable {
    private final double width;
    private final double height;
    private final double moonCenterX = 212.0;
    private final double moonCenterY = 56.0;
    private final double moonRadius = 22.0;
    private final List<Star> stars = new ArrayList<>();

    public static class Star {
        double x;
        double y;
        double size;
        double speed;
        double phase;

        public Star(double x, double y, double size, double speed, double phase) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.phase = phase;
        }
    }

    public Sky(double width, double height, int starCount, Random rng) {
        this.width = width;
        this.height = height;

        // Генерация случайных звёзд по небу
        for (int i = 0; i < starCount; i++) {
            double sx = rng.nextDouble() * width;
            double sy = rng.nextDouble() * 250.0; // небо до крыш домов
            // Исключаем попадание прямо внутрь диска луны
            if (Math.hypot(sx - moonCenterX, sy - moonCenterY) < moonRadius + 4) {
                continue;
            }
            double size = 1.0 + rng.nextDouble() * 1.5;
            double speed = 1.0 + rng.nextDouble() * 3.5;
            double phase = rng.nextDouble() * Math.PI * 2;
            stars.add(new Star(sx, sy, size, speed, phase));
        }
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        // 1. Цвет неба концепта: плотный глубокий ночной цвет
        gc.setFill(Color.web("#0e1329"));
        gc.fillRect(0, 0, width, height);

        // 2. Звёзды (случайное положение, мерцание)
        for (Star s : stars) {
            double alpha = 0.35 + 0.65 * (0.5 + 0.5 * Math.sin(time * s.speed + s.phase));
            gc.setFill(Color.rgb(255, 255, 255, alpha));
            gc.fillOval(s.x, s.y, s.size, s.size);
        }

        // 3. Луна (fillOval) в фиксированном положении
        gc.setFill(Color.web("#d7dadf"));
        gc.fillOval(moonCenterX - moonRadius, moonCenterY - moonRadius, moonRadius * 2, moonRadius * 2);

        // Контур Луны
        gc.setStroke(Color.web("#10121a"));
        gc.setLineWidth(1.2);
        gc.strokeOval(moonCenterX - moonRadius, moonCenterY - moonRadius, moonRadius * 2, moonRadius * 2);

        // Кратеры Луны как на эскизе (серо-голубоватые пятна)
        gc.setFill(Color.web("#b8bdc7"));
        gc.fillOval(moonCenterX - 9, moonCenterY - 11, 6.5, 6.0);
        gc.fillOval(moonCenterX - 14, moonCenterY - 2, 7.5, 7.0);
        gc.fillOval(moonCenterX - 6, moonCenterY + 4, 8.5, 8.0);
        gc.fillOval(moonCenterX + 4, moonCenterY - 8, 6.0, 5.5);
        gc.fillOval(moonCenterX + 5, moonCenterY + 2, 7.0, 6.5);
        gc.fillOval(moonCenterX - 4, moonCenterY - 5, 4.5, 4.0);
        gc.fillOval(moonCenterX + 9, moonCenterY + 9, 4.5, 4.0);

        // Тонкие внутренние тени кратеров
        gc.setStroke(Color.web("#9ea4b0"));
        gc.setLineWidth(0.6);
        gc.strokeOval(moonCenterX - 9, moonCenterY - 11, 6.5, 6.0);
        gc.strokeOval(moonCenterX - 14, moonCenterY - 2, 7.5, 7.0);
        gc.strokeOval(moonCenterX - 6, moonCenterY + 4, 8.5, 8.0);
        gc.strokeOval(moonCenterX + 5, moonCenterY + 2, 7.0, 6.5);
    }
}
