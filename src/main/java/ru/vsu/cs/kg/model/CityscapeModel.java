package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Главная модель сцены:
 * - Небо, Луна и звёзды
 * - Задний план: ПОЛНОЦЕННЫЕ широкие хрущёвки во дворах (шириной 120-140 px, нормальные 5-этажные дома)
 * - Передний план: 5 домов хрущёвок переднего ряда
 * - Фонарные столбы: ТОЛЬКО мерцающая лампа под плафоном, НИКАКИХ пятен на земле
 * - Проезды во дворы, тротуар, дорога, скамейка, урны и автомобиль
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

        // 2. Полноразмерные полноценные хрущёвки заднего плана во дворах (широкие 5-этажки на 5 стояков окон)
        // Левый двор: дом шириной 130 px (полноценный фасад)
        Khrushchevka bgLeft = new Khrushchevka(105, houseY - 14, 130, houseH + 14, 5, true, rng);
        // Правый двор: дом шириной 130 px (полноценный фасад)
        Khrushchevka bgRight = new Khrushchevka(815, houseY - 14, 130, houseH + 14, 5, true, rng);
        backgroundLayers.add(bgLeft);
        backgroundLayers.add(bgRight);

        // 3. Пять домов переднего ряда
        Khrushchevka h1 = new Khrushchevka(3, houseY, 146, houseH, 5, false, rng);
        Khrushchevka h2 = new Khrushchevka(184, houseY, 170, houseH, 6, false, rng);
        Khrushchevka h3 = new Khrushchevka(357, houseY, 319, houseH, 11, false, rng);
        Khrushchevka h4 = new Khrushchevka(679, houseY, 176, houseH, 6, false, rng);
        Khrushchevka h5 = new Khrushchevka(891, houseY, 130, houseH, 4, false, rng);

        frontHouses.add(h1);
        frontHouses.add(h2);
        frontHouses.add(h3);
        frontHouses.add(h4);
        frontHouses.add(h5);

        // 4. Окружение переднего плана (тротуар, дорога, заезды во дворы, лавочка, урны)
        foregroundLayers.add(new ForegroundEnvironment(BASE_WIDTH, BASE_HEIGHT, sidewalkTopY, roadTopY));

        // 5. Фонари (строго чёрный столб и мерцающая лампа под козырьком)
        double[] lanternXs = {26, 290, 464, 556, 726};
        for (double lx : lanternXs) {
            foregroundLayers.add(new Lantern(lx, sidewalkTopY + 20, 54.0, rng));
        }

        // 6. Проезжающий синий автомобиль
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
