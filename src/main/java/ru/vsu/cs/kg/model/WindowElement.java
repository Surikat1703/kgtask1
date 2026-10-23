package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.Random;

/**
 * Окно хрущёвки:
 * - Увеличенный читаемый размер (как на концепте: крупнее, выразительнее)
 * - Возможность динамически менять форму окна (cycleType)
 * - 3 формы:
 *     1. Трёхстворчатое окно (две вертикальные белые стойки и верхняя форточка)
 *     2. Двустворчатое окно с форточкой
 *     3. Окно с арочной фрамугой сверху (примитив ARC)
 */
public class WindowElement {
    public enum WindowType {
        CONCEPT_TRIPLE,   // 3 створки (оригинал эскиза)
        DOUBLE_CASEMENT,  // 2 створки + форточка
        ARCH_FRAME        // Арочный свод
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

        int t = rng.nextInt(3);
        if (t == 0) {
            this.type = WindowType.CONCEPT_TRIPLE;
        } else if (t == 1) {
            this.type = WindowType.DOUBLE_CASEMENT;
        } else {
            this.type = WindowType.ARCH_FRAME;
        }

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
     * Смена формы окна (по клику мыши или по вызову).
     */
    public void cycleType() {
        if (type == WindowType.CONCEPT_TRIPLE) {
            type = WindowType.DOUBLE_CASEMENT;
        } else if (type == WindowType.DOUBLE_CASEMENT) {
            type = WindowType.ARCH_FRAME;
        } else {
            type = WindowType.CONCEPT_TRIPLE;
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

        // Чёрная ниша окна
        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, width, height);

        // Стекло окна
        double pad = 2.0;
        double gw = width - pad * 2;
        double gh = height - pad * 2;

        gc.setFill(glassColor);
        gc.fillRect(x + pad, y + pad, gw, gh);

        // Белые переплёты рамы
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.0);
        gc.strokeRect(x + pad + 0.5, y + pad + 0.5, gw - 1.0, gh - 1.0);

        if (type == WindowType.CONCEPT_TRIPLE) {
            // Оригинальная трёхстворчатая форма с эскиза
            double col1 = x + pad + gw * 0.35;
            double col2 = x + pad + gw * 0.65;
            gc.strokeLine(col1, y + pad, col1, y + pad + gh);
            gc.strokeLine(col2, y + pad, col2, y + pad + gh);
            double row1 = y + pad + gh * 0.32;
            gc.strokeLine(col1, row1, col2, row1);

        } else if (type == WindowType.DOUBLE_CASEMENT) {
            // Двустворчатая рама
            double mid = x + pad + gw * 0.45;
            gc.strokeLine(mid, y + pad, mid, y + pad + gh);
            double fortY = y + pad + gh * 0.38;
            gc.strokeLine(mid, fortY, x + pad + gw, fortY);

        } else {
            // Арочная форма (примитив ARC)
            gc.setLineWidth(1.8);
            gc.strokeArc(x + pad + 1, y + pad + 1, gw - 2, gh * 0.8, 0, 180, ArcType.OPEN);
            double mid = x + pad + gw * 0.5;
            gc.strokeLine(mid, y + pad + gh * 0.35, mid, y + pad + gh);
        }
    }
}
