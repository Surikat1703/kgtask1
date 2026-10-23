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
 * Главное окно JavaFX приложения.
 * Разрешение окна в точности соответствует концепту: 1024 x 484.
 */
public class MainApp extends Application {
    private CityscapeModel cityscapeModel;
    private long startNanoTime;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(CityscapeModel.WIDTH, CityscapeModel.HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        cityscapeModel = new CityscapeModel();
        startNanoTime = System.nanoTime();

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                double elapsedTime = (currentNanoTime - startNanoTime) / 1_000_000_000.0;
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                cityscapeModel.draw(gc, elapsedTime);
            }
        };
        animationTimer.start();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, CityscapeModel.WIDTH, CityscapeModel.HEIGHT);

        primaryStage.setTitle("Компьютерная графика - Хрущёвки (Задача 1)");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
