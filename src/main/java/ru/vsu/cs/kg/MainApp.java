package ru.vsu.cs.kg;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.vsu.cs.kg.model.CityscapeModel;

/**
 * Главная точка входа JavaFX приложения.
 */
public class MainApp extends Application {
    private static final double SCENE_WIDTH = 1200;
    private static final double SCENE_HEIGHT = 600;

    private CityscapeModel cityscapeModel;
    private long startNanoTime;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(SCENE_WIDTH, SCENE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        cityscapeModel = new CityscapeModel(SCENE_WIDTH, SCENE_HEIGHT);
        startNanoTime = System.nanoTime();

        // Главный цикл отрисовки и анимации JavaFX
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                double elapsedTime = (currentNanoTime - startNanoTime) / 1_000_000_000.0;
                // Очистка и отрисовка кадра
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                cityscapeModel.draw(gc, elapsedTime);
            }
        };
        animationTimer.start();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        primaryStage.setTitle("Компьютерная графика - Задача 1: Ночной панельный микрорайон");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
