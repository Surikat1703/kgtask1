package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Хрущёвка 1-в-1 по концепту:
 * - Ровно 6 этажей, 16 окон в ряд на фасаде
 * - Серая текстура бетона (#999999 / #a3a3a3)
 * - Чёрные четкие швы между панелями
 * - Крыша: парапет со скошенными углами и штрихами
 * - Чёрные телеантенны характерной формы ("ёлочки" / Т-образные мачты)
 */
public class Khrushchevka implements Drawable {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final int rows = 6;
    private final int cols;
    private final List<WindowElement> windows = new ArrayList<>();
    private final List<Double> antennaXs = new ArrayList<>();

    public Khrushchevka(double x, double y, double width, double height, int cols, Random rng) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.cols = cols;

        // Расчёт позиций окон в сетке панели
        double cellW = width / cols;
        double cellH = height / rows;
        double winW = cellW * 0.52;
        double winH = cellH * 0.58;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double wx = c * cellW + (cellW - winW) / 2.0;
                double wy = r * cellH + (cellH - winH) / 2.0;
                windows.add(new WindowElement(wx, wy, winW, winH, rng));
            }
        }

        // Позиции антенн на крыше
        if (cols >= 10) {
            // Большое центральное здание (3 антенны по эскизу)
            antennaXs.add(width * 0.18);
            antennaXs.add(width * 0.52);
            antennaXs.add(width * 0.82);
        } else if (cols >= 4) {
            // Боковые здания (по 1-2 антенны)
            antennaXs.add(width * 0.35);
            antennaXs.add(width * 0.75);
        } else {
            // Узкие пролёты
            antennaXs.add(width * 0.5);
        }
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        // 1. Антенны на крыше (как нарисовано на эскизе от руки)
        drawAntennas(gc);

        // 2. Фасад здания
        gc.setFill(Color.web("#999a9e"));
        gc.fillRect(x, y, width, height);

        // Внешний контур здания
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.2);
        gc.strokeRect(x, y, width, height);

        // 3. Парапет крыши (выступающая плита сверху)
        gc.setFill(Color.web("#787b80"));
        gc.fillRect(x - 2, y - 6, width + 4, 7);
        gc.strokeRect(x - 2, y - 6, width + 4, 7);

        // Скосы и штрихи на крыше как на эскизе
        gc.setLineWidth(1.2);
        for (double k = x + 15; k < x + width; k += 45) {
            gc.strokeLine(k, y - 6, k + 8, y);
        }

        // 4. Сетка межпанельных швов (чёрные линии)
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.6);

        double cellW = width / cols;
        double cellH = height / rows;

        // Горизонтальные швы между этажами
        for (int r = 1; r < rows; r++) {
            double sy = y + r * cellH;
            gc.strokeLine(x, sy, x + width, sy);
        }

        // Вертикальные швы между панелями
        for (int c = 1; c < cols; c++) {
            double sx = x + c * cellW;
            gc.strokeLine(sx, y, sx, y + height);
        }

        // 5. Отрисовка всех окон
        for (WindowElement win : windows) {
            win.draw(gc, x, y);
        }
    }

    private void drawAntennas(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);

        for (double relX : antennaXs) {
            double ax = x + relX;
            double topY = y - 24;

            // Вертикальная опора
            gc.strokeLine(ax, y, ax, topY);

            // Поперечные "усы" антенны с разветвлениями
            gc.strokeLine(ax - 10, topY + 4, ax + 10, topY + 4);
            gc.strokeLine(ax - 14, topY + 10, ax + 14, topY + 10);
            gc.strokeLine(ax - 8, topY + 16, ax + 8, topY + 16);

            // Наклонные "рога" антенны
            gc.setLineWidth(1.4);
            gc.strokeLine(ax - 10, topY + 4, ax - 13, topY - 2);
            gc.strokeLine(ax + 10, topY + 4, ax + 13, topY - 2);
            gc.strokeLine(ax - 14, topY + 10, ax - 17, topY + 5);
            gc.strokeLine(ax + 14, topY + 10, ax + 17, topY + 5);
            gc.setLineWidth(2.0);
        }
    }
}
