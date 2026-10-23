package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Окружение переднего плана:
 * - Исправлен въезд во двор (аккуратный асфальтовый проезд с естественной перспективой и бордюрами)
 * - Увеличена урна и скорректирован шрифт/отступы надписи, чтобы текст гарантированно не вылезал
 * - Дорога, разметка, тротуарная плитка и холмики газона
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
        // 1. Аккуратные проезды во дворы между домами
        gc.setFill(Color.web("#505359"));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);

        // Левый проезд во двор (между крайним левым и вторым домом)
        double[] leftDriveX = {152, 182, 172, 122};
        double[] leftDriveY = {sidewalkTopY - 60, sidewalkTopY - 60, sidewalkTopY, sidewalkTopY};
        gc.fillPolygon(leftDriveX, leftDriveY, 4);
        gc.strokePolygon(leftDriveX, leftDriveY, 4);

        // Правый проезд во двор
        double[] rightDriveX = {858, 888, 918, 868};
        double[] rightDriveY = {sidewalkTopY - 60, sidewalkTopY - 60, sidewalkTopY, sidewalkTopY};
        gc.fillPolygon(rightDriveX, rightDriveY, 4);
        gc.strokePolygon(rightDriveX, rightDriveY, 4);

        // 2. Тротуар
        gc.setFill(Color.web("#8e9196"));
        gc.fillRect(0, sidewalkTopY, width, roadTopY - sidewalkTopY);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeLine(0, sidewalkTopY, width, sidewalkTopY);

        // Сетка плитки
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.2);
        for (double y = sidewalkTopY + 7; y < roadTopY; y += 7.5) {
            gc.strokeLine(0, y, width, y);
        }
        for (double x = -10; x < width + 40; x += 15) {
            gc.strokeLine(x, sidewalkTopY, x - 18, roadTopY);
        }

        // 3. Бордюр
        gc.setFill(Color.web("#505359"));
        gc.fillRect(0, roadTopY, width, 5);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.6);
        gc.strokeLine(0, roadTopY, width, roadTopY);
        gc.strokeLine(0, roadTopY + 5, width, roadTopY + 5);

        // 4. Проезжая часть
        gc.setFill(Color.web("#303236"));
        gc.fillRect(0, roadTopY + 5, width, height - (roadTopY + 5));

        // Разметка дороги
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4.0);
        double markY = roadTopY + 30;
        double[] dashStarts = {10, 135, 330, 520, 715, 910};
        for (double dx : dashStarts) {
            gc.strokeLine(dx, markY, dx + 65, markY);
        }

        // Край дороги
        gc.setStroke(Color.web("#141414"));
        gc.setLineWidth(2.5);
        gc.strokeLine(0, height - 8, width, height - 8);

        // 5. Островки насыпей/газонов
        drawMounds(gc);

        // 6. Скамейка
        drawBench(gc, 505, sidewalkTopY + 12);

        // 7. Урны для мусора (исправленные, текст строго внутри)
        double[] urnXs = {88, 385, 606, 698};
        for (double ux : urnXs) {
            drawUrn(gc, ux, sidewalkTopY + 10);
        }
    }

    private void drawMounds(GraphicsContext gc) {
        gc.setFill(Color.web("#dd9571"));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);

        drawSingleMound(gc, 9, sidewalkTopY + 24, 32, 16);
        drawSingleMound(gc, 273, sidewalkTopY + 24, 52, 19);
        drawSingleMound(gc, 460, sidewalkTopY + 25, 45, 17);
        drawSingleMound(gc, 548, sidewalkTopY + 25, 44, 15);
        drawSingleMound(gc, 715, sidewalkTopY + 24, 50, 19);
    }

    private void drawSingleMound(GraphicsContext gc, double x, double y, double w, double h) {
        gc.beginPath();
        gc.moveTo(x, y);
        gc.quadraticCurveTo(x + w * 0.2, y - h * 1.05, x + w * 0.5, y - h);
        gc.quadraticCurveTo(x + w * 0.8, y - h * 0.95, x + w, y);
        gc.closePath();
        gc.fill();
        gc.stroke();
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
        // Урна увеличена до комфортных размеров: 16x20 px
        double w = 16;
        double h = 20;

        // Ножка
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.2);
        gc.strokeLine(x + w / 2.0, y + h, x + w / 2.0, y + h + 6);

        // Бак
        gc.setFill(Color.web("#5c6169"));
        gc.fillRect(x, y, w, h);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.4);
        gc.strokeRect(x, y, w, h);

        // Полосы жесткости бака
        gc.setLineWidth(0.8);
        gc.strokeLine(x + 2, y + 4, x + w - 2, y + 4);
        gc.strokeLine(x + 2, y + h - 4, x + w - 2, y + h - 4);

        // Надпись «УРНА» точно по центру бака, не вылезая за края
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 4.5));
        gc.fillText("УРНА", x + 1.8, y + 12);
    }
}
