package com.taxisim.assignment;

import com.taxisim.model.Booking;
import com.taxisim.model.Taxi;

import java.util.List;
import java.util.Optional;

public interface AssignmentStrategy {
    Optional<Taxi> chooseTaxi(List<Taxi> availableTaxis, Booking booking);
}

