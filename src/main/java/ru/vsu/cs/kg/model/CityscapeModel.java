package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CityscapeModel implements Drawable {
    public static final double BASE_WIDTH = 1024.0;
    public static final double BASE_HEIGHT = 484.0;

    private static final double SIDEWALK_TOP_Y = 442.0;
    private static final double ROAD_TOP_Y = 462.0;
    private static final double HOUSE_HEIGHT = 182.0;

    private static final int STAR_COUNT_BASE = 110;
    private static final int STAR_COUNT_EXTRA = 50;

    private static final double REAR_HOUSE_WIDTH = 130.0;
    private static final double REAR_HOUSE_EXTRA_HEIGHT = 14.0;
    private static final double[] REAR_HOUSE_XS = {105, 815};

    private static final double LANTERN_HEIGHT = 54.0;
    private static final double LANTERN_GROUND_OFFSET = 20.0;
    private static final double[] LANTERN_XS = {26, 290, 464, 556, 726};

    private static final double CAR_PERIOD_SECONDS = 7.0;
    private static final double CAR_DRIVE_SECONDS = 4.5;

    private static final double STREET_SIGN_X = 140.0;
    private static final String STREET_NAME = "МИРА";

    private record FrontHouse(double x, double width, int columns) {}

    private record DrivewayGap(int leftHouse, int rightHouse) {}

    private static final List<FrontHouse> FRONT_ROW = List.of(
            new FrontHouse(3, 146, 5),
            new FrontHouse(184, 170, 6),
            new FrontHouse(357, 319, 11),
            new FrontHouse(679, 176, 6),
            new FrontHouse(891, 130, 4)
    );

    private static final List<DrivewayGap> DRIVEWAY_GAPS = List.of(
            new DrivewayGap(0, 1),
            new DrivewayGap(3, 4)
    );

    private static final List<Driveway> DRIVEWAYS = buildDriveways();

    private final List<Drawable> layers = new ArrayList<>();
    private final List<Khrushchevka> frontHouses = new ArrayList<>();

    public CityscapeModel() {
        regenerate();
    }

    public void regenerate() {
        layers.clear();
        frontHouses.clear();

        Random rng = new Random();
        double houseY = SIDEWALK_TOP_Y - HOUSE_HEIGHT;

        addSky(rng);
        addBackgroundHouses(houseY, rng);
        addFrontHouses(houseY, rng);
        addEnvironment();
        addStreetSign();
        addLanterns(rng);
        addCar();
    }

    private void addSky(Random rng) {
        int starCount = STAR_COUNT_BASE + rng.nextInt(STAR_COUNT_EXTRA);
        layers.add(new Sky(BASE_WIDTH, BASE_HEIGHT, starCount, rng));
    }

    private void addBackgroundHouses(double houseY, Random rng) {
        for (double x : REAR_HOUSE_XS) {
            layers.add(new Khrushchevka(
                    x,
                    houseY - REAR_HOUSE_EXTRA_HEIGHT,
                    REAR_HOUSE_WIDTH,
                    HOUSE_HEIGHT + REAR_HOUSE_EXTRA_HEIGHT,
                    5,
                    true,
                    rng));
        }
    }

    private void addFrontHouses(double houseY, Random rng) {
        for (FrontHouse spec : FRONT_ROW) {
            Khrushchevka house = new Khrushchevka(
                    spec.x(), houseY, spec.width(), HOUSE_HEIGHT, spec.columns(), false, rng);
            frontHouses.add(house);
            layers.add(house);
        }
    }

    private void addEnvironment() {
        layers.add(new ForegroundEnvironment(
                BASE_WIDTH, BASE_HEIGHT, SIDEWALK_TOP_Y, ROAD_TOP_Y, DRIVEWAYS));
    }

    private void addStreetSign() {
        layers.add(new StreetSign(STREET_SIGN_X, SIDEWALK_TOP_Y + 19, STREET_NAME));
    }

    private void addLanterns(Random rng) {
        for (double lanternX : LANTERN_XS) {
            layers.add(new Lantern(
                    lanternX,
                    SIDEWALK_TOP_Y + LANTERN_GROUND_OFFSET,
                    LANTERN_HEIGHT,
                    rng));
        }
    }

    private void addCar() {
        layers.add(new Car(BASE_WIDTH, ROAD_TOP_Y, CAR_PERIOD_SECONDS, CAR_DRIVE_SECONDS));
    }

    private static List<Driveway> buildDriveways() {
        List<Driveway> result = new ArrayList<>(DRIVEWAY_GAPS.size());
        for (DrivewayGap gap : DRIVEWAY_GAPS) {
            FrontHouse left = FRONT_ROW.get(gap.leftHouse());
            FrontHouse right = FRONT_ROW.get(gap.rightHouse());
            result.add(new Driveway(left.x() + left.width(), right.x()));
        }
        return List.copyOf(result);
    }

    public boolean handleMouseClick(double x, double y) {
        for (Khrushchevka house : frontHouses) {
            if (house.handleClick(x, y)) {
                return true;
            }
        }
        return false;
    }

    public void cycleAllWindows() {
        for (Khrushchevka house : frontHouses) {
            house.cycleAllWindows();
        }
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        for (Drawable layer : layers) {
            layer.draw(gc, time);
        }
    }
}
