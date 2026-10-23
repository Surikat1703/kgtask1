package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Главная модель сцены:
 * - Небо, Луна и звёзды
 * - Задний план: полноценные широкие хрущёвки во дворах (без странных срезов, правильная перспектива)
 * - Передний план: 5 домов хрущёвок с 4 формами окон и 3 цветами
 * - Фонарные столбы с живым мерцающим светом и мягким световым конусом
 * - Дорога, аккуратные заезды во дворы, лавочка, урны, холмики и проезжающая машина
 */
public class CityscapeModel implements Drawable {
    public static final double BASE_WIDTH = 1024.0;
    public static final double BASE_HEIGHT = 484.0;

    private final List<Drawable> backgroundLayers = new ArrayList<>();
    private final List<Khrushchevka> frontHouses = new ArrayList<>();
    private final List<Drawable> foregroundLayers = new ArrayList<>();

    public CityscapeModel() {
        regenerate();
    }

    public void regenerate() {
        backgroundLayers.clear();
        frontHouses.clear();
        foregroundLayers.clear();
        Random rng = new Random();

        // 1. Небо со звёздами и Луной
        int starCount = 110 + rng.nextInt(50);
        backgroundLayers.add(new Sky(BASE_WIDTH, BASE_HEIGHT, starCount, rng));

        double sidewalkTopY = 442.0;
        double roadTopY = 462.0;
        double houseH = 182.0;
        double houseY = sidewalkTopY - houseH;

        // 2. Полноценные здания во дворах на заднем плане (широкие дома, видные в просвет между передними домами)
        // Левый двор: полноценный дом на заднем плане
        BackgroundCourtyardBuilding bgLeft = new BackgroundCourtyardBuilding(136, houseY - 14, 60, houseH + 14, 2, rng);
        // Правый двор: полноценный дом на заднем плане
        BackgroundCourtyardBuilding bgRight = new BackgroundCourtyardBuilding(846, houseY - 14, 60, houseH + 14, 2, rng);
        backgroundLayers.add(bgLeft);
        backgroundLayers.add(bgRight);

        // 3. Пять домов переднего плана (5 этажей, 4 формы окон)
        Khrushchevka h1 = new Khrushchevka(3, houseY, 146, houseH, 5, rng);
        Khrushchevka h2 = new Khrushchevka(184, houseY, 170, houseH, 6, rng);
        Khrushchevka h3 = new Khrushchevka(357, houseY, 319, houseH, 11, rng);
        Khrushchevka h4 = new Khrushchevka(679, houseY, 176, houseH, 6, rng);
        Khrushchevka h5 = new Khrushchevka(891, houseY, 130, houseH, 4, rng);

        frontHouses.add(h1);
        frontHouses.add(h2);
        frontHouses.add(h3);
        frontHouses.add(h4);
        frontHouses.add(h5);

        // 4. Окружение переднего плана (тротуар, дорога, заезды во дворы, лавочка, урны, холмики)
        foregroundLayers.add(new ForegroundEnvironment(BASE_WIDTH, BASE_HEIGHT, sidewalkTopY, roadTopY));

        // 5. Фонари (с мерцающей лампой и синхронным мягким световым пятном)
        double[] lanternXs = {26, 290, 464, 556, 726};
        for (double lx : lanternXs) {
            foregroundLayers.add(new Lantern(lx, sidewalkTopY + 20, 54.0, rng));
        }

        // 6. Проезжающий синий седан
        foregroundLayers.add(new Car(BASE_WIDTH, roadTopY, 7.0, 4.5));
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
        for (Drawable d : backgroundLayers) {
            d.draw(gc, time);
        }
        for (Khrushchevka house : frontHouses) {
            house.draw(gc, time);
        }
        for (Drawable d : foregroundLayers) {
            d.draw(gc, time);
        }
    }
}
