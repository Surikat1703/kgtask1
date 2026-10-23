package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Проезжая часть улицы и тротуар:
 * - Тротуар с плиткой/сеткой швов (как на эскизе с косыми и вертикальными линиями)
 * - Бордюрный камень (поребрик)
 * - Асфальтовая дорога с разметкой
 * - Проезды во дворы между домами
 */
public class RoadAndSidewalk implements Drawable {
    private final double width;
    private final double height;
    private final double sidewalkTopY;
    private final double roadTopY;

    public RoadAndSidewalk(double width, double height, double sidewalkTopY, double roadTopY) {
        this.width = width;
        this.height = height;
        this.sidewalkTopY = sidewalkTopY;
        this.roadTopY = roadTopY;
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        // 1. Асфальт между домами и проезды во дворы
        gc.setFill(Color.web("#35373d"));
        // Проезд слева
        double[] leftDriveX = {130, 200, 230, 110};
        double[] leftDriveY = {sidewalkTopY - 70, sidewalkTopY - 70, sidewalkTopY + 30, sidewalkTopY + 30};
        gc.fillPolygon(leftDriveX, leftDriveY, 4);

        // Проезд справа
        double[] rightDriveX = {width - 230, width - 160, width - 110, width - 200};
        double[] rightDriveY = {sidewalkTopY - 70, sidewalkTopY - 70, sidewalkTopY + 30, sidewalkTopY + 30};
        gc.fillPolygon(rightDriveX, rightDriveY, 4);

        // 2. Тротуарная зона
        gc.setFill(Color.web("#8f969e"));
        gc.fillRect(0, sidewalkTopY, width, roadTopY - sidewalkTopY);

        // Сетка тротуарной плитки (перспективные и горизонтальные линии как на эскизе)
        gc.setStroke(Color.web("#5e636b"));
        gc.setLineWidth(1.0);

        // Горизонтальные линии швов
        for (double y = sidewalkTopY + 8; y < roadTopY; y += 9) {
            gc.strokeLine(0, y, width, y);
        }

        // Вертикальные/диагональные насечки плитки
        for (double x = 0; x < width; x += 18) {
            gc.strokeLine(x, sidewalkTopY, x - 12, roadTopY);
        }

        // 3. Бордюр (поребрик)
        gc.setFill(Color.web("#505459"));
        gc.fillRect(0, roadTopY - 3, width, 5);
        gc.setStroke(Color.web("#222326"));
        gc.setLineWidth(1.2);
        gc.strokeLine(0, roadTopY - 3, width, roadTopY - 3);
        gc.strokeLine(0, roadTopY + 2, width, roadTopY + 2);

        // Вертикальные сколы/швы бордюра
        for (double bx = 0; bx < width; bx += 30) {
            gc.strokeLine(bx, roadTopY - 3, bx, roadTopY + 2);
        }

        // 4. Проезжая часть (тёмный асфальт)
        gc.setFill(Color.web("#25272b"));
        gc.fillRect(0, roadTopY + 2, width, height - roadTopY);

        // Тонкая трещина на асфальте с помощью кривой (примитив кривая линия)
        gc.setStroke(Color.web("#141517"));
        gc.setLineWidth(1.2);
        gc.beginPath();
        gc.moveTo(150, roadTopY + 20);
        gc.lineTo(190, roadTopY + 26);
        gc.lineTo(230, roadTopY + 22);
        gc.lineTo(280, roadTopY + 32);
        gc.stroke();

        // 5. Прерывистая линия дорожной разметки (штрихи)
        gc.setStroke(Color.web("#eceef2"));
        gc.setLineWidth(3.0);
        double dashY = roadTopY + (height - roadTopY) * 0.45;
        double dashLen = 40.0;
        double spaceLen = 30.0;
        for (double sx = 20; sx < width; sx += dashLen + spaceLen) {
            gc.strokeLine(sx, dashY, sx + dashLen, dashY);
        }

        // Нижняя сплошная краевая полоса дороги
        gc.setStroke(Color.web("#9ea3ad"));
        gc.setLineWidth(2.0);
        gc.strokeLine(0, height - 12, width, height - 12);
    }
}
