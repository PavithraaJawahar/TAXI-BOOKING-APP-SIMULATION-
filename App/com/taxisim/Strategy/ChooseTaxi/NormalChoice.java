package com.taxisim.Strategy.ChooseTaxi;

import com.taxisim.model.Booking;
import com.taxisim.model.Taxi;
import com.taxisim.geo.LocationUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NormalChoice extends chooseTaxisStrategy {

    public NormalChoice(int count)
    {
        this.count=count;
    }
    //In normal situations - its easy to get a driver so notify only 5 nearest drivers
    @Override
    public List<Taxi> choose(List<Taxi> availableTaxis, Booking b) {
        List<Taxi> FinalList=new ArrayList<>();
        return FinalList=availableTaxis.stream().sorted(Comparator.comparingDouble(
                t-> LocationUtil.distance(t.getX(),t.getY(),b.getPickupX(),b.getPickupY())))
                .limit(count).collect(Collectors.toList());

    }


}
