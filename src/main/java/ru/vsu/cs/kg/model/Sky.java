package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Небо со случайно расположенными и мерцающими звёздами и Луной с кратерами.
 * Использует примитивы:
 * - Прямоугольник с градиентом (LinearGradient) для ночного неба
 * - Эллипсы/круги для Луны и кратеров (fillOval, strokeOval)
 * - Радиальный градиент (RadialGradient) для свечения Луны
 * - Точки/маленькие эллипсы со случайной яркостью для звёзд
 */
public class Sky implements Drawable {
    private final double width;
    private final double height;
    private final double moonX;
    private final double moonY;
    private final double moonRadius;
    private final List<Star> stars = new ArrayList<>();

    public static class Star {
        public double x;
        public double y;
        public double size;
        public double blinkSpeed;
        public double phase;

        public Star(double x, double y, double size, double blinkSpeed, double phase) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.blinkSpeed = blinkSpeed;
            this.phase = phase;
        }
    }

    public Sky(double width, double height, int starCount, Random rng) {
        this.width = width;
        this.height = height;
        // Фиксированное положение Луны согласно эскизу (в левой верхней части)
        this.moonX = width * 0.20;
        this.moonY = height * 0.12;
        this.moonRadius = 30.0;

        // Генерация случайных звёзд по небу
        for (int i = 0; i < starCount; i++) {
            double sx = rng.nextDouble() * width;
            double sy = rng.nextDouble() * (height * 0.65); // Звёзды в верхней части неба
            double size = 1.0 + rng.nextDouble() * 2.2;
            double speed = 1.5 + rng.nextDouble() * 4.0;
            double phase = rng.nextDouble() * Math.PI * 2;
            stars.add(new Star(sx, sy, size, speed, phase));
        }
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        // 1. Градиент ночного неба
        LinearGradient skyGrad = new LinearGradient(
                0, 0, 0, height * 0.7, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#070913")),
                new Stop(0.5, Color.web("#0d1326")),
                new Stop(1.0, Color.web("#171b30"))
        );
        gc.setFill(skyGrad);
        gc.fillRect(0, 0, width, height);

        // 2. Отрисовка мерцающих звёзд
        for (Star star : stars) {
            double brightness = 0.35 + 0.65 * (0.5 + 0.5 * Math.sin(time * star.blinkSpeed + star.phase));
            gc.setFill(Color.rgb(240, 245, 255, brightness));
            gc.fillOval(star.x, star.y, star.size, star.size);
            // Если звезда крупная — рисуем лёгкие лучики (крестик) линиями
            if (star.size > 2.6 && brightness > 0.8) {
                gc.setStroke(Color.rgb(255, 255, 255, brightness * 0.4));
                gc.setLineWidth(0.8);
                gc.strokeLine(star.x - 2, star.y + star.size / 2, star.x + star.size + 2, star.y + star.size / 2);
                gc.strokeLine(star.x + star.size / 2, star.y - 2, star.x + star.size / 2, star.y + star.size + 2);
            }
        }

        // 3. Свечение Луны (Радиальный градиент)
        double haloRadius = moonRadius * 2.5;
        RadialGradient moonHalo = new RadialGradient(
                0, 0, moonX, moonY, haloRadius, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(220, 230, 245, 0.45)),
                new Stop(0.5, Color.rgb(200, 215, 240, 0.15)),
                new Stop(1.0, Color.TRANSPARENT)
        );
        gc.setFill(moonHalo);
        gc.fillOval(moonX - haloRadius, moonY - haloRadius, haloRadius * 2, haloRadius * 2);

        // 4. Тело Луны (fillOval)
        gc.setFill(Color.web("#e1e5ea"));
        gc.fillOval(moonX - moonRadius, moonY - moonRadius, moonRadius * 2, moonRadius * 2);

        // Контур Луны
        gc.setStroke(Color.web("#9aa4b2"));
        gc.setLineWidth(1.5);
        gc.strokeOval(moonX - moonRadius, moonY - moonRadius, moonRadius * 2, moonRadius * 2);

        // Кратеры на Луне
        gc.setFill(Color.web("#c4cbd4"));
        gc.fillOval(moonX - 8, moonY - 10, 10, 8);
        gc.fillOval(moonX + 6, moonY - 4, 12, 11);
        gc.fillOval(moonX - 12, moonY + 7, 14, 9);
        gc.fillOval(moonX + 4, moonY + 12, 7, 7);
        gc.fillOval(moonX - 2, moonY + 2, 5, 5);

        // Тонкие ободки кратеров
        gc.setStroke(Color.web("#b1b9c4"));
        gc.setLineWidth(0.8);
        gc.strokeOval(moonX - 8, moonY - 10, 10, 8);
        gc.strokeOval(moonX + 6, moonY - 4, 12, 11);
        gc.strokeOval(moonX - 12, moonY + 7, 14, 9);
    }
}
