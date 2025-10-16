package com.taxisim.Strategy.ChooseTaxi;

import com.taxisim.model.Booking;
import com.taxisim.model.Taxi;
import java.util.List;

public class chooseTaxis {
    private final chooseTaxisStrategy strategy;

    public chooseTaxis(chooseTaxisStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Taxi> getNotifiableTaxis(List<Taxi> availableTaxis, Booking b) {
        return strategy.choose(availableTaxis,b);
    }
}

