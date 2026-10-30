package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Окружение переднего плана:
 * - Тротуарная плитка
 * - Бордюр и проезжая часть с разметкой
 * - Проезды во дворы: простые аккуратные асфальтовые полосы между домами
 *   (без наслоений и двойной отрисовки)
 * - Скамейка и урны
 */
public class ForegroundEnvironment implements Drawable {
    private final double width;
    private final double height;
    private final double sidewalkTopY;
    private final double roadTopY;

    // Координаты щелей между домами (проезды во дворы)
    // Левый проезд: дом1 заканчивается x=149, дом2 начинается x=184
    private static final double LEFT_GAP_X1 = 149.0;
    private static final double LEFT_GAP_X2 = 184.0;
    // Правый проезд: дом4 заканчивается x=855, дом5 начинается x=891
    private static final double RIGHT_GAP_X1 = 855.0;
    private static final double RIGHT_GAP_X2 = 891.0;

    public ForegroundEnvironment(double width, double height, double sidewalkTopY, double roadTopY) {
        this.width = width;
        this.height = height;
        this.sidewalkTopY = sidewalkTopY;
        this.roadTopY = roadTopY;
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        // 1. Тротуарная зона
        gc.setFill(Color.web("#8e9196"));
        gc.fillRect(0, sidewalkTopY, width, roadTopY - sidewalkTopY);

        // Верхняя граница тротуара
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeLine(0, sidewalkTopY, width, sidewalkTopY);

        // Сетка плитки тротуара (пропускаем зоны проездов, чтобы не наслаивать)
        gc.setStroke(Color.web("#606368"));
        gc.setLineWidth(1.0);

        // Горизонтальные линии
        for (double y = sidewalkTopY + 7; y < roadTopY; y += 7.5) {
            // Левая часть (до левого проезда)
            gc.strokeLine(0, y, LEFT_GAP_X1, y);
            // Между проездами
            gc.strokeLine(LEFT_GAP_X2, y, RIGHT_GAP_X1, y);
            // Правая часть (после правого проезда)
            gc.strokeLine(RIGHT_GAP_X2, y, width, y);
        }

        // Диагональные швы плитки (пропускаем зоны проездов)
        for (double x = -10; x < width + 40; x += 15) {
            double bottomX = x - 18;
            // Пропускаем если попадает в зону проезда
            boolean inLeftGap = (x >= LEFT_GAP_X1 - 5 && x <= LEFT_GAP_X2 + 5);
            boolean inRightGap = (x >= RIGHT_GAP_X1 - 5 && x <= RIGHT_GAP_X2 + 5);
            if (inLeftGap || inRightGap) continue;
            gc.strokeLine(x, sidewalkTopY, bottomX, roadTopY);
        }

        // 2. Проезды во дворы — простой тёмный асфальт в щели между домами
        drawDriveway(gc, LEFT_GAP_X1, LEFT_GAP_X2);
        drawDriveway(gc, RIGHT_GAP_X1, RIGHT_GAP_X2);

        // 3. Бордюрный камень вдоль проезжей части
        gc.setFill(Color.web("#505359"));
        gc.fillRect(0, roadTopY, width, 5);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.6);
        gc.strokeLine(0, roadTopY, width, roadTopY);
        gc.strokeLine(0, roadTopY + 5, width, roadTopY + 5);

        // 4. Проезжая часть
        gc.setFill(Color.web("#303236"));
        gc.fillRect(0, roadTopY + 5, width, height - (roadTopY + 5));

