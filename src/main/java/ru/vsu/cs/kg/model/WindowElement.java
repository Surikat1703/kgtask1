package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Окно хрущёвки со строго 4 формами окон:
 * 1. SINGLE_VERTICAL: Окно с одной сплошной вертикальной линией по центру
 * 2. FULL_CROSS: Окно крестом (вертикальная линия + горизонтальная линия)
 * 3. CROSS_NO_LEFT: Крест без левой половины горизонтальной линии (линия только справа)
 * 4. CROSS_NO_RIGHT: Крест без правой половины горизонтальной линии (линия только слева)
 */
public class WindowElement {
    public enum WindowType {
        SINGLE_VERTICAL, // Одна вертикальная линия
        FULL_CROSS,      // Полный крест
        CROSS_NO_LEFT,   // Крест без левой части горизонтали (только справа)
        CROSS_NO_RIGHT   // Крест без правой части горизонтали (только слева)
    }

    private final double relX;
    private final double relY;
    private final double width;
    private final double height;
    private WindowType type;
    private final Color glassColor;

    public WindowElement(double relX, double relY, double width, double height, Random rng) {
        this.relX = relX;
        this.relY = relY;
        this.width = width;
        this.height = height;

        // Случайный выбор одной из 4 форм
        int t = rng.nextInt(4);
        if (t == 0) {
            this.type = WindowType.SINGLE_VERTICAL;
        } else if (t == 1) {
            this.type = WindowType.FULL_CROSS;
        } else if (t == 2) {
            this.type = WindowType.CROSS_NO_LEFT;
        } else {
            this.type = WindowType.CROSS_NO_RIGHT;
        }

        // Случайный цвет: темно-синий, фиолетовый или желтый
        int c = rng.nextInt(3);
        if (c == 0) {
            this.glassColor = Color.web("#0e1236"); // тёмно-синий
        } else if (c == 1) {
            this.glassColor = Color.web("#be3ec9"); // фиолетовый
        } else {
            this.glassColor = Color.web("#ffe600"); // жёлтый
        }
    }

    /**
     * Переключение формы окна по кругу (1 -> 2 -> 3 -> 4 -> 1)
     */
    public void cycleType() {
        switch (type) {
            case SINGLE_VERTICAL -> type = WindowType.FULL_CROSS;
            case FULL_CROSS -> type = WindowType.CROSS_NO_LEFT;
            case CROSS_NO_LEFT -> type = WindowType.CROSS_NO_RIGHT;
            case CROSS_NO_RIGHT -> type = WindowType.SINGLE_VERTICAL;
        }
    }

    public boolean contains(double parentX, double parentY, double px, double py) {
        double absX = parentX + relX;
        double absY = parentY + relY;
        return px >= absX && px <= absX + width && py >= absY && py <= absY + height;
    }

    public void draw(GraphicsContext gc, double baseX, double baseY) {
        double x = baseX + relX;
        double y = baseY + relY;

        // Внешняя чёрная ниша окна
        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, width, height);

        // Цветное стекло
        double pad = 2.0;
        double gw = width - pad * 2;
        double gh = height - pad * 2;

        gc.setFill(glassColor);
        gc.fillRect(x + pad, y + pad, gw, gh);

        // Белая рама и переплёты
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.2);
        gc.strokeRect(x + pad + 0.5, y + pad + 0.5, gw - 1.0, gh - 1.0);

        double midX = x + pad + gw * 0.5;
        double midY = y + pad + gh * 0.40; // Горизонтальная планка чуть выше середины (классическая форточка)

        // 1. Всегда есть вертикальная линия по центру
        gc.strokeLine(midX, y + pad, midX, y + pad + gh);

        // 2. Горизонтальные планки согласно 4 типам
        switch (type) {
            case SINGLE_VERTICAL -> {
                // Только вертикальная линия, горизонтальной нет
            }
            case FULL_CROSS -> {
                // Полный крест (линия слева и справа)
                gc.strokeLine(x + pad, midY, x + pad + gw, midY);
            }
            case CROSS_NO_LEFT -> {
                // Крест без левой половины (линия только СПРАВА)
                gc.strokeLine(midX, midY, x + pad + gw, midY);
            }
            case CROSS_NO_RIGHT -> {
                // Крест без правой половины (линия только СЛЕВА)
                gc.strokeLine(x + pad, midY, midX, midY);
            }
        }
    }
}
