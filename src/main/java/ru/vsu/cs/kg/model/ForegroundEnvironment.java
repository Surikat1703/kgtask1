package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Тротуар, дорога, обочина, лавочка, урны и земляные холмики 1-в-1 по концепту:
 * - Серый тротуар с косой сеткой плитки (#8e9196)
 * - Перспективные заезды во дворы между хрущёвками (#595c61)
 * - Песчано-рыжие насыпи холмиков газона (#dd9571) с чёрным контуром
 * - Зелёная скамейка (#3f9935) с чёрным каркасом
 * - Серые цилиндрические урны (#5c6169) с текстом
 * - Тёмный асфальт дороги (#303236) с белой прерывистой разметкой
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
        // 1. Проезды во дворы между домами (в перспективе, как на эскизе)
        gc.setFill(Color.web("#54575c"));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);

        // Левый проезд
        double[] leftDriveX = {149, 185, 175, 115};
        double[] leftDriveY = {sidewalkTopY - 70, sidewalkTopY - 70, sidewalkTopY + 30, sidewalkTopY + 30};
        gc.fillPolygon(leftDriveX, leftDriveY, 4);
        gc.strokePolygon(leftDriveX, leftDriveY, 4);

        // Правый проезд
        double[] rightDriveX = {856, 892, 925, 860};
        double[] rightDriveY = {sidewalkTopY - 70, sidewalkTopY - 70, sidewalkTopY + 30, sidewalkTopY + 30};
        gc.fillPolygon(rightDriveX, rightDriveY, 4);
        gc.strokePolygon(rightDriveX, rightDriveY, 4);

        // 2. Тротуарная зона
        gc.setFill(Color.web("#8e9196"));
        gc.fillRect(0, sidewalkTopY, width, roadTopY - sidewalkTopY);

        // Внешняя граница тротуара вверху
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeLine(0, sidewalkTopY, width, sidewalkTopY);

        // Сетка плитки тротуара точно как на эскизе:
        // Горизонтальные линии
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.2);
        for (double y = sidewalkTopY + 7; y < roadTopY; y += 7.5) {
            gc.strokeLine(0, y, width, y);
        }

        // Диагонально-вертикальные косые линии швов плитки
        for (double x = -10; x < width + 40; x += 15) {
            gc.strokeLine(x, sidewalkTopY, x - 18, roadTopY);
        }

        // 3. Бордюрный камень (поребрик)
        gc.setFill(Color.web("#505359"));
        gc.fillRect(0, roadTopY, width, 5);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.6);
        gc.strokeLine(0, roadTopY, width, roadTopY);
        gc.strokeLine(0, roadTopY + 5, width, roadTopY + 5);

        // 4. Проезжая часть дороги
        gc.setFill(Color.web("#303236"));
        gc.fillRect(0, roadTopY + 5, width, height - (roadTopY + 5));

        // Белая разметка по центру дороги (неровные толстые штрихи как от руки на концепте)
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4.0);
        double markY = roadTopY + 32;
        double[] dashStarts = {10, 135, 330, 520, 715, 910};
        for (double dx : dashStarts) {
            gc.strokeLine(dx, markY, dx + 65, markY);
        }

        // Нижняя линия обочины/края дороги
        gc.setStroke(Color.web("#141414"));
        gc.setLineWidth(2.5);
        gc.strokeLine(0, height - 8, width, height - 8);

        // 5. Островки/кучи песка и земли (в точных координатах концепта)
        drawMounds(gc);

        // 6. Скамейка по центру
        drawBench(gc, 505, sidewalkTopY + 14);

        // 7. Урны для мусора (в точных позициях концепта)
        double[] urnXs = {88, 385, 606, 698};
        for (double ux : urnXs) {
            drawUrn(gc, ux, sidewalkTopY + 12);
        }
    }

    private void drawMounds(GraphicsContext gc) {
        // Цвет островков земли: #dd9571 с чёрным контуром
        gc.setFill(Color.web("#dd9571"));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);

        // Холмик 1: слева у края (x ~ 9, w ~ 32)
        drawSingleMound(gc, 9, sidewalkTopY + 24, 32, 16);

        // Холмик 2: у левого проезда (x ~ 273, w ~ 52)
        drawSingleMound(gc, 273, sidewalkTopY + 24, 52, 19);

        // Холмик 3: под лавочкой и слева от неё (x ~ 460, w ~ 45)
        drawSingleMound(gc, 460, sidewalkTopY + 25, 45, 17);

        // Холмик 4: справа от лавочки (x ~ 548, w ~ 44)
        drawSingleMound(gc, 548, sidewalkTopY + 25, 44, 15);

        // Холмик 5: справа (x ~ 715, w ~ 50)
        drawSingleMound(gc, 715, sidewalkTopY + 24, 50, 19);
    }

    private void drawSingleMound(GraphicsContext gc, double x, double y, double w, double h) {
        // Округлая форма холма с использованием Bezier/Quadratic Curve (примитив кривая)
        gc.beginPath();
        gc.moveTo(x, y);
        gc.quadraticCurveTo(x + w * 0.2, y - h * 1.05, x + w * 0.5, y - h);
        gc.quadraticCurveTo(x + w * 0.8, y - h * 0.95, x + w, y);
        gc.closePath();
        gc.fill();
        gc.stroke();
    }

    private void drawBench(GraphicsContext gc, double x, double y) {
        double w = 36;
        double h = 14;

        // Чёрный металлический каркас и ножки
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeLine(x + 3, y + h, x + 5, y + 2);
        gc.strokeLine(x + w - 3, y + h, x + w - 5, y + 2);
        gc.strokeLine(x + 4, y + h - 2, x + w - 4, y + h - 2);

        // Зелёные дощечки спинки и сиденья (#3f9935)
        gc.setFill(Color.web("#3f9935"));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.2);

        // Спинка
        gc.fillRect(x + 2, y, w - 4, 3.5);
        gc.strokeRect(x + 2, y, w - 4, 3.5);

        // Сиденье
        gc.fillRect(x, y + 6, w, 3.5);
        gc.strokeRect(x, y + 6, w, 3.5);
    }

    private void drawUrn(GraphicsContext gc, double x, double y) {
        double w = 11;
        double h = 18;

        // Ножка урны
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.2);
        gc.strokeLine(x + w / 2.0, y + h, x + w / 2.0, y + h + 5);

        // Корпус урны (серый цилиндрический бак)
        gc.setFill(Color.web("#5c6169"));
        gc.fillRect(x, y, w, h);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.4);
        gc.strokeRect(x, y, w, h);

        // Текстовая надпись на урне
        gc.setFill(Color.web("#ffffff"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 4.5));
        gc.fillText("УРНА", x + 0.8, y + 10);
    }
}
