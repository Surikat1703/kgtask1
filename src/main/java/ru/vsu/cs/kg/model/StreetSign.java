package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StreetSign implements Drawable {
    private static final Color POLE_COLOR = Color.web("#3a3f45");
    private static final Color PLATE_COLOR = Color.web("#1d4f91");
    private static final Color PLATE_BORDER = Color.web("#e9edf2");
    private static final Color TEXT_COLOR = Color.WHITE;

    private static final double POLE_WIDTH = 2.5;
    private static final double POLE_HEIGHT = 52.0;
    private static final double PLATE_WIDTH = 30.0;
    private static final double PLATE_HEIGHT = 10.0;
    private static final double PLATE_X_OFFSET = 8.0;
    private static final double PLATE_Y_OFFSET = 4.0;
    private static final double PLATE_BORDER_WIDTH = 0.8;
    private static final double TEXT_SIZE = 6.4;
    private static final double TEXT_BASELINE_OFFSET = 7.4;
    private static final double TEXT_LEFT_OFFSET = 5.0;

    private static final Font FONT = Font.font("Arial", FontWeight.BOLD, TEXT_SIZE);

    private final double x;
    private final double baseY;
    private final String text;

    public StreetSign(double x, double baseY, String text) {
        this.x = x;
        this.baseY = baseY;
        this.text = text;
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        double plateX = x + PLATE_X_OFFSET;
        double plateY = baseY - POLE_HEIGHT + PLATE_Y_OFFSET;
        double poleTop = plateY - 5;

        gc.setStroke(POLE_COLOR);
        gc.setLineWidth(POLE_WIDTH);
        gc.strokeLine(x, baseY, x, poleTop);

        gc.beginPath();
        gc.moveTo(x, poleTop);
        gc.bezierCurveTo(x + 8, poleTop, x + 10, plateY - 2, x + 10, plateY + 1);
        gc.stroke();

        gc.setFill(PLATE_COLOR);
        gc.fillRect(plateX, plateY, PLATE_WIDTH, PLATE_HEIGHT);

        gc.setStroke(PLATE_BORDER);
        gc.setLineWidth(PLATE_BORDER_WIDTH);
        gc.strokeRect(plateX, plateY, PLATE_WIDTH, PLATE_HEIGHT);

        gc.setFill(TEXT_COLOR);
        gc.setFont(FONT);
        gc.fillText(text, plateX + TEXT_LEFT_OFFSET, plateY + TEXT_BASELINE_OFFSET);
    }
}
