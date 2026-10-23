package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Проезжающий автомобиль (ВАЗ-2101 / классический седан в стиле эскиза).
 * Проезжает раз в periodSeconds секунд.
 * Использует примитивы:
 * - Прямоугольники и трапеции для кузова
 * - Эллипсы / круги для колес и колпаков
 * - Арки колесных арок (ArcType.OPEN)
 * - Овалы со световым градиентом для лучей фар
 * - Текстовую надпись с госномером (например, "в777ах 36")
 */
public class Car implements Drawable {
    private final double roadY;
    private final double sceneWidth;
    private final double periodSeconds; // Период появления автомобиля (раз в N секунд)
    private final double driveDuration; // Время движения через весь экран
    private final double carWidth = 90.0;
    private final double carHeight = 36.0;

    public Car(double sceneWidth, double roadY, double periodSeconds, double driveDuration) {
        this.sceneWidth = sceneWidth;
        this.roadY = roadY;
        this.periodSeconds = periodSeconds;
        this.driveDuration = driveDuration;
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        // Расчёт цикла времени: автомобиль проезжает с задержкой
        double cycleTime = time % periodSeconds;

        // Если сейчас интервал ожидания — машина за пределами экрана
        if (cycleTime > driveDuration) {
            return;
        }

        // Прогресс движения от 0.0 до 1.0 (слева направо)
        double progress = cycleTime / driveDuration;
        double currentX = -carWidth - 50 + progress * (sceneWidth + carWidth + 100);
        double currentY = roadY - carHeight + 10;

        // 1. Свет передних фар (проецируется вперед на дорогу)
        double headlightX = currentX + carWidth;
        double headlightY = currentY + carHeight * 0.65;

        // Конус света фар (многоугольник или овал)
        double[] coneX = {headlightX, headlightX + 110, headlightX + 110};
        double[] coneY = {headlightY, headlightY - 15, headlightY + 25};
        RadialGradient headBeam = new RadialGradient(
                0, 0, headlightX, headlightY, 110, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(255, 255, 200, 0.45)),
                new Stop(0.7, Color.rgb(255, 240, 150, 0.15)),
                new Stop(1.0, Color.TRANSPARENT)
        );
        gc.setFill(headBeam);
        gc.fillPolygon(coneX, coneY, 3);

        // 2. Кузов машины (ярко-голубой, как на эскизе)
        // Нижняя часть (прямоугольник)
        gc.setFill(Color.web("#2b74d9"));
        gc.fillRect(currentX, currentY + 12, carWidth, 16);
        gc.setStroke(Color.web("#143566"));
        gc.setLineWidth(1.5);
        gc.strokeRect(currentX, currentY + 12, carWidth, 16);

        // Кабина (трапеция крыши)
        double roofLeft = currentX + 18;
        double roofRight = currentX + carWidth - 22;
        double roofTop = currentY;
        double cabinBottom = currentY + 12;

        double[] cabX = {roofLeft - 8, roofLeft, roofRight, roofRight + 8};
        double[] cabY = {cabinBottom, roofTop, roofTop, cabinBottom};
        gc.setFill(Color.web("#3988f7"));
        gc.fillPolygon(cabX, cabY, 4);
        gc.strokePolygon(cabX, cabY, 4);

        // Окна машины (голубовато-белые стекла)
        gc.setFill(Color.web("#d2e7ff"));
        double[] winBackX = {roofLeft - 5, roofLeft + 2, currentX + 38, currentX + 38};
        double[] winBackY = {cabinBottom - 1, roofTop + 2, roofTop + 2, cabinBottom - 1};
        gc.fillPolygon(winBackX, winBackY, 4);

        double[] winFrontX = {currentX + 42, currentX + 42, roofRight - 2, roofRight + 5};
        double[] winFrontY = {cabinBottom - 1, roofTop + 2, roofTop + 2, cabinBottom - 1};
        gc.fillPolygon(winFrontX, winFrontY, 4);

        // Стойка между окнами
        gc.setStroke(Color.web("#143566"));
        gc.setLineWidth(2.0);
        gc.strokeLine(currentX + 40, roofTop + 1, currentX + 40, cabinBottom);

        // Дверная ручка
        gc.setFill(Color.web("#c0c0c0"));
        gc.fillRect(currentX + 32, currentY + 16, 6, 2);

        // 3. Бамперы (металлические)
        gc.setFill(Color.web("#c4c9d4"));
        gc.fillRect(currentX - 3, currentY + 22, 4, 5); // задний
        gc.fillRect(currentX + carWidth - 1, currentY + 22, 4, 5); // передний

        // 4. Фары
        // Передняя фара
        gc.setFill(Color.web("#fff6bd"));
        gc.fillOval(currentX + carWidth - 2, currentY + 15, 4, 6);
        // Задний фонарь (красный)
        gc.setFill(Color.web("#e62e2e"));
        gc.fillOval(currentX - 2, currentY + 15, 3, 5);

        // 5. Текстовая надпись: Госномер автомобиля (примитив ТЕКСТ)
        gc.setFill(Color.WHITE);
        gc.fillRect(currentX + carWidth * 0.42, currentY + 20, 24, 7);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.6);
        gc.strokeRect(currentX + carWidth * 0.42, currentY + 20, 24, 7);
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 5.5));
        gc.fillText("В777АХ36", currentX + carWidth * 0.42 + 1, currentY + 25.5);

        // 6. Колёса и колёсные арки (Арки ArcType.OPEN + Эллипсы)
        double wheelRadius = 8.5;
        double wheelY = currentY + 25;
        double wheel1X = currentX + 18;
        double wheel2X = currentX + carWidth - 20;

        // Колёсные арки (вырез в кузове)
        gc.setStroke(Color.web("#143566"));
        gc.setLineWidth(2.0);
        gc.strokeArc(wheel1X - wheelRadius - 2, wheelY - wheelRadius - 2, (wheelRadius + 2) * 2, (wheelRadius + 2) * 2, 0, 180, ArcType.OPEN);
        gc.strokeArc(wheel2X - wheelRadius - 2, wheelY - wheelRadius - 2, (wheelRadius + 2) * 2, (wheelRadius + 2) * 2, 0, 180, ArcType.OPEN);

        // Сами шины (тёмно-серые эллипсы)
        gc.setFill(Color.web("#17171a"));
        gc.fillOval(wheel1X - wheelRadius, wheelY - wheelRadius + 2, wheelRadius * 2, wheelRadius * 2);
        gc.fillOval(wheel2X - wheelRadius, wheelY - wheelRadius + 2, wheelRadius * 2, wheelRadius * 2);

        // Диски / колпаки колёс (светло-серые)
        gc.setFill(Color.web("#bcc4cc"));
        gc.fillOval(wheel1X - wheelRadius * 0.5, wheelY - wheelRadius * 0.5 + 2, wheelRadius, wheelRadius);
        gc.fillOval(wheel2X - wheelRadius * 0.5, wheelY - wheelRadius * 0.5 + 2, wheelRadius, wheelRadius);

        // Спицы колёс (вращение при движении)
        double wheelAngle = time * 20.0;
        gc.setStroke(Color.web("#303338"));
        gc.setLineWidth(1.0);
        double cosA = Math.cos(wheelAngle) * (wheelRadius * 0.5);
        double sinA = Math.sin(wheelAngle) * (wheelRadius * 0.5);
        gc.strokeLine(wheel1X - cosA, wheelY + 2 - sinA, wheel1X + cosA, wheelY + 2 + sinA);
        gc.strokeLine(wheel2X - cosA, wheelY + 2 - sinA, wheel2X + cosA, wheelY + 2 + sinA);
    }
}
