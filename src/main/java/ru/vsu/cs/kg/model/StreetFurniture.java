package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Элементы благоустройства двора и улицы:
 * - Скамейка (лавочка) из деревянных брусков и чугунных ножек
 * - Урны для мусора с надписью «МУСОР» (примитив ТЕКСТ)
 * - Газоны / земляные холмики с криволинейными контурами (Bezier curve / quadratic curve)
 */
public class StreetFurniture implements Drawable {
    private final double benchX;
    private final double benchY;
    private final double[] trashCanX;
    private final double trashY;

    public StreetFurniture(double benchX, double benchY, double[] trashCanX, double trashY) {
        this.benchX = benchX;
        this.benchY = benchY;
        this.trashCanX = trashCanX;
        this.trashY = trashY;
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        // 1. Отрисовка земляных насыпей / островков газона (как коричневые островки на эскизе)
        drawMounds(gc);

        // 2. Скамейка
        drawBench(gc, benchX, benchY);

        // 3. Урны с надписями
        for (double tx : trashCanX) {
            drawTrashCan(gc, tx, trashY);
        }
    }

    private void drawMounds(GraphicsContext gc) {
        // Островки земли/газона на тротуаре, как на эскизе пользователя (коричнево-охристые)
        gc.setFill(Color.web("#b88265"));
        gc.setStroke(Color.web("#382319"));
        gc.setLineWidth(1.6);

        // Левый холмик
        drawSmoothMound(gc, 260, benchY + 12, 55, 12);
        // Центральный холмик под лавочкой
        drawSmoothMound(gc, benchX - 35, benchY + 14, 110, 14);
        // Правый холмик
        drawSmoothMound(gc, 840, benchY + 12, 60, 12);
    }

    private void drawSmoothMound(GraphicsContext gc, double x, double y, double w, double h) {
        // Криволинейный холм с помощью кривой Безье
        gc.beginPath();
        gc.moveTo(x, y);
        gc.quadraticCurveTo(x + w * 0.25, y - h, x + w * 0.5, y - h * 0.9);
        gc.quadraticCurveTo(x + w * 0.75, y - h * 1.1, x + w, y);
        gc.closePath();
        gc.fill();
        gc.stroke();
    }

    private void drawBench(GraphicsContext gc, double x, double y) {
        double w = 60;
        double h = 22;

        // Чугунные ножки скамейки
        gc.setStroke(Color.web("#1c1c1f"));
        gc.setLineWidth(2.2);
        // Левая ножка (арка / изгиб)
        gc.strokeLine(x + 5, y + h, x + 8, y + 8);
        gc.strokeLine(x + 8, y + 8, x + 4, y);
        // Правая ножка
        gc.strokeLine(x + w - 5, y + h, x + w - 8, y + 8);
        gc.strokeLine(x + w - 8, y + 8, x + w - 4, y);
        // Перекладина ножек внизу
        gc.strokeLine(x + 6, y + h - 2, x + w - 6, y + h - 2);

        // Деревянные рейки спинки (зелёно-бирюзовые или деревянные, на эскизе зелёные)
        gc.setFill(Color.web("#4b8a47"));
        gc.setStroke(Color.web("#224420"));
        gc.setLineWidth(1.0);

        // Две рейки спинки
        gc.fillRect(x + 2, y, w - 4, 3.5);
        gc.strokeRect(x + 2, y, w - 4, 3.5);
        gc.fillRect(x + 2, y + 5, w - 4, 3.5);
        gc.strokeRect(x + 2, y + 5, w - 4, 3.5);

        // Сиденье скамейки
        gc.fillRect(x, y + 10, w, 4.0);
        gc.strokeRect(x, y + 10, w, 4.0);
    }

    private void drawTrashCan(GraphicsContext gc, double x, double y) {
        double w = 15;
        double h = 22;

        // Ножка урны
        gc.setStroke(Color.web("#1e2024"));
        gc.setLineWidth(2.5);
        gc.strokeLine(x + w / 2.0, y + h, x + w / 2.0, y + h + 6);

        // Бак урны (серый металлический прямоугольник со скруглёнными углами / фаской)
        gc.setFill(Color.web("#43474d"));
        gc.fillRect(x, y, w, h);
        gc.setStroke(Color.web("#1e2024"));
        gc.setLineWidth(1.2);
        gc.strokeRect(x, y, w, h);

        // Полосы жести
        gc.setLineWidth(0.8);
        gc.strokeLine(x + 2, y + 5, x + w - 2, y + 5);
        gc.strokeLine(x + 2, y + h - 5, x + w - 2, y + h - 5);

        // Текстовая надпись на урне «МУСОР» вертикально или мелко (примитив ТЕКСТ)
        gc.setFill(Color.web("#e0e0e0"));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 5.0));
        gc.fillText("УРНА", x + 1.2, y + 12);
    }
}
