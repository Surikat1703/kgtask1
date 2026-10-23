package ru.vsu.cs.kg;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import ru.vsu.cs.kg.model.CityscapeModel;

/**
 * Главное окно JavaFX приложения:
 * - Динамическое разрешение (окно можно растягивать, сворачивать, разворачивать на весь экран с сохранением пропорций)
 * - Интерактивность:
 *     - Клик ЛКМ по окну: переключает форму конкретного окна
 *     - Клавиша W или Клик ПКМ: переключает форму сразу всех окон в городе
 *     - Клавиша R: перегенерация города (новые окна, звёзды)
 */
public class MainApp extends Application {
    private CityscapeModel cityscapeModel;
    private long startNanoTime;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(CityscapeModel.BASE_WIDTH, CityscapeModel.BASE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        cityscapeModel = new CityscapeModel();
        startNanoTime = System.nanoTime();

        // Анимация
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                double elapsedTime = (currentNanoTime - startNanoTime) / 1_000_000_000.0;
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                cityscapeModel.draw(gc, elapsedTime);
            }
        };
        animationTimer.start();

        // Панель с динамическим масштабированием (Scale Transform) под любой размер окна
        Pane root = new Pane(canvas);
        Scale scale = new Scale(1.0, 1.0, 0, 0);
        canvas.getTransforms().add(scale);

        Scene scene = new Scene(root, CityscapeModel.BASE_WIDTH, CityscapeModel.BASE_HEIGHT);

        // Динамическое разрешение: масштабируем сцену при изменении размеров окна пользователем
        scene.widthProperty().addListener((obs, oldW, newW) -> {
            updateScale(scale, newW.doubleValue(), scene.getHeight(), canvas);
        });
        scene.heightProperty().addListener((obs, oldH, newH) -> {
            updateScale(scale, scene.getWidth(), newH.doubleValue(), canvas);
        });

        // Интерактив: клик мыши для смены формы окна
        scene.setOnMouseClicked(event -> {
            double virtualX = event.getX() / scale.getX();
            double virtualY = event.getY() / scale.getY();

            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                boolean hit = cityscapeModel.handleMouseClick(virtualX, virtualY);
                if (!hit) {
                    // Клик в пустоту меняет форму всех окон
                    cityscapeModel.cycleAllWindows();
                }
            } else {
                cityscapeModel.cycleAllWindows();
            }
        });

        // Горячие клавиши
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.W || event.getCode() == KeyCode.SPACE) {
                // Переключить форму всех окон
                cityscapeModel.cycleAllWindows();
            } else if (event.getCode() == KeyCode.R) {
                // Перегенерировать сцену
                cityscapeModel.regenerate();
            }
        });

        primaryStage.setTitle("Компьютерная графика - Хрущёвки (Клик по окну / 'W' - смена формы, 'R' - регенерация)");
        primaryStage.setResizable(true); // Разрешено изменение размера окна
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateScale(Scale scale, double windowW, double windowH, Canvas canvas) {
        if (windowW <= 0 || windowH <= 0) return;
        double scaleX = windowW / CityscapeModel.BASE_WIDTH;
        double scaleY = windowH / CityscapeModel.BASE_HEIGHT;
        // Масштабируем равномерно, чтобы картинка красиво заполняла окно
        scale.setX(scaleX);
        scale.setY(scaleY);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
