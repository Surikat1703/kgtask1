package ru.vsu.cs.kg.model;

public record Driveway(double x1, double x2) {
    public double width() {
        return x2 - x1;
    }
}
