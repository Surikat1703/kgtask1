package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class Car implements Drawable {
    private static final Color BODY_COLOR = Color.web("#3b6ced");
    private static final Color GLASS_COLOR = Color.web("#6ea4ff");
    private static final Color HEADLIGHT_COLOR = Color.web("#ffe600");
    private static final Color TAILLIGHT_COLOR = Color.web("#ee2222");
    private static final Color HUBCAP_COLOR = Color.web("#999999");

    private static final double CAR_WIDTH = 50.0;
    private static final double WHEEL_RADIUS = 5.5;
    private static final double CAR_SCREEN_MARGIN = 20.0;
    private static final double CAR_Y_OFFSET = 5.0;

    private static final double BODY_TOP = 10.0;
    private static final double BODY_HEIGHT = 12.0;
    private static final double CABIN_NEAR_INSET = 6.0;
    private static final double CABIN_FAR_INSET = 14.0;
    private static final double WINDOW_NEAR_INSET = 9.0;
    private static final double WINDOW_FAR_INSET = 15.0;
    private static final double WINDOW_TOP = 2.0;
    private static final double WINDOW_BOTTOM = 9.0;
    private static final double LIGHT_Y = 12.0;
    private static final double LIGHT_WIDTH = 3.0;
    private static final double LIGHT_HEIGHT = 4.0;
    private static final double WHEEL_Y = 21.0;
    private static final double WHEEL_X_INSET = 11.0;
    private static final double HUB_RADIUS = 2.5;

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
        double x = -CAR_WIDTH - CAR_SCREEN_MARGIN
                + progress * (sceneWidth + CAR_WIDTH + CAR_SCREEN_MARGIN * 2);
        double y = roadY + CAR_Y_OFFSET;

        drawBody(gc, x, y);
        drawCabin(gc, x, y);
        drawWindows(gc, x, y);
        drawLights(gc, x, y);
        drawWheels(gc, x, y);
    }

    private void drawBody(GraphicsContext gc, double x, double y) {
        gc.setFill(BODY_COLOR);
        gc.fillRect(x, y + BODY_TOP, CAR_WIDTH, BODY_HEIGHT);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeRect(x, y + BODY_TOP, CAR_WIDTH, BODY_HEIGHT);
    }

    private void drawCabin(GraphicsContext gc, double x, double y) {
        gc.setFill(BODY_COLOR);
        double[] cabinX = {
                x + CABIN_NEAR_INSET,
                x + CABIN_FAR_INSET,
                x + CAR_WIDTH - CABIN_FAR_INSET,
                x + CAR_WIDTH - CABIN_NEAR_INSET
        };
        double[] cabinY = {y + BODY_TOP, y, y, y + BODY_TOP};
        gc.fillPolygon(cabinX, cabinY, cabinX.length);
        gc.strokePolygon(cabinX, cabinY, cabinX.length);
    }

    private void drawWindows(GraphicsContext gc, double x, double y) {
        double[] windowX = {
                x + WINDOW_NEAR_INSET,
                x + WINDOW_FAR_INSET,
                x + CAR_WIDTH - WINDOW_FAR_INSET,
                x + CAR_WIDTH - WINDOW_NEAR_INSET
        };
        double[] windowY = {y + WINDOW_BOTTOM, y + WINDOW_TOP, y + WINDOW_TOP, y + WINDOW_BOTTOM};

        gc.setFill(GLASS_COLOR);
        gc.fillPolygon(windowX, windowY, windowX.length);
        gc.strokePolygon(windowX, windowY, windowX.length);

        gc.strokeLine(x + CAR_WIDTH * 0.5, y + WINDOW_TOP,
                x + CAR_WIDTH * 0.5, y + WINDOW_BOTTOM);
    }

    private void drawLights(GraphicsContext gc, double x, double y) {
        gc.setFill(HEADLIGHT_COLOR);
        gc.fillRect(x + CAR_WIDTH - 2, y + LIGHT_Y, LIGHT_WIDTH, LIGHT_HEIGHT);
        gc.strokeRect(x + CAR_WIDTH - 2, y + LIGHT_Y, LIGHT_WIDTH, LIGHT_HEIGHT);

        gc.setFill(TAILLIGHT_COLOR);
        gc.fillRect(x - 1, y + LIGHT_Y, LIGHT_WIDTH, LIGHT_HEIGHT);
        gc.strokeRect(x - 1, y + LIGHT_Y, LIGHT_WIDTH, LIGHT_HEIGHT);
    }

    private void drawWheels(GraphicsContext gc, double x, double y) {
        double wheelY = y + WHEEL_Y;
        for (double wheelX : new double[] {x + WHEEL_X_INSET, x + CAR_WIDTH - WHEEL_X_INSET}) {
            drawWheel(gc, wheelX, wheelY);
        }
    }

    private void drawWheel(GraphicsContext gc, double wheelX, double wheelY) {
        double archRadius = WHEEL_RADIUS + 1.5;
        gc.setLineWidth(1.6);
        gc.strokeArc(wheelX - archRadius, wheelY - archRadius,
                archRadius * 2, archRadius * 2, 0, 180, ArcType.OPEN);

        gc.setFill(Color.BLACK);
        gc.fillOval(wheelX - WHEEL_RADIUS, wheelY - WHEEL_RADIUS,
                WHEEL_RADIUS * 2, WHEEL_RADIUS * 2);

        gc.setFill(HUBCAP_COLOR);
        gc.fillOval(wheelX - HUB_RADIUS, wheelY - HUB_RADIUS,
                HUB_RADIUS * 2, HUB_RADIUS * 2);
    }
}
