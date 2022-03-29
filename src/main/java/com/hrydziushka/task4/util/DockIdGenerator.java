package com.hrydziushka.task4.util;

public class DockIdGenerator {
    private static int counter = 0;

    public static String generateId() {
        return "D" + ++counter;
    }
}
