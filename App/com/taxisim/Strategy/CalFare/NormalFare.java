package com.taxisim.Strategy.CalFare;

import com.taxisim.model.Booking;
import com.taxisim.geo.LocationUtil;



public class NormalFare extends FareStrategy {

   public NormalFare(int base)
   {
       this.base=base;
   }
    @Override
    public int calFare(Booking b) {
       return (int)LocationUtil.distance(b.getPickupX(),b.getPickupY(),b.getDropX(),b.getDropY())*base;

    }
}
