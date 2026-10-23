package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Окружение переднего плана:
 * - Заезды во дворы перерисованы строго по оригинальному эскизу:
 *   тёмно-серый асфальт (#4a4d52), уходящий под углом вниз прямо к дороге,
 *   с чёрным контуром и бордюрной фаской (как на оригинальном рисунке!).
 * - Сетка плитки тротуара доходит ровно до границы дворов.
 * - Урны, лавочка, земляные холмики и дорога с разметкой.
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
        // 1. Тротуарная зона
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
            gc.strokeLine(x, sidewalkTopY, x - 18, roadTopY);
        }

        // 2. Заезды во дворы строго как на концепте художника (нарисованы поверх плитки тротуара)
        drawCourtyardDriveways(gc);

        // 3. Бордюрный камень вдоль всей дороги
        gc.setFill(Color.web("#505359"));
        gc.fillRect(0, roadTopY, width, 5);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.6);
        gc.strokeLine(0, roadTopY, width, roadTopY);
        gc.strokeLine(0, roadTopY + 5, width, roadTopY + 5);

        // 4. Проезжая часть дороги
        gc.setFill(Color.web("#303236"));
        gc.fillRect(0, roadTopY + 5, width, height - (roadTopY + 5));

        // Белая разметка по центру дороги
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

        // 7. Урны для мусора
        double[] urnXs = {88, 385, 606, 698};
        for (double ux : urnXs) {
            drawUrn(gc, ux, sidewalkTopY + 10);
        }
    }

    private void drawCourtyardDriveways(GraphicsContext gc) {
        // Тёмно-серый асфальт заезда во двор (#4d5057) с жирной чёрной окантовкой
        gc.setFill(Color.web("#4d5057"));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.2);

        // Левый заезд во двор (в точности повторяет контур концепта: полигон от домов вниз через весь тротуар до дороги)
        double[] leftX = {149, 184, 172, 115};
        double[] leftY = {sidewalkTopY - 75, sidewalkTopY - 75, roadTopY, roadTopY};
        gc.fillPolygon(leftX, leftY, 4);
        gc.strokePolygon(leftX, leftY, 4);

        // Светлая боковая грань тротуара у левого заезда (фаска бордюра)
        gc.setFill(Color.web("#b0b5be"));
        double[] leftEdgeX = {149, 153, 119, 115};
        double[] leftEdgeY = {sidewalkTopY - 75, sidewalkTopY - 75, roadTopY, roadTopY};
        gc.fillPolygon(leftEdgeX, leftEdgeY, 4);
        gc.strokePolygon(leftEdgeX, leftEdgeY, 4);

        // Правый заезд во двор (в точности по концепту: уходит вправо-вниз)
        double[] rightX = {856, 891, 928, 865};
        double[] rightY = {sidewalkTopY - 75, sidewalkTopY - 75, roadTopY, roadTopY};
        gc.setFill(Color.web("#4d5057"));
        gc.fillPolygon(rightX, rightY, 4);
        gc.strokePolygon(rightX, rightY, 4);

        // Светлая боковая грань тротуара у правого заезда
        gc.setFill(Color.web("#b0b5be"));
        double[] rightEdgeX = {856, 860, 869, 865};
        double[] rightEdgeY = {sidewalkTopY - 75, sidewalkTopY - 75, roadTopY, roadTopY};
        gc.fillPolygon(rightEdgeX, rightEdgeY, 4);
        gc.strokePolygon(rightEdgeX, rightEdgeY, 4);
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
