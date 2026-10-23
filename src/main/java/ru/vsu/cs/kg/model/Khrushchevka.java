package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Панельный дом хрущевка (серия 1-464 / 1-468).
 * Включает:
 * - Бетонный фасад из серых плит со швами (прямоугольники и линии)
 * - Крыша с парапетом и телевизионными антеннами (сложные ломаные линии)
 * - Сетка случайно сгенерированных окон (WindowElement)
 * - Адресная табличка (осмысленная текстовая надпись, fillText)
 * - Входной подъезд / дверь с козырьком
 */
public class Khrushchevka implements Drawable {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final int floors;
    private final int entrances; // Подъезды
    private final int windowsPerRow;
    private final String streetAddress;
    private final List<WindowElement> windows = new ArrayList<>();
    private final List<Antenna> antennas = new ArrayList<>();

    public static class Antenna {
        double relX;
        double height;
        int crossbars;

        public Antenna(double relX, double height, int crossbars) {
            this.relX = relX;
            this.height = height;
            this.crossbars = crossbars;
        }
    }

    public Khrushchevka(double x, double y, double width, double height,
                        int floors, int windowsPerRow, String streetAddress, Random rng) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.floors = floors;
        this.entrances = 2;
        this.windowsPerRow = windowsPerRow;
        this.streetAddress = streetAddress;

        // Генерация окон по этажам и стоякам
        double padX = 18.0;
        double padY = 22.0;
        double winW = (width - padX * 2) / windowsPerRow - 10.0;
        double winH = (height - padY * 2 - 15) / floors - 12.0;

        for (int r = 0; r < floors; r++) {
            for (int c = 0; c < windowsPerRow; c++) {
                double wx = padX + c * (winW + 10.0) + 5.0;
                double wy = padY + r * (winH + 12.0) + 5.0;
                windows.add(new WindowElement(wx, wy, winW, winH, rng));
            }
        }

        // Генерация ТВ-антенн на крыше как на эскизе
        int antCount = 3 + rng.nextInt(3);
        for (int i = 0; i < antCount; i++) {
            double ax = padX + rng.nextDouble() * (width - padX * 2);
            double ah = 20.0 + rng.nextDouble() * 22.0;
            antennas.add(new Antenna(ax, ah, 3 + rng.nextInt(3)));
        }
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        // 1. Антенны на крыше (рисуются за/над парапетом)
        drawAntennas(gc);

        // 2. Основное тело дома (серые бетонные панели)
        gc.setFill(Color.web("#80868f"));
        gc.fillRect(x, y, width, height);

        // Окантовка контура дома
        gc.setStroke(Color.web("#2c2d30"));
        gc.setLineWidth(2.0);
        gc.strokeRect(x, y, width, height);

        // 3. Бетонные швы панелей (горизонтальные и вертикальные линии)
        gc.setStroke(Color.web("#4b4e54"));
        gc.setLineWidth(1.5);

        // Горизонтальные межпанельные швы
        double panelH = height / floors;
        for (int i = 1; i < floors; i++) {
            double sy = y + i * panelH;
            gc.strokeLine(x, sy, x + width, sy);
        }

        // Вертикальные швы
        double panelW = width / windowsPerRow;
        for (int i = 1; i < windowsPerRow; i++) {
            double sx = x + i * panelW;
            gc.strokeLine(sx, y, sx, y + height);
        }

        // 4. Карниз / парапет крыши
        gc.setFill(Color.web("#5e6269"));
        gc.fillRect(x - 2, y - 6, width + 4, 7);
        gc.setStroke(Color.web("#232426"));
        gc.setLineWidth(1.5);
        gc.strokeRect(x - 2, y - 6, width + 4, 7);

        // Маленькие выступы вентиляционных шахт на крыше
        gc.setFill(Color.web("#4f5359"));
        gc.fillRect(x + width * 0.15, y - 12, 16, 7);
        gc.strokeRect(x + width * 0.15, y - 12, 16, 7);
        gc.fillRect(x + width * 0.75, y - 13, 20, 8);
        gc.strokeRect(x + width * 0.75, y - 13, 20, 8);

        // 5. Отрисовка всех окон
        for (WindowElement win : windows) {
            win.draw(gc, x, y);
        }

        // 6. Осмысленная текстовая надпись: адресная табличка на торце / стене дома
        drawAddressPlate(gc);
    }

    private void drawAntennas(GraphicsContext gc) {
        gc.setStroke(Color.web("#222325"));
        gc.setLineWidth(1.6);

        for (Antenna ant : antennas) {
            double ax = x + ant.relX;
            double topY = y - ant.height;
            // Центральная мачта
            gc.strokeLine(ax, y, ax, topY);

            // Поперечные перекладины антенны
            for (int k = 1; k <= ant.crossbars; k++) {
                double barY = topY + k * (ant.height / (ant.crossbars + 1.2));
                double barW = 8.0 + (ant.crossbars - k) * 3.5;
                gc.strokeLine(ax - barW, barY, ax + barW, barY);
            }
            // Диагональные оттяжки
            gc.setLineWidth(0.8);
            gc.strokeLine(ax, topY + 6, ax - 7, y);
            gc.strokeLine(ax, topY + 6, ax + 7, y);
            gc.setLineWidth(1.6);
        }
    }

    private void drawAddressPlate(GraphicsContext gc) {
        double plateX = x + 10;
        double plateY = y + height - 28;
        double plateW = 100;
        double plateH = 20;

        // Синяя советская табличка с адресом
        gc.setFill(Color.web("#143570"));
        gc.fillRect(plateX, plateY, plateW, plateH);
        gc.setStroke(Color.web("#ffffff"));
        gc.setLineWidth(1.0);
        gc.strokeRect(plateX + 1, plateY + 1, plateW - 2, plateH - 2);

        // Белый текст на табличке (примитив ТЕКСТ)
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 9.5));
        gc.fillText(streetAddress, plateX + 5, plateY + 14);
    }
}
