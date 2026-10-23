package ru.vsu.cs.kg.model;

import javafx.scene.canvas.GraphicsContext;

/**
 * Базовый интерфейс для всех графических элементов сцены.
 */
public interface Drawable {
    void draw(GraphicsContext gc, double elapsedTimeSeconds);
}
