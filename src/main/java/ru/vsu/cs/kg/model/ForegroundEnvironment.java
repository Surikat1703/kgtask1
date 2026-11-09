package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.List;

public class ForegroundEnvironment implements Drawable {
    private static final Color SIDEWALK_COLOR = Color.web("#8e9196");
    private static final Color TILE_SEAM_COLOR = Color.web("#606368");
    private static final Color CURB_COLOR = Color.web("#505359");
    private static final Color ROAD_COLOR = Color.web("#303236");
    private static final Color DRIVEWAY_COLOR = Color.web("#42454a");
    private static final Color DRIVEWAY_EDGE_COLOR = Color.web("#70747a");
    private static final Color BENCH_COLOR = Color.web("#3f9935");
    private static final Color BUSH_FILL_COLOR = Color.web("#3f7a3a");
    private static final Color BUSH_STROKE_COLOR = Color.web("#244a26");
    private static final Color URN_BODY_COLOR = Color.web("#3d4540");
    private static final Color URN_LID_COLOR = Color.web("#2f3632");
    private static final Color URN_BASE_COLOR = Color.web("#23272a");

    private static final double CURB_HEIGHT = 5.0;
    private static final double TILE_ROW_START = 7.0;
    private static final double TILE_ROW_STEP = 7.5;
    private static final double TILE_DIAGONAL_START = -10.0;
    private static final double TILE_DIAGONAL_STEP = 15.0;
    private static final double TILE_DIAGONAL_SHIFT = 18.0;

    private static final double ROAD_MARK_Y_OFFSET = 13.0;
    private static final double ROAD_MARK_START = 10.0;
    private static final double ROAD_MARK_LENGTH = 26.0;
    private static final double ROAD_MARK_STEP = 52.0;
    private static final double ROAD_MARK_WIDTH = 2.0;

    private static final double BENCH_X = 505.0;
    private static final double BENCH_TOP_OFFSET = 4.0;
    private static final double BENCH_WIDTH = 38.0;
    private static final double[] URN_XS = {88, 385, 606, 698};
    private static final double URN_BASE_OFFSET = 19.0;
    private static final double[][] BUSH_POSITIONS = {{120, 14}, {402, 15}, {620, 14}, {780, 15}};

    private final double width;
    private final double height;
    private final double sidewalkTopY;
    private final double roadTopY;
    private final List<Driveway> driveways;

    public ForegroundEnvironment(double width,
                                 double height,
                                 double sidewalkTopY,
                                 double roadTopY,
                                 List<Driveway> driveways) {
        this.width = width;
        this.height = height;
        this.sidewalkTopY = sidewalkTopY;
        this.roadTopY = roadTopY;
        this.driveways = List.copyOf(driveways);
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        drawSidewalk(gc);
        drawDriveways(gc);
        drawCurbAndRoad(gc);
        drawBushes(gc);
        drawStreetFurniture(gc);
    }

    private void drawSidewalk(GraphicsContext gc) {
        gc.setFill(SIDEWALK_COLOR);
        gc.fillRect(0, sidewalkTopY, width, roadTopY - sidewalkTopY);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeLine(0, sidewalkTopY, width, sidewalkTopY);

        drawTileGrid(gc);
    }

    private void drawTileGrid(GraphicsContext gc) {
        gc.setStroke(TILE_SEAM_COLOR);
        gc.setLineWidth(1.0);

        for (double y = sidewalkTopY + TILE_ROW_START; y < roadTopY; y += TILE_ROW_STEP) {
            double from = 0;
            for (Driveway driveway : driveways) {
                gc.strokeLine(from, y, driveway.x1(), y);
                from = driveway.x2();
            }
            gc.strokeLine(from, y, width, y);
        }

        for (double x = TILE_DIAGONAL_START; x < width + 40; x += TILE_DIAGONAL_STEP) {
            if (insideDriveway(x)) {
                continue;
            }
            gc.strokeLine(x, sidewalkTopY, x - TILE_DIAGONAL_SHIFT, roadTopY);
        }
    }

    private void drawDriveways(GraphicsContext gc) {
        for (Driveway driveway : driveways) {
            drawDriveway(gc, driveway);
        }
    }

    private boolean insideDriveway(double x) {
        for (Driveway driveway : driveways) {
            if (x >= driveway.x1() - 5 && x <= driveway.x2() + 5) {
                return true;
            }
        }
        return false;
    }

    private void drawDriveway(GraphicsContext gc, Driveway driveway) {
        gc.setFill(DRIVEWAY_COLOR);
        gc.fillRect(driveway.x1(), sidewalkTopY, driveway.width(), roadTopY - sidewalkTopY);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.8);
        gc.strokeLine(driveway.x1(), sidewalkTopY, driveway.x1(), roadTopY);
        gc.strokeLine(driveway.x2(), sidewalkTopY, driveway.x2(), roadTopY);

