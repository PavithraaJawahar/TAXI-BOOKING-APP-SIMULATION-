package com.taxisim.Strategy.ChooseTaxi;

import com.taxisim.model.Booking;
import com.taxisim.model.Taxi;
import com.taxisim.geo.LocationUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PremiumChoice extends chooseTaxisStrategy {
    public PremiumChoice(int count)
    {
        this.count=count;
    }
    //first filter drivers by their rating and then by nearest - can wait slightly longer
    @Override
    public List<Taxi> choose(List<Taxi> availableTaxis, Booking b) {
        List<Taxi> FinalList=new ArrayList<>();
        return FinalList=availableTaxis.stream().sorted(Comparator.comparingDouble(Taxi::getRating).reversed().thenComparing(t-> LocationUtil.distance(t.getX(),t.getY(),b.getPickupX(),b.getPickupY()))).limit(count).collect(Collectors.toList());
    }
}