        // Белая прерывистая разметка по центру дороги (примитив прямая линия + штрихи)
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4.0);
        double markY = roadTopY + 30;
        for (double dx = 10; dx < width; dx += 95) {
            gc.strokeLine(dx, markY, dx + 65, markY);
        }

        // Нижний край дороги
        gc.setStroke(Color.web("#141414"));
        gc.setLineWidth(2.5);
        gc.strokeLine(0, height - 8, width, height - 8);

        // 5. Декоративные кусты/холмики (кривые Безье — обязательный примитив)
        drawBush(gc, 120, sidewalkTopY + 14);
        drawBush(gc, 402, sidewalkTopY + 15);
        drawBush(gc, 620, sidewalkTopY + 14);
        drawBush(gc, 780, sidewalkTopY + 15);

        // 6. Скамейка
        drawBench(gc, 505, sidewalkTopY + 12);

        // 7. Урны для мусора
        double[] urnXs = {88, 385, 606, 698};
        for (double ux : urnXs) {
            drawUrn(gc, ux, sidewalkTopY + 10);
        }
    }

    /**
     * Рисует аккуратный проезд во двор — просто тёмная полоса асфальта
     * от верхней границы тротуара до проезжей части, без наслоений.
     */
    private void drawDriveway(GraphicsContext gc, double x1, double x2) {
        double driveW = x2 - x1;

        // Тёмный асфальт проезда (от тротуара до дороги)
        gc.setFill(Color.web("#42454a"));
        gc.fillRect(x1, sidewalkTopY, driveW, roadTopY - sidewalkTopY);

        // Чёрные боковые границы
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.8);
        gc.strokeLine(x1, sidewalkTopY, x1, roadTopY);
        gc.strokeLine(x2, sidewalkTopY, x2, roadTopY);

        // Светлые бордюрные стенки по бокам (как срезы поребрика вглубь)
        gc.setFill(Color.web("#70747a"));
        gc.fillRect(x1, sidewalkTopY, 3, roadTopY - sidewalkTopY);
        gc.fillRect(x2 - 3, sidewalkTopY, 3, roadTopY - sidewalkTopY);
    }

    /**
     * Рисует декоративный куст/холмик на тротуаре (примитив — кривые Безье).
     * Как на концепте — маленькие коричневатые кустики/насыпи.
     */
    private void drawBush(GraphicsContext gc, double cx, double cy) {
        gc.setFill(Color.web("#6b4226"));
        gc.beginPath();
        gc.moveTo(cx - 12, cy + 5);
        gc.bezierCurveTo(cx - 10, cy - 6, cx - 2, cy - 8, cx, cy - 3);
        gc.bezierCurveTo(cx + 2, cy - 8, cx + 10, cy - 6, cx + 12, cy + 5);
        gc.closePath();
        gc.fill();

        gc.setStroke(Color.web("#4a2e18"));
        gc.setLineWidth(1.2);
        gc.beginPath();
        gc.moveTo(cx - 12, cy + 5);
        gc.bezierCurveTo(cx - 10, cy - 6, cx - 2, cy - 8, cx, cy - 3);
        gc.bezierCurveTo(cx + 2, cy - 8, cx + 10, cy - 6, cx + 12, cy + 5);
        gc.stroke();
    }

    private void drawBench(GraphicsContext gc, double x, double y) {
        double w = 38;
        double h = 15;

        // Чёрные ножки
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeLine(x + 3, y + h, x + 5, y + 2);
        gc.strokeLine(x + w - 3, y + h, x + w - 5, y + 2);
        gc.strokeLine(x + 4, y + h - 2, x + w - 4, y + h - 2);

        // Зелёные доски спинки и сиденья
        gc.setFill(Color.web("#3f9935"));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.2);

        gc.fillRect(x + 2, y, w - 4, 3.5);
        gc.strokeRect(x + 2, y, w - 4, 3.5);

        gc.fillRect(x, y + 6, w, 3.5);
        gc.strokeRect(x, y + 6, w, 3.5);
    }

    private void drawUrn(GraphicsContext gc, double x, double y) {
        double w = 16;
        double h = 20;

        // Ножка урны
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.2);
        gc.strokeLine(x + w / 2.0, y + h, x + w / 2.0, y + h + 6);

        // Корпус урны
        gc.setFill(Color.web("#5c6169"));
        gc.fillRect(x, y, w, h);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.4);
        gc.strokeRect(x, y, w, h);

        // Полосы жёсткости
        gc.setLineWidth(0.8);
        gc.strokeLine(x + 2, y + 4, x + w - 2, y + 4);
        gc.strokeLine(x + 2, y + h - 4, x + w - 2, y + h - 4);

        // Надпись «УРНА» (текстовая надпись — обязательный примитив)
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 4.5));
        gc.fillText("УРНА", x + 1.8, y + 12);
    }
}