        gc.setFill(DRIVEWAY_EDGE_COLOR);
        gc.fillRect(driveway.x1(), sidewalkTopY, 3, roadTopY - sidewalkTopY);
        gc.fillRect(driveway.x2() - 3, sidewalkTopY, 3, roadTopY - sidewalkTopY);
    }

    private void drawCurbAndRoad(GraphicsContext gc) {
        gc.setFill(CURB_COLOR);
        gc.fillRect(0, roadTopY, width, CURB_HEIGHT);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.6);
        gc.strokeLine(0, roadTopY, width, roadTopY);
        gc.strokeLine(0, roadTopY + CURB_HEIGHT, width, roadTopY + CURB_HEIGHT);

        gc.setFill(ROAD_COLOR);
        gc.fillRect(0, roadTopY + CURB_HEIGHT, width, height - (roadTopY + CURB_HEIGHT));

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(ROAD_MARK_WIDTH);
        double markY = roadTopY + ROAD_MARK_Y_OFFSET;
        for (double dx = ROAD_MARK_START; dx < width; dx += ROAD_MARK_STEP) {
            gc.strokeLine(dx, markY, dx + ROAD_MARK_LENGTH, markY);
        }
    }

    private void drawBushes(GraphicsContext gc) {
        for (double[] bush : BUSH_POSITIONS) {
            drawBush(gc, bush[0], sidewalkTopY + bush[1]);
        }
    }

    private void drawBush(GraphicsContext gc, double cx, double cy) {
        gc.setFill(BUSH_FILL_COLOR);
        gc.beginPath();
        bushPath(gc, cx, cy);
        gc.fill();

        gc.setStroke(BUSH_STROKE_COLOR);
        gc.setLineWidth(1.2);
        gc.beginPath();
        bushPath(gc, cx, cy);
        gc.stroke();
    }

    private void bushPath(GraphicsContext gc, double cx, double cy) {
        gc.moveTo(cx - 12, cy + 5);
        gc.bezierCurveTo(cx - 10, cy - 6, cx - 2, cy - 8, cx, cy - 3);
        gc.bezierCurveTo(cx + 2, cy - 8, cx + 10, cy - 6, cx + 12, cy + 5);
        gc.closePath();
    }

    private void drawStreetFurniture(GraphicsContext gc) {
        drawBench(gc, BENCH_X, sidewalkTopY + BENCH_TOP_OFFSET);
        for (double urnX : URN_XS) {
            drawUrn(gc, urnX, sidewalkTopY + URN_BASE_OFFSET);
        }
    }

    private void drawBench(GraphicsContext gc, double x, double y) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeLine(x + 3, y + 15, x + 5, y + 2);
        gc.strokeLine(x + BENCH_WIDTH - 3, y + 15, x + BENCH_WIDTH - 5, y + 2);
        gc.strokeLine(x + 4, y + 13, x + BENCH_WIDTH - 4, y + 13);

        gc.setFill(BENCH_COLOR);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.2);
        gc.fillRect(x + 2, y, BENCH_WIDTH - 4, 3.5);
        gc.strokeRect(x + 2, y, BENCH_WIDTH - 4, 3.5);
        gc.fillRect(x, y + 6, BENCH_WIDTH, 3.5);
        gc.strokeRect(x, y + 6, BENCH_WIDTH, 3.5);
    }

    private void drawUrn(GraphicsContext gc, double cx, double baseY) {
        double bodyWidth = 11.0;
        double bodyHeight = 11.5;
        double x = cx - bodyWidth / 2.0;
        double bodyTop = baseY - bodyHeight;

        gc.setFill(URN_BODY_COLOR);
        gc.fillRoundRect(x, bodyTop, bodyWidth, bodyHeight, 2.5, 2.5);

        double lidWidth = bodyWidth + 1.6;
        double lidHeight = 9.0;
        double lidY = bodyTop - 4.0;
        gc.setFill(URN_LID_COLOR);
        gc.fillArc(x - 0.8, lidY, lidWidth, lidHeight, 0, 180, ArcType.CHORD);

        gc.setFill(Color.BLACK);
        gc.fillRect(x + 1.8, bodyTop + 1.2, bodyWidth - 3.6, 1.8);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.9);
        gc.strokeRoundRect(x, bodyTop, bodyWidth, bodyHeight, 2.5, 2.5);
        gc.strokeArc(x - 0.8, lidY, lidWidth, lidHeight, 0, 180, ArcType.CHORD);

        gc.setFill(URN_BASE_COLOR);
        gc.fillRect(x - 0.5, baseY - 1.2, bodyWidth + 1.0, 1.2);
    }
}
