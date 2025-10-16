package com.taxisim.Strategy.CalFare;

import com.taxisim.model.Booking;

public abstract class FareStrategy {
    static int base;
    abstract int calFare(Booking b);
}
