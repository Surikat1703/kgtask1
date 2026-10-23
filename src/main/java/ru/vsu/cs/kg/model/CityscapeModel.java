package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Модель сцены ночного микрорайона хрущёвок.
 * Полностью повторяет композицию эскиза концепта (1024 x 484):
 * - Небо со звёздами и Луной
 * - Задний план: дома в проёмах
 * - Передний план: ряд одинаковых серых хрущёвок (6 этажей) со случайными окнами 3-х форм и 3-х цветов
 * - Тротуарная плитка, проезды во дворы, бордюр и дорога с разметкой
 * - Островки насыпей/газонов, зелёная лавочка, урны
 * - Фонарные столбы с мерцанием
 * - Проезжающий синий автомобиль
 */
public class CityscapeModel implements Drawable {
    public static final double WIDTH = 1024.0;
    public static final double HEIGHT = 484.0;

    private final List<Drawable> layers = new ArrayList<>();

    public CityscapeModel() {
        regenerate();
    }

    public void regenerate() {
        layers.clear();
        Random rng = new Random();

        // 1. Небо (фиксированная Луна с кратерами, случайные мерцающие звёзды)
        int starCount = 110 + rng.nextInt(50);
        layers.add(new Sky(WIDTH, HEIGHT, starCount, rng));

        // Геометрия переднего плана
        double sidewalkTopY = 442.0;
        double roadTopY = 462.0;
        double houseH = 182.0;
        double houseY = sidewalkTopY - houseH; // 260.0

        // 2. Дома вдалеке в проёмах (задний план)
        // Левый дальний пролёт (между домом 1 и домом 2)
        layers.add(new Khrushchevka(150, houseY - 5, 33, houseH + 5, 1, rng));
        // Правый дальний пролёт (между домом 3 и домом 4)
        layers.add(new Khrushchevka(858, houseY - 5, 33, houseH + 5, 1, rng));

        // 3. Передний ряд хрущёвок (1-в-1 по концепту):
        // Дом 1 (крайний слева): x=3, w=145, 7 стояков окон
        layers.add(new Khrushchevka(3, houseY, 145, houseH, 7, rng));

        // Дом 2 (слева от центра): x=184, w=170, 8 стояков окон
        layers.add(new Khrushchevka(184, houseY, 170, houseH, 8, rng));

        // Дом 3 (центральный): x=357, w=319, 16 стояков окон
        layers.add(new Khrushchevka(357, houseY, 319, houseH, 16, rng));

        // Дом 4 (справа от центра): x=679, w=176, 9 стояков окон
        layers.add(new Khrushchevka(679, houseY, 176, houseH, 9, rng));

        // Дом 5 (крайний справа): x=893, w=128, 6 стояков окон
        layers.add(new Khrushchevka(893, houseY, 128, houseH, 6, rng));

        // 4. Окружение: тротуар, дорога, проезды, лавочка, урны, земляные насыпи
        layers.add(new ForegroundEnvironment(WIDTH, HEIGHT, sidewalkTopY, roadTopY));

        // 5. Фонари (в точных координатах концепта)
        double[] lanternXs = {26, 290, 464, 556, 726};
        for (double lx : lanternXs) {
            layers.add(new Lantern(lx, sidewalkTopY + 22, 54.0, rng));
        }

        // 6. Проезжающий синий седан (раз в 7 секунд, движение 4.5 секунды)
        layers.add(new Car(WIDTH, roadTopY, 7.0, 4.5));
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        for (Drawable d : layers) {
            d.draw(gc, time);
        }
    }
}
