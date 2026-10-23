package ru.vsu.cs.kg;

/**
 * Класс-стартер без наследования от Application.
 * Позволяет напрямую запускать проект по зеленой кнопке в IntelliJ IDEA
 * без ошибки "JavaFX runtime components are missing".
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
