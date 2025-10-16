package com.taxisim.Strategy.CalFare;
import com.taxisim.model.Booking;
import com.taxisim.geo.LocationUtil;

public class SurgeFare extends FareStrategy {
    private static int surgeValue;
    public SurgeFare(int base,int surgeValue)
    {
        this.base=base;
        this.surgeValue=surgeValue;
    }
    @Override
    public int calFare(Booking b) {
        return (int)LocationUtil.distance(b.getPickupX(),b.getPickupY(),b.getDropX(),b.getDropY())*base*surgeValue;

    }
}
