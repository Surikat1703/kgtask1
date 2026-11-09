package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

import java.util.Random;

public class WindowElement {
    public enum WindowType {
        SINGLE_VERTICAL,
        FULL_CROSS,
        CROSS_NO_LEFT;

        WindowType next() {
            WindowType[] all = values();
            return all[(ordinal() + 1) % all.length];
        }
    }

    private static final Color[] GLASS_COLORS = {
            Color.web("#0e1236"),
            Color.web("#be3ec9"),
            Color.web("#ffe600")
    };

    private static final double GLASS_PADDING = 2.0;
    private static final double FRAME_LINE_WIDTH = 2.2;
    private static final double MULLION_LINE_WIDTH = 2.2;
    private static final double HORIZONTAL_MULLION_RATIO = 0.40;

    private final double relX;
    private final double relY;
    private final double width;
    private final double height;
    private final Color glassColor;
    private WindowType type;

    public WindowElement(double relX, double relY, double width, double height, Random rng) {
        this.relX = relX;
        this.relY = relY;
        this.width = width;
        this.height = height;
        this.type = WindowType.values()[rng.nextInt(WindowType.values().length)];
        this.glassColor = GLASS_COLORS[rng.nextInt(GLASS_COLORS.length)];
    }

    public void cycleType() {
        type = type.next();
    }

    public boolean contains(double parentX, double parentY, double px, double py) {
        double absX = parentX + relX;
        double absY = parentY + relY;
        return px >= absX && px <= absX + width && py >= absY && py <= absY + height;
    }

    public void draw(GraphicsContext gc, double baseX, double baseY) {
        double x = baseX + relX;
        double y = baseY + relY;
        double glassWidth = width - GLASS_PADDING * 2;
        double glassHeight = height - GLASS_PADDING * 2;

        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, width, height);

        gc.setFill(glassColor);
        gc.fillRect(x + GLASS_PADDING, y + GLASS_PADDING, glassWidth, glassHeight);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(FRAME_LINE_WIDTH);
        gc.strokeRect(x + GLASS_PADDING + 0.5, y + GLASS_PADDING + 0.5,
                glassWidth - 1.0, glassHeight - 1.0);

        drawMullions(gc, x, y, glassWidth, glassHeight);
    }

    private void drawMullions(GraphicsContext gc, double x, double y,
                              double glassWidth, double glassHeight) {
        StrokeLineCap previousCap = gc.getLineCap();
        gc.setLineCap(StrokeLineCap.BUTT);
        gc.setLineWidth(MULLION_LINE_WIDTH);

        try {
            double glassX = x + GLASS_PADDING;
            double glassY = y + GLASS_PADDING;
            double midX = glassX + glassWidth * 0.5;
            double midY = glassY + glassHeight * HORIZONTAL_MULLION_RATIO;

            gc.strokeLine(midX, glassY, midX, glassY + glassHeight);

            switch (type) {
                case SINGLE_VERTICAL -> {
                }
                case FULL_CROSS -> gc.strokeLine(glassX, midY, glassX + glassWidth, midY);
                case CROSS_NO_LEFT -> gc.strokeLine(midX, midY, glassX + glassWidth, midY);
            }
        } finally {
            gc.setLineCap(previousCap);
        }
    }
}
