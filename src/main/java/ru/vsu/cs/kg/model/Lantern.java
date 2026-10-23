package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;

import java.util.Random;

/**
 * Фонарный столб.
 * Имеет свой таймер мерцания (flicker) со случайной периодичностью.
 * Использует примитивы:
 * - Прямоугольники и полигоны для опоры и кронштейна
 * - Кривые / дуги (arc) для изгиба плафона и абажура
 * - Радиальный полупрозрачный градиент для светового конуса на земле
 */
public class Lantern implements Drawable {
    private final double x;
    private final double groundY;
    private final double height;
    private final double flickerSpeed;
    private final double flickerPhase;
    private final double glitchInterval;

    public Lantern(double x, double groundY, double height, Random rng) {
        this.x = x;
        this.groundY = groundY;
        this.height = height;
        this.flickerSpeed = 1.0 + rng.nextDouble() * 3.0;
        this.flickerPhase = rng.nextDouble() * Math.PI * 2;
        this.glitchInterval = 2.5 + rng.nextDouble() * 4.0;
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        // Расчёт мерцания: базовое синусоидальное + случайное импульсное затухание раз в glitchInterval
        double glitch = Math.sin(time * (Math.PI * 2 / glitchInterval) + flickerPhase);
        boolean isGlitching = glitch > 0.94; // короткое угасание

        double baseBrightness = 0.75 + 0.25 * Math.sin(time * flickerSpeed + flickerPhase);
        if (isGlitching) {
            baseBrightness *= (0.2 + 0.3 * Math.sin(time * 30)); // дрожание/искрение
        }

        double lampX = x;
        double lampY = groundY - height;

        // 1. Световое пятно на тротуаре/земле от фонаря (RadialGradient + Oval)
        if (baseBrightness > 0.2) {
            double coneRadiusX = 85.0;
            double coneRadiusY = 35.0;
            RadialGradient lightCone = new RadialGradient(
                    0, 0, lampX, groundY - 10, coneRadiusX, false, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.rgb(255, 230, 140, 0.45 * baseBrightness)),
                    new Stop(0.6, Color.rgb(255, 200, 100, 0.18 * baseBrightness)),
                    new Stop(1.0, Color.TRANSPARENT)
            );
            gc.setFill(lightCone);
            gc.fillOval(lampX - coneRadiusX, groundY - coneRadiusY, coneRadiusX * 2, coneRadiusY * 2);

            // Ореол вокруг самой лампы
            RadialGradient bulbGlow = new RadialGradient(
                    0, 0, lampX, lampY + 4, 30.0, false, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.rgb(255, 255, 200, 0.8 * baseBrightness)),
                    new Stop(0.4, Color.rgb(255, 220, 120, 0.35 * baseBrightness)),
                    new Stop(1.0, Color.TRANSPARENT)
            );
            gc.setFill(bulbGlow);
            gc.fillOval(lampX - 30, lampY - 26, 60, 60);
        }

        // 2. Чугунная основа фонаря (тумба у земли)
        gc.setFill(Color.web("#1c1d21"));
        gc.fillRect(x - 4, groundY - 14, 8, 14);
        gc.fillRect(x - 6, groundY - 4, 12, 4);

        // 3. Вертикальный столб
        gc.setStroke(Color.web("#2a2b30"));
        gc.setLineWidth(3.5);
        gc.strokeLine(x, groundY - 12, x, lampY + 10);

        // 4. Кронштейн / рожок фонаря (изгиб с помощью дуги или ломаной)
        gc.setLineWidth(2.5);
        gc.strokeArc(x - 8, lampY, 16, 16, 0, 180, ArcType.OPEN);

        // 5. Колпак/абажур светильника (трапеция / многоугольник)
        double[] polyX = {lampX - 8, lampX + 8, lampX + 5, lampX - 5};
        double[] polyY = {lampY + 2, lampY + 2, lampY - 5, lampY - 5};
        gc.setFill(Color.web("#202124"));
        gc.fillPolygon(polyX, polyY, 4);
        gc.setStroke(Color.web("#111214"));
        gc.setLineWidth(1.0);
        gc.strokePolygon(polyX, polyY, 4);

        // 6. Сама светящаяся лампочка (овал / дуга)
        Color bulbColor = isGlitching
                ? Color.rgb(180, 150, 80, 0.5)
                : Color.rgb(255, 245, 180, baseBrightness);
        gc.setFill(bulbColor);
        gc.fillOval(lampX - 4.5, lampY + 2, 9, 7);
        gc.setStroke(Color.web("#d1ba6b"));
        gc.setLineWidth(0.8);
        gc.strokeOval(lampX - 4.5, lampY + 2, 9, 7);
    }
}
