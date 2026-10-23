package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Полноценная дальняя хрущевка во дворе:
 * - Не срезанная узкая полоска, а полноценный многоэтажный панельный дом вдалеке,
 *   видимый в перспективе проёма двора между домами переднего плана
 * - Имеет полноценные этажи, окна, межпанельные швы и антенны
 */
public class BackgroundCourtyardBuilding implements Drawable {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final int rows = 5;
    private final int cols;
    private final List<WindowElement> windows = new ArrayList<>();

    public BackgroundCourtyardBuilding(double x, double y, double width, double height, int cols, Random rng) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.cols = cols;

        double cellW = width / cols;
        double cellH = height / rows;
        double winW = cellW * 0.60;
        double winH = cellH * 0.62;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double wx = c * cellW + (cellW - winW) / 2.0;
                double wy = r * cellH + (cellH - winH) / 2.0;
                windows.add(new WindowElement(wx, wy, winW, winH, rng));
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        // Задний план чуть темнее из-за ночной воздушной перспективы
        gc.setFill(Color.web("#757980"));
        gc.fillRect(x, y, width, height);

        // Контур
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeRect(x, y, width, height);

        // Парапет крыши
        gc.setFill(Color.web("#5e6269"));
        gc.fillRect(x - 2, y - 5, width + 4, 6);
        gc.strokeRect(x - 2, y - 5, width + 4, 6);

        // Антенна на крыше
        gc.setLineWidth(1.8);
        double ax = x + width * 0.5;
        gc.strokeLine(ax, y, ax, y - 18);
        gc.strokeLine(ax - 7, y - 14, ax + 7, y - 14);
        gc.strokeLine(ax - 10, y - 9, ax + 10, y - 9);

        // Межпанельные швы
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.2);
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

        // Окна
        for (WindowElement win : windows) {
            win.draw(gc, x, y);
        }
    }
}
