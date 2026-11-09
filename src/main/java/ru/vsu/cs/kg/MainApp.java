package ru.vsu.cs.kg;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import ru.vsu.cs.kg.model.CityscapeModel;

public class MainApp extends Application {
    private static final String TITLE =
            "Компьютерная графика - Хрущёвки (W/ЛКМ - смена формы всех окон, ЛКМ по окну - смена формы окна, R - регенерация, F11 - полный экран)";
    private static final String FULLSCREEN_HINT =
            "F11 или Esc — выйти из полноэкранного режима";

    private final Scale fillScale = new Scale(1.0, 1.0, 0, 0);
    private CityscapeModel cityscapeModel;
    private Canvas canvas;
    private double canvasOffsetX;
    private double canvasOffsetY;

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(CityscapeModel.BASE_WIDTH, CityscapeModel.BASE_HEIGHT);
        canvas.getTransforms().add(fillScale);

        cityscapeModel = new CityscapeModel();
        startAnimation();

        Scene scene = createScene();
        configureInput(scene, primaryStage);
        configureStage(primaryStage, scene);
    }

    private void startAnimation() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        long startNanoTime = System.nanoTime();

        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                double elapsedTime = (currentNanoTime - startNanoTime) / 1_000_000_000.0;
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                cityscapeModel.draw(gc, elapsedTime);
            }
        }.start();
    }

    private Scene createScene() {
        Pane root = new Pane(canvas);
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root, CityscapeModel.BASE_WIDTH, CityscapeModel.BASE_HEIGHT);
        scene.widthProperty().addListener((obs, oldW, newW) ->
                updateScale(newW.doubleValue(), scene.getHeight()));
        scene.heightProperty().addListener((obs, oldH, newH) ->
                updateScale(scene.getWidth(), newH.doubleValue()));
        return scene;
    }

    private void configureInput(Scene scene, Stage stage) {
        scene.setOnMouseClicked(event -> handleMouseClick(event.getButton(), event.getX(), event.getY()));

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.W) {
                cityscapeModel.cycleAllWindows();
            } else if (event.getCode() == KeyCode.R) {
                cityscapeModel.regenerate();
            } else if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
    }

    private void handleMouseClick(MouseButton button, double sceneX, double sceneY) {
        if (button != MouseButton.PRIMARY) {
            cityscapeModel.cycleAllWindows();
            return;
        }

        double scale = fillScale.getX();
        double virtualX = (sceneX - canvasOffsetX) / scale;
        double virtualY = (sceneY - canvasOffsetY) / scale;

        if (virtualX < 0 || virtualX > CityscapeModel.BASE_WIDTH
                || virtualY < 0 || virtualY > CityscapeModel.BASE_HEIGHT) {
            return;
        }

        if (!cityscapeModel.handleMouseClick(virtualX, virtualY)) {
            cityscapeModel.cycleAllWindows();
        }
    }

    private void configureStage(Stage stage, Scene scene) {
        stage.setTitle(TITLE);
        stage.setResizable(true);
        stage.setFullScreenExitHint(FULLSCREEN_HINT);
        stage.setScene(scene);
        stage.show();
    }

    private void updateScale(double windowW, double windowH) {
        if (windowW <= 0 || windowH <= 0) return;

        double scale = Math.min(windowW / CityscapeModel.BASE_WIDTH,
                windowH / CityscapeModel.BASE_HEIGHT);
        fillScale.setX(scale);
        fillScale.setY(scale);

        canvasOffsetX = (windowW - CityscapeModel.BASE_WIDTH * scale) / 2.0;
        canvasOffsetY = (windowH - CityscapeModel.BASE_HEIGHT * scale) / 2.0;
        canvas.setTranslateX(canvasOffsetX);
        canvas.setTranslateY(canvasOffsetY);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
