package com.taxisim.util;

public final class LocationUtil {
    //points coordinate values can be from 0 to 99
    private static final int size = 100;

    private LocationUtil() { }

    public static int[] toCoordinates(String location) {
        if (location == null) location = "";
        location = location.trim().toLowerCase();

        int sum = 0;
        for (char c : location.toCharArray()) {
            sum += c;
        }

        int x = sum % size;
        int y = (sum / size) % size;

        return new int[] { x, y };
    }


    public static double distance(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

}



