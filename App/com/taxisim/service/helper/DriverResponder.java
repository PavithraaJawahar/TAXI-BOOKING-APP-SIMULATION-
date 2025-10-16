package com.taxisim.service.helper;

import com.taxisim.model.Booking;
import com.taxisim.model.Taxi;
import com.taxisim.logging.Logger;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class DriverResponder implements Runnable {
    private final Taxi taxi;
    private final Booking booking;
    private final BookingRequestCoordinator coordinator;
    private final CountDownLatch latch;
    private final Logger log = Logger.getInstance();
    private final Random random = new Random();

    public DriverResponder(Taxi taxi, Booking booking, BookingRequestCoordinator coordinator, CountDownLatch latch) {
        this.taxi = taxi;
        this.booking = booking;
        this.coordinator = coordinator;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            // simulate driver response delay (0.3 - 2s)
            Thread.sleep(300 + random.nextInt(1700));

            boolean willAccept = random.nextDouble() < 0.8; // 80% chance to accept
            if (willAccept && coordinator.tryAccept(taxi)) {
                log.info("Taxi " + taxi.getTaxiId() + " accepted booking " + booking.getBookingId());
            } else {
                log.info("Taxi " + taxi.getTaxiId() + " declined or missed booking " + booking.getBookingId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            latch.countDown(); // ✅ always decrement latch
        }
    }
}
