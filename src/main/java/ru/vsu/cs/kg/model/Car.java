package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

/**
 * Автомобиль точь-в-точь по концепту:
 * - Синий угловатый седан (#3466ee кузов, #6ea4ff стекла)
 * - Чёрный толстый контур (нарисован в стиле скетча)
 * - Чёрные колёса
 * - Проезжает по дороге раз в periodSeconds секунд
 */
public class Car implements Drawable {
    private final double sceneWidth;
    private final double roadY;
    private final double periodSeconds;
    private final double driveDuration;

    public Car(double sceneWidth, double roadY, double periodSeconds, double driveDuration) {
        this.sceneWidth = sceneWidth;
        this.roadY = roadY;
        this.periodSeconds = periodSeconds;
        this.driveDuration = driveDuration;
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        double cycle = time % periodSeconds;
        if (cycle > driveDuration) {
            return;
        }

        double progress = cycle / driveDuration;
        double carW = 50.0;
        double carH = 26.0;
        double x = -carW - 20 + progress * (sceneWidth + carW + 40);
        double y = roadY + 5;

        // 1. Корпус машины (ярко-синий как на концепте: #3b6ced)
        // Нижняя часть (прямоугольник)
        gc.setFill(Color.web("#3b6ced"));
        gc.fillRect(x, y + 10, carW, 12);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeRect(x, y + 10, carW, 12);

        // Верхняя часть (кабина со скошенным лобовым и задним стеклом)
        double[] cabX = {x + 6, x + 14, x + carW - 14, x + carW - 6};
        double[] cabY = {y + 10, y, y, y + 10};
        gc.setFill(Color.web("#3b6ced"));
        gc.fillPolygon(cabX, cabY, 4);
        gc.strokePolygon(cabX, cabY, 4);

        // Стекла кабины (светло-голубые #6ea4ff)
        double[] winX = {x + 9, x + 15, x + carW - 15, x + carW - 9};
        double[] winY = {y + 9, y + 2, y + 2, y + 9};
        gc.setFill(Color.web("#6ea4ff"));
        gc.fillPolygon(winX, winY, 4);
        gc.strokePolygon(winX, winY, 4);

        // Центральная стойка окна
        gc.strokeLine(x + carW * 0.5, y + 2, x + carW * 0.5, y + 9);

        // 2. Фары
        // Передняя фара (жёлтая)
        gc.setFill(Color.web("#ffe600"));
        gc.fillRect(x + carW - 2, y + 12, 3, 4);
        gc.strokeRect(x + carW - 2, y + 12, 3, 4);

        // Задняя фара (красная)
        gc.setFill(Color.web("#ee2222"));
        gc.fillRect(x - 1, y + 12, 3, 4);
        gc.strokeRect(x - 1, y + 12, 3, 4);

        // 3. Колёса (чёрные круги, примитив круг/эллипс)
        double wheelRadius = 5.5;
        double w1X = x + 11;
        double w2X = x + carW - 11;
        double wheelY = y + 21;

        // Колесные арки (примитив ARC)
        gc.setLineWidth(1.6);
        gc.strokeArc(w1X - wheelRadius - 1.5, wheelY - wheelRadius - 1.5, (wheelRadius + 1.5) * 2, (wheelRadius + 1.5) * 2, 0, 180, ArcType.OPEN);
        gc.strokeArc(w2X - wheelRadius - 1.5, wheelY - wheelRadius - 1.5, (wheelRadius + 1.5) * 2, (wheelRadius + 1.5) * 2, 0, 180, ArcType.OPEN);

        // Сами шины
        gc.setFill(Color.BLACK);
        gc.fillOval(w1X - wheelRadius, wheelY - wheelRadius, wheelRadius * 2, wheelRadius * 2);
        gc.fillOval(w2X - wheelRadius, wheelY - wheelRadius, wheelRadius * 2, wheelRadius * 2);

        // Диски колёс
        gc.setFill(Color.web("#999999"));
        gc.fillOval(w1X - 2.5, wheelY - 2.5, 5, 5);
        gc.fillOval(w2X - 2.5, wheelY - 2.5, 5, 5);
    }
}
