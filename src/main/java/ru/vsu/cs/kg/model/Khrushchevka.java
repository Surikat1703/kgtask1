package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Khrushchevka implements Drawable {
    private static final int ROWS = 5;

    private static final double WINDOW_WIDTH_RATIO = 0.65;
    private static final double WINDOW_HEIGHT_RATIO = 0.68;

    private static final double PARAPET_OVERHANG = 2.0;
    private static final double PARAPET_HEIGHT = 7.0;
    private static final double PARAPET_RAISE = 6.0;
    private static final double PARAPET_HATCH_START = 12.0;
    private static final double PARAPET_HATCH_STEP = 35.0;
    private static final double PARAPET_HATCH_SHIFT = 8.0;

    private static final double FACADE_LINE_WIDTH = 2.2;
    private static final double PARAPET_LINE_WIDTH = 1.2;
    private static final double SEAM_LINE_WIDTH = 1.6;

    private static final double ANTENNA_HEIGHT = 24.0;
    private static final double ANTENNA_LINE_WIDTH = 2.0;
    private static final double ANTENNA_WHISKER_WIDTH = 1.4;

    private static final Color FACADE_FRONT = Color.web("#999a9e");
    private static final Color FACADE_BACK = Color.web("#757980");
    private static final Color PARAPET_FRONT = Color.web("#787b80");
    private static final Color PARAPET_BACK = Color.web("#5e6269");

    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final double cellWidth;
    private final double cellHeight;
    private final int cols;
    private final boolean isBackground;
    private final List<WindowElement> windows = new ArrayList<>();
    private final List<Double> antennaXs = new ArrayList<>();

    public Khrushchevka(double x,
                        double y,
                        double width,
                        double height,
                        int cols,
                        boolean isBackground,
                        Random rng) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.cols = cols;
        this.isBackground = isBackground;
        this.cellWidth = width / cols;
        this.cellHeight = height / ROWS;

        createWindows(rng);
        createAntennas();
    }

    private void createWindows(Random rng) {
        double winWidth = cellWidth * WINDOW_WIDTH_RATIO;
        double winHeight = cellHeight * WINDOW_HEIGHT_RATIO;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < cols; col++) {
                double wx = col * cellWidth + (cellWidth - winWidth) / 2.0;
                double wy = row * cellHeight + (cellHeight - winHeight) / 2.0;
                windows.add(new WindowElement(wx, wy, winWidth, winHeight, rng));
            }
        }
    }

    private void createAntennas() {
        if (cols >= 8) {
            antennaXs.add(width * 0.22);
            antennaXs.add(width * 0.52);
            antennaXs.add(width * 0.80);
        } else if (cols >= 4) {
            antennaXs.add(width * 0.35);
            antennaXs.add(width * 0.72);
        } else {
            antennaXs.add(width * 0.5);
        }
    }

    public boolean handleClick(double px, double py) {
        for (WindowElement window : windows) {
            if (window.contains(x, y, px, py)) {
                window.cycleType();
                return true;
            }
        }
        return false;
    }

    public void cycleAllWindows() {
        for (WindowElement window : windows) {
            window.cycleType();
        }
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        drawAntennas(gc);
        drawFacade(gc);
        drawParapet(gc);
        drawSeams(gc);
        drawWindows(gc);
    }

    private void drawFacade(GraphicsContext gc) {
        gc.setFill(isBackground ? FACADE_BACK : FACADE_FRONT);
        gc.fillRect(x, y, width, height);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(FACADE_LINE_WIDTH);
        gc.strokeRect(x, y, width, height);
    }

    private void drawParapet(GraphicsContext gc) {
        double parapetY = y - PARAPET_RAISE;
        double parapetWidth = width + PARAPET_OVERHANG * 2;

        gc.setFill(isBackground ? PARAPET_BACK : PARAPET_FRONT);
        gc.fillRect(x - PARAPET_OVERHANG, parapetY, parapetWidth, PARAPET_HEIGHT);
        gc.strokeRect(x - PARAPET_OVERHANG, parapetY, parapetWidth, PARAPET_HEIGHT);

        gc.setLineWidth(PARAPET_LINE_WIDTH);
        for (double k = x + PARAPET_HATCH_START; k < x + width; k += PARAPET_HATCH_STEP) {
            gc.strokeLine(k, parapetY, k + PARAPET_HATCH_SHIFT, y);
        }
    }

    private void drawSeams(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(SEAM_LINE_WIDTH);

        for (int row = 1; row < ROWS; row++) {
            double seamY = y + row * cellHeight;
            gc.strokeLine(x, seamY, x + width, seamY);
        }

        for (int col = 1; col < cols; col++) {
            double seamX = x + col * cellWidth;
            gc.strokeLine(seamX, y, seamX, y + height);
        }
    }

    private void drawWindows(GraphicsContext gc) {
        for (WindowElement window : windows) {
            window.draw(gc, x, y);
        }
    }

    private void drawAntennas(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(ANTENNA_LINE_WIDTH);

        for (double relX : antennaXs) {
            double antennaX = x + relX;
            double topY = y - ANTENNA_HEIGHT;

            gc.strokeLine(antennaX, y, antennaX, topY);

            gc.strokeLine(antennaX - 10, topY + 4, antennaX + 10, topY + 4);
            gc.strokeLine(antennaX - 14, topY + 10, antennaX + 14, topY + 10);
            gc.strokeLine(antennaX - 8, topY + 16, antennaX + 8, topY + 16);

            gc.setLineWidth(ANTENNA_WHISKER_WIDTH);
            gc.strokeLine(antennaX - 10, topY + 4, antennaX - 13, topY - 2);
            gc.strokeLine(antennaX + 10, topY + 4, antennaX + 13, topY - 2);
            gc.strokeLine(antennaX - 14, topY + 10, antennaX - 17, topY + 5);
            gc.strokeLine(antennaX + 14, topY + 10, antennaX + 17, topY + 5);
            gc.setLineWidth(ANTENNA_LINE_WIDTH);
        }
    }
}
