package ru.vsu.cs.kg;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.vsu.cs.kg.model.CityscapeModel;

import javax.imageio.ImageIO;
import java.io.File;

public class RenderSnapshotTest extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws Exception {
        double w = CityscapeModel.BASE_WIDTH;
        double h = CityscapeModel.BASE_HEIGHT;
        Canvas canvas = new Canvas(w, h);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        CityscapeModel model = new CityscapeModel();
        model.draw(gc, 2.0);

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, w, h);
        stage.setScene(scene);

        WritableImage image = canvas.snapshot(null, null);
        File outputFile = new File("preview_snapshot.png");
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
        System.out.println("SNAPSHOT_SAVED: " + outputFile.getAbsolutePath());

        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
