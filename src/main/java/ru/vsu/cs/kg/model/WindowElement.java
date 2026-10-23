package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.Random;

/**
 * Окно хрущёвки точь-в-точь по концепту:
 * - Чёрный толстый контур вокруг окна
 * - Белая внутренняя рама (контур и переплёты)
 * - 3 случайные формы (одно из требований):
 *     1. Трёхстворчатое окно с двумя вертикальными белыми полосами и верхней фрамугой (как на концепте!)
 *     2. Двустворчатое окно с вертикальной стойкой и форточкой
 *     3. Окно с арочной фрамугой сверху (использование примитива ARC!)
 * - Случайный цвет внутри (одно из требований):
 *     - Тёмно-синий: #0a0b2a
 *     - Фиолетовый / маджента: #b545be
 *     - Жёлтый: #fde802
 */
public class WindowElement {
    public enum WindowType {
        CONCEPT_TRIPLE,   // 3 створки (оригинал эскиза)
        DOUBLE_CASEMENT,  // 2 створки + форточка
        ARCH_FRAME        // Арочный свод (arc)
    }

    private final double relX;
    private final double relY;
    private final double width;
    private final double height;
    private final WindowType type;
    private final Color glassColor;

    public WindowElement(double relX, double relY, double width, double height, Random rng) {
        this.relX = relX;
        this.relY = relY;
        this.width = width;
        this.height = height;

        // Выбор случайной формы из 3-х
        int t = rng.nextInt(3);
        if (t == 0) {
            this.type = WindowType.CONCEPT_TRIPLE;
        } else if (t == 1) {
            this.type = WindowType.DOUBLE_CASEMENT;
        } else {
            this.type = WindowType.ARCH_FRAME;
        }

        // Выбор случайного цвета из 3-х (тёмно-синий, фиолетовый, жёлтый)
        int c = rng.nextInt(3);
        if (c == 0) {
            this.glassColor = Color.web("#0e1236"); // тёмно-синий
        } else if (c == 1) {
            this.glassColor = Color.web("#be3ec9"); // фиолетовый
        } else {
            this.glassColor = Color.web("#ffe600"); // ярко-жёлтый
        }
    }

    public void draw(GraphicsContext gc, double baseX, double baseY) {
        double x = baseX + relX;
        double y = baseY + relY;

        // 1. Чёрная внешняя обводка / ниша (как на эскизе)
        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, width, height);

        // 2. Внутреннее светящееся стекло
        double pad = 1.6;
        double gw = width - pad * 2;
        double gh = height - pad * 2;

        gc.setFill(glassColor);
        gc.fillRect(x + pad, y + pad, gw, gh);

        // 3. Белая оконная рама и переплёты (толстая белая линия)
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.8);

        // Внутренняя белая рамка
        gc.strokeRect(x + pad + 0.5, y + pad + 0.5, gw - 1.0, gh - 1.0);

        if (type == WindowType.CONCEPT_TRIPLE) {
            // Оригинальная форма с эскиза: две вертикальные белые стойки
            double col1 = x + pad + gw * 0.33;
            double col2 = x + pad + gw * 0.67;
            gc.strokeLine(col1, y + pad, col1, y + pad + gh);
            gc.strokeLine(col2, y + pad, col2, y + pad + gh);
            // Горизонтальная линия форточки сверху
            double row1 = y + pad + gh * 0.35;
            gc.strokeLine(col1, row1, col2, row1);

        } else if (type == WindowType.DOUBLE_CASEMENT) {
            // Вторая форма: двустворчатое окно с форточкой в правой створке
            double mid = x + pad + gw * 0.45;
            gc.strokeLine(mid, y + pad, mid, y + pad + gh);
            double fortY = y + pad + gh * 0.40;
            gc.strokeLine(mid, fortY, x + pad + gw, fortY);

        } else {
            // Третья форма: арочный верх с использованием примитива ARC
            gc.setLineWidth(1.5);
            gc.strokeArc(x + pad + 1, y + pad + 1, gw - 2, gh * 0.75, 0, 180, ArcType.OPEN);
            double mid = x + pad + gw * 0.5;
            gc.strokeLine(mid, y + pad + gh * 0.35, mid, y + pad + gh);
        }
    }
}
