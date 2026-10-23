package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.Random;

/**
 * Окно хрущёвки.
 * Каждое окно имеет:
 * - Случайный тип формы (из 3 видов):
 *     1. TYPE_TWO_LEAF: двустворчатое окно (створка слева и форточка/большая справа)
 *     2. TYPE_THREE_LEAF: трёхстворчатое окно со средней широкой створкой
 *     3. TYPE_ARCH_CURTAIN: окно с декоративной полукруглой аркой/шторой сверху (использование arc!)
 * - Случайный цвет света:
 *     - Тёмно-синий (выключен свет / синеватый отсвет ночи)
 *     - Фиолетовый / маджента (фитолампа для рассады / неоновый свет)
 *     - Тёплый жёлтый / янтарный (уютный домашний свет)
 */
public class WindowElement {
    public enum WindowType {
        TWO_LEAF,
        THREE_LEAF,
        ARCH_CURTAIN
    }

    private final double relX;
    private final double relY;
    private final double width;
    private final double height;
    private final WindowType type;
    private final Color lightColor;
    private final boolean hasCurtain;

    public WindowElement(double relX, double relY, double width, double height, Random rng) {
        this.relX = relX;
        this.relY = relY;
        this.width = width;
        this.height = height;

        // Выбор одного из трёх типов
        int t = rng.nextInt(3);
        if (t == 0) {
            this.type = WindowType.TWO_LEAF;
        } else if (t == 1) {
            this.type = WindowType.THREE_LEAF;
        } else {
            this.type = WindowType.ARCH_CURTAIN;
        }

        // Случайный цвет света: тёмно-синий, фиолетовый или жёлтый
        int c = rng.nextInt(3);
        if (c == 0) {
            // Тёмно-синий (почти тёмное окно / ночной отсвет)
            this.lightColor = Color.rgb(18, 32, 68);
        } else if (c == 1) {
            // Фиолетовый (фитолампа на подоконнике)
            this.lightColor = Color.rgb(180, 80, 215);
        } else {
            // Тёплый жёлтый
            this.lightColor = Color.rgb(255, 215, 64);
        }

        this.hasCurtain = rng.nextBoolean();
    }

    public void draw(GraphicsContext gc, double baseX, double baseY) {
        double x = baseX + relX;
        double y = baseY + relY;

        // Внешняя тёмная окантовка/ниша окна
        gc.setFill(Color.web("#1c1c1f"));
        gc.fillRect(x - 2, y - 2, width + 4, height + 4);

        // Подоконник (линия / узкий прямоугольник)
        gc.setFill(Color.web("#75777a"));
        gc.fillRect(x - 3, y + height, width + 6, 2.5);

        // Рама окна (белая/светло-серая)
        gc.setFill(Color.web("#e6ebed"));
        gc.fillRect(x, y, width, height);

        // Светящееся стекло
        double innerPad = 2.0;
        double glassW = width - innerPad * 2;
        double glassH = height - innerPad * 2;
        gc.setFill(lightColor);
        gc.fillRect(x + innerPad, y + innerPad, glassW, glassH);

        // Рисование створок в зависимости от формы окна
        gc.setStroke(Color.web("#222326"));
        gc.setLineWidth(1.2);

        if (type == WindowType.TWO_LEAF) {
            // Двустворчатое окно: вертикальный импост и форточка в правой части
            double splitX = x + innerPad + glassW * 0.42;
            gc.strokeLine(splitX, y + innerPad, splitX, y + innerPad + glassH);
            // Горизонтальная форточка справа
            double fortY = y + innerPad + glassH * 0.35;
            gc.strokeLine(splitX, fortY, x + innerPad + glassW, fortY);

        } else if (type == WindowType.THREE_LEAF) {
            // Трёхстворчатое окно: две вертикальные перемычки
            double w1 = glassW / 3.0;
            gc.strokeLine(x + innerPad + w1, y + innerPad, x + innerPad + w1, y + innerPad + glassH);
            gc.strokeLine(x + innerPad + 2 * w1, y + innerPad, x + innerPad + 2 * w1, y + innerPad + glassH);
            // Горизонтальная перекладина сверху
            gc.strokeLine(x + innerPad, y + innerPad + glassH * 0.3, x + innerPad + glassW, y + innerPad + glassH * 0.3);

        } else {
            // Окно с аркой / декоративным полукруглым сводом (примитив ARC!)
            // Рисуем арку шторы/фрамуги в верхней части
            gc.setFill(Color.rgb(20, 20, 25, 0.45));
            gc.fillArc(x + innerPad, y + innerPad - glassH * 0.2, glassW, glassH * 0.8, 180, 180, ArcType.CHORD);
            gc.strokeArc(x + innerPad, y + innerPad - glassH * 0.2, glassW, glassH * 0.8, 180, 180, ArcType.CHORD);

            // Т-образная рама
            double midX = x + innerPad + glassW / 2.0;
            gc.strokeLine(midX, y + innerPad, midX, y + innerPad + glassH);
            gc.strokeLine(x + innerPad, y + innerPad + glassH * 0.4, x + innerPad + glassW, y + innerPad + glassH * 0.4);
        }

        // Шторы/жалюзи при включенном свете (если есть)
        if (hasCurtain && (lightColor.getGreen() > 0.3 || lightColor.getRed() > 0.5)) {
            gc.setStroke(Color.rgb(50, 40, 40, 0.5));
            gc.setLineWidth(0.8);
            // Несколько горизонтальных линий жалюзи
            for (double ly = y + innerPad + 3; ly < y + innerPad + glassH * 0.5; ly += 3.5) {
                gc.strokeLine(x + innerPad + 1, ly, x + innerPad + glassW - 1, ly);
            }
        }

        // Тонкий внутренний контур стекла
        gc.setStroke(Color.web("#38393d"));
        gc.setLineWidth(0.8);
        gc.strokeRect(x + innerPad, y + innerPad, glassW, glassH);
    }
}
