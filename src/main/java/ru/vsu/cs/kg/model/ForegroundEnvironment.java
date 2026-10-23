package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Окружение переднего плана:
 * - Заезды во дворы
 * - Тротуарная плитка, бордюр и проезжая часть
 * - Никаких пятен под фонарями!
 * - Скамейка и урны
 */
public class ForegroundEnvironment implements Drawable {
    private final double width;
    private final double height;
    private final double sidewalkTopY;
    private final double roadTopY;

    public ForegroundEnvironment(double width, double height, double sidewalkTopY, double roadTopY) {
        this.width = width;
        this.height = height;
        this.sidewalkTopY = sidewalkTopY;
        this.roadTopY = roadTopY;
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        // 1. Асфальтовые проезды во дворы между домами
        drawCourtyardDriveways(gc);

        // 2. Тротуарная зона
        gc.setFill(Color.web("#8e9196"));
        gc.fillRect(0, sidewalkTopY, width, roadTopY - sidewalkTopY);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeLine(0, sidewalkTopY, width, sidewalkTopY);

        // Сетка плитки тротуара
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.2);
        for (double y = sidewalkTopY + 7; y < roadTopY; y += 7.5) {
            gc.strokeLine(0, y, width, y);
        }
        for (double x = -10; x < width + 40; x += 15) {
            if ((x >= 120 && x <= 185) || (x >= 855 && x <= 925)) {
                continue;
            }
            gc.strokeLine(x, sidewalkTopY, x - 18, roadTopY);
        }

        // Перерисовываем проезды во дворы поверх плитки для ровного стыка
        drawCourtyardDriveways(gc);

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

        // Белая разметка по центру
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4.0);
        double markY = roadTopY + 30;
        double[] dashStarts = {10, 135, 330, 520, 715, 910};
        for (double dx : dashStarts) {
            gc.strokeLine(dx, markY, dx + 65, markY);
        }

        // Нижний край дороги
        gc.setStroke(Color.web("#141414"));
        gc.setLineWidth(2.5);
        gc.strokeLine(0, height - 8, width, height - 8);

        // 5. Скамейка
        drawBench(gc, 505, sidewalkTopY + 12);

        // 6. Урны для мусора
        double[] urnXs = {88, 385, 606, 698};
        for (double ux : urnXs) {
            drawUrn(gc, ux, sidewalkTopY + 10);
        }
    }

    private void drawCourtyardDriveways(GraphicsContext gc) {
        gc.setFill(Color.web("#42454a"));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.2);

        // Левый заезд во двор
        double[] leftX = {149, 184, 168, 126};
        double[] leftY = {sidewalkTopY - 70, sidewalkTopY - 70, roadTopY, roadTopY};
        gc.fillPolygon(leftX, leftY, 4);
        gc.strokePolygon(leftX, leftY, 4);

        // Правый заезд во двор
        double[] rightX = {856, 891, 915, 873};
        double[] rightY = {sidewalkTopY - 70, sidewalkTopY - 70, roadTopY, roadTopY};
        gc.fillPolygon(rightX, rightY, 4);
        gc.strokePolygon(rightX, rightY, 4);

        // Бордюрные скосы по бокам заездов
        gc.setFill(Color.web("#6c7178"));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.4);

        double[] leftBevelX = {149, 153, 130, 126};
        double[] leftBevelY = {sidewalkTopY - 70, sidewalkTopY - 70, roadTopY, roadTopY};
        gc.fillPolygon(leftBevelX, leftBevelY, 4);
        gc.strokePolygon(leftBevelX, leftBevelY, 4);

        double[] rightBevelX = {887, 891, 915, 911};
        double[] rightBevelY = {sidewalkTopY - 70, sidewalkTopY - 70, roadTopY, roadTopY};
        gc.fillPolygon(rightBevelX, rightBevelY, 4);
        gc.strokePolygon(rightBevelX, rightBevelY, 4);
    }

    private void drawBench(GraphicsContext gc, double x, double y) {
        double w = 38;
        double h = 15;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeLine(x + 3, y + h, x + 5, y + 2);
        gc.strokeLine(x + w - 3, y + h, x + w - 5, y + 2);
        gc.strokeLine(x + 4, y + h - 2, x + w - 4, y + h - 2);

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

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.2);
        gc.strokeLine(x + w / 2.0, y + h, x + w / 2.0, y + h + 6);

        gc.setFill(Color.web("#5c6169"));
        gc.fillRect(x, y, w, h);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.4);
        gc.strokeRect(x, y, w, h);

        gc.setLineWidth(0.8);
        gc.strokeLine(x + 2, y + 4, x + w - 2, y + 4);
        gc.strokeLine(x + 2, y + h - 4, x + w - 2, y + h - 4);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 4.5));
        gc.fillText("УРНА", x + 1.8, y + 12);
    }
}
