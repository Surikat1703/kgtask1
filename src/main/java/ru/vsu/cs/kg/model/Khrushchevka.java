package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Хрущёвка:
 * - 5 этажей (классическая пятиэтажка, окна крупнее и пропорциональнее)
 * - Окна увеличены, сетка просторнее
 * - Поддерживает смену формы окна по клику
 */
public class Khrushchevka implements Drawable {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final int rows = 5; // 5 этажей как в реальной хрущевке
    private final int cols;
    private final List<WindowElement> windows = new ArrayList<>();
    private final List<Double> antennaXs = new ArrayList<>();

    public Khrushchevka(double x, double y, double width, double height, int cols, Random rng) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.cols = cols;

        // Расчёт пропорциональных крупных окон
        double cellW = width / cols;
        double cellH = height / rows;
        double winW = cellW * 0.65;
        double winH = cellH * 0.68;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double wx = c * cellW + (cellW - winW) / 2.0;
                double wy = r * cellH + (cellH - winH) / 2.0;
                windows.add(new WindowElement(wx, wy, winW, winH, rng));
            }
        }

        if (cols >= 8) {
            antennaXs.add(width * 0.22);
            antennaXs.add(width * 0.52);
            antennaXs.add(width * 0.80);
        } else if (cols >= 4) {
            antennaXs.add(width * 0.35);
            antennaXs.add(width * 0.72);
        } else {
            antennaXs.add(width * 0.5);
        }
    }

    public boolean handleClick(double px, double py) {
        for (WindowElement win : windows) {
            if (win.contains(x, y, px, py)) {
                win.cycleType();
                return true;
            }
        }
        return false;
    }

    /**
     * Поменять форму всех окон дома
     */
    public void cycleAllWindows() {
        for (WindowElement win : windows) {
            win.cycleType();
        }
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        drawAntennas(gc);

        // Фасад здания
        gc.setFill(Color.web("#999a9e"));
        gc.fillRect(x, y, width, height);

        // Внешний контур
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.2);
        gc.strokeRect(x, y, width, height);

        // Парапет крыши
        gc.setFill(Color.web("#787b80"));
        gc.fillRect(x - 2, y - 6, width + 4, 7);
        gc.strokeRect(x - 2, y - 6, width + 4, 7);

        // Штрихи парапета
        gc.setLineWidth(1.2);
        for (double k = x + 12; k < x + width; k += 35) {
            gc.strokeLine(k, y - 6, k + 8, y);
        }

        // Межпанельные швы
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.6);
        double cellW = width / cols;
        double cellH = height / rows;

        for (int r = 1; r < rows; r++) {
            double sy = y + r * cellH;
            gc.strokeLine(x, sy, x + width, sy);
        }

        for (int c = 1; c < cols; c++) {
            double sx = x + c * cellW;
            gc.strokeLine(sx, y, sx, y + height);
        }

        // Отрисовка окон
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

            gc.strokeLine(ax, y, ax, topY);

            gc.strokeLine(ax - 10, topY + 4, ax + 10, topY + 4);
            gc.strokeLine(ax - 14, topY + 10, ax + 14, topY + 10);
            gc.strokeLine(ax - 8, topY + 16, ax + 8, topY + 16);

            gc.setLineWidth(1.4);
            gc.strokeLine(ax - 10, topY + 4, ax - 13, topY - 2);
            gc.strokeLine(ax + 10, topY + 4, ax + 13, topY - 2);
            gc.strokeLine(ax - 14, topY + 10, ax - 17, topY + 5);
            gc.strokeLine(ax + 14, topY + 10, ax + 17, topY + 5);
            gc.setLineWidth(2.0);
        }
    }
}
