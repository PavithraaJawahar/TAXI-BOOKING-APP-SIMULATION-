package com.taxisim.Strategy.ChooseTaxi;

import com.taxisim.model.Taxi;
import com.taxisim.model.Booking;
import java.util.List;

public abstract class chooseTaxisStrategy {
     static int count;
     abstract List<Taxi> choose(List<Taxi> availableTaxis, Booking b);
}
