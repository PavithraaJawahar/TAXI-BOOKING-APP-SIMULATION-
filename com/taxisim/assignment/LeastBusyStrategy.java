package com.taxisim.assignment;

import com.taxisim.model.Booking;
import com.taxisim.model.Taxi;
import com.taxisim.util.LocationUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LeastBusyStrategy implements AssignmentStrategy {

    private static final int count = 3;

    @Override
    public Optional<Taxi> chooseTaxi(List<Taxi> availableTaxis, Booking booking) {
        if (availableTaxis == null || availableTaxis.isEmpty()) return Optional.empty();


        List<Taxi> nearest = availableTaxis.stream()
                .sorted(Comparator.comparingDouble(t ->
                        LocationUtil.distance(t.getX(), t.getY(), booking.getPickupX(), booking.getPickupY())))
                .limit(count)
                .collect(Collectors.toList());


        return nearest.stream()
                .min(Comparator.comparingDouble(Taxi::getEarnings));
    }
}

