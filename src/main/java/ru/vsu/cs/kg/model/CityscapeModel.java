package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Модель сцены города.
 * - 5 этажей, крупные гармоничные окна
 * - Поддержка смены формы окна (клик мыши по окну или пробел)
 * - Единый источник света от фонарей (без дублирующего статичного)
 */
public class CityscapeModel implements Drawable {
    public static final double BASE_WIDTH = 1024.0;
    public static final double BASE_HEIGHT = 484.0;

    private final List<Drawable> layers = new ArrayList<>();
    private final List<Khrushchevka> houses = new ArrayList<>();

    public CityscapeModel() {
        regenerate();
    }

    public void regenerate() {
        layers.clear();
        houses.clear();
        Random rng = new Random();

        // 1. Небо со звёздами и Луной
        int starCount = 110 + rng.nextInt(50);
        layers.add(new Sky(BASE_WIDTH, BASE_HEIGHT, starCount, rng));

        double sidewalkTopY = 442.0;
        double roadTopY = 462.0;
        double houseH = 182.0;
        double houseY = sidewalkTopY - houseH;

        // 2. Дома вдалеке в проёмах
        Khrushchevka bgLeft = new Khrushchevka(153, houseY - 5, 28, houseH + 5, 1, rng);
        Khrushchevka bgRight = new Khrushchevka(860, houseY - 5, 28, houseH + 5, 1, rng);
        layers.add(bgLeft);
        layers.add(bgRight);

        // 3. Пять передних домов (5 этажей, крупные окна)
        Khrushchevka h1 = new Khrushchevka(3, houseY, 146, houseH, 5, rng);
        Khrushchevka h2 = new Khrushchevka(184, houseY, 170, houseH, 6, rng);
        Khrushchevka h3 = new Khrushchevka(357, houseY, 319, houseH, 11, rng);
        Khrushchevka h4 = new Khrushchevka(679, houseY, 176, houseH, 6, rng);
        Khrushchevka h5 = new Khrushchevka(891, houseY, 130, houseH, 4, rng);

        houses.add(h1);
        houses.add(h2);
        houses.add(h3);
        houses.add(h4);
        houses.add(h5);

        layers.addAll(houses);

        // 4. Окружение переднего плана
        layers.add(new ForegroundEnvironment(BASE_WIDTH, BASE_HEIGHT, sidewalkTopY, roadTopY));

        // 5. Фонари (с единственным источником света)
        double[] lanternXs = {26, 290, 464, 556, 726};
        for (double lx : lanternXs) {
            layers.add(new Lantern(lx, sidewalkTopY + 20, 54.0, rng));
        }

        // 6. Автомобиль
        layers.add(new Car(BASE_WIDTH, roadTopY, 7.0, 4.5));
    }

    /**
     * Обработка клика мыши: смена формы окна, в которое кликнули
     */
    public boolean handleMouseClick(double x, double y) {
        for (Khrushchevka house : houses) {
            if (house.handleClick(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Сменить форму всех окон на сцене
     */
    public void cycleAllWindows() {
        for (Khrushchevka house : houses) {
            house.cycleAllWindows();
        }
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        for (Drawable d : layers) {
            d.draw(gc, time);
        }
    }
}
