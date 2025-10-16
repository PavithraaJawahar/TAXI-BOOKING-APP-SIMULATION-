package com.taxisim.Strategy.CalFare;

import com.taxisim.model.Booking;
//dependency injection - instead of creating objs for strategy - create via dependent class
public class FareCalculator {
    private final FareStrategy strategy;

    public FareCalculator(FareStrategy strategy) {
        this.strategy = strategy;
    }

    public double getFare(Booking b) {
        return strategy.calFare(b);
    }
}

