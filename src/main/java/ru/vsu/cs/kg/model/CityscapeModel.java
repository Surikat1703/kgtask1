package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Главный фасад сцены (CityscapeModel), объединяющий все объектные слои рисунка:
 * - Небо, звезды, луна
 * - Задний и передний ряды хрущёвок
 * - Дорожная инфраструктура и тротуары
 * - Фонарные столбы с независимым мерцанием
 * - Уличная мебель (лавочки, урны)
 * - Анимированный автомобиль
 */
public class CityscapeModel implements Drawable {
    private final double width;
    private final double height;
    private final List<Drawable> backgroundLayers = new ArrayList<>();
    private final List<Drawable> buildingLayers = new ArrayList<>();
    private final List<Drawable> foregroundLayers = new ArrayList<>();

    public CityscapeModel(double width, double height) {
        this.width = width;
        this.height = height;
        regenerate();
    }

    /**
     * Динамическая генерация изображения: при каждом вызове (или перезапуске)
     * генерируется уникальное случайное расположение звёзд, окон разной формы и цветов.
     */
    public void regenerate() {
        backgroundLayers.clear();
        buildingLayers.clear();
        foregroundLayers.clear();

        Random rng = new Random();

        // 1. Небо со случайным количеством звёзд (от 90 до 150)
        int starCount = 90 + rng.nextInt(60);
        Sky sky = new Sky(width, height, starCount, rng);
        backgroundLayers.add(sky);

        // Расчёт геометрии города
        double groundY = height * 0.76;
        double sidewalkTopY = groundY - 30;
        double houseH = height * 0.38;
        double houseY = sidewalkTopY - houseH;

        // 2. Задний план: дома вдалеке в проёмах между передними домами
        // Дом в левом проёме
        Khrushchevka bgLeft = new Khrushchevka(135, houseY - 15, 80, houseH, 5, 2, "ул. Мира, 12", rng);
        // Дом в правом проёме
        Khrushchevka bgRight = new Khrushchevka(width - 215, houseY - 15, 80, houseH, 5, 2, "ул. Ленина, 5", rng);
        buildingLayers.add(bgLeft);
        buildingLayers.add(bgRight);

        // 3. Передний план: 3 больших дома хрущевки во всю ширину как на эскизе
        double houseW = (width - 40) / 3.0; // ширина каждого из 3 фасадов
        Khrushchevka house1 = new Khrushchevka(10, houseY, houseW - 10, houseH, 5, 5, "пр. Революции, 1", rng);
        Khrushchevka house2 = new Khrushchevka(15 + houseW, houseY, houseW - 10, houseH, 5, 5, "пр. Революции, 3", rng);
        Khrushchevka house3 = new Khrushchevka(20 + 2 * houseW, houseY, houseW - 10, houseH, 5, 5, "пр. Революции, 5", rng);
        buildingLayers.add(house1);
        buildingLayers.add(house2);
        buildingLayers.add(house3);

        // 4. Дорога и тротуары
        RoadAndSidewalk roadAndSidewalk = new RoadAndSidewalk(width, height, sidewalkTopY, groundY);
        foregroundLayers.add(roadAndSidewalk);

        // 5. Уличная мебель: лавочка по центру, урны около домов и лавочки
        double benchX = width * 0.5 - 30;
        double benchY = groundY - 26;
        double[] trashPositions = {120, width * 0.38, width * 0.62, width - 120};
        StreetFurniture furniture = new StreetFurniture(benchX, benchY, trashPositions, groundY - 26);
        foregroundLayers.add(furniture);

        // 6. Фонари на переднем плане со случайной периодичностью мерцания
        double[] lanternXPositions = {
                houseW * 0.3,
                houseW * 0.85,
                houseW * 1.5,
                houseW * 2.15,
                houseW * 2.7
        };
        for (double lx : lanternXPositions) {
            Lantern lantern = new Lantern(lx, groundY - 10, 60.0, rng);
            foregroundLayers.add(lantern);
        }

        // 7. Автомобиль: проезжает раз в 8 секунд за 5 секунд через весь экран
        Car car = new Car(width, groundY + (height - groundY) * 0.55, 8.0, 5.0);
        foregroundLayers.add(car);
    }

    @Override
    public void draw(GraphicsContext gc, double time) {
        for (Drawable d : backgroundLayers) {
            d.draw(gc, time);
        }
        for (Drawable d : buildingLayers) {
            d.draw(gc, time);
        }
        for (Drawable d : foregroundLayers) {
            d.draw(gc, time);
        }
    }
}
