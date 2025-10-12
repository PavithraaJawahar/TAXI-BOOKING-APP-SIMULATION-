package com.taxisim.service;

import com.taxisim.model.*;
import com.taxisim.util.LoggedAction;
import com.taxisim.assignment.AssignmentStrategy;
import com.taxisim.pool.CustomThreadPool;
import com.taxisim.pool.WTFactory;
import com.taxisim.util.Logger;
import com.taxisim.assignment.LeastBusyStrategy;
import com.taxisim.exceptions.NoTaxiAvailableException;
import com.taxisim.model.BookingLogDao;
import com.taxisim.model.BookingLogDaoImpl;

import java.util.List;
import java.util.Optional;

public class BookingFacade {
    private final Logger log = Logger.getInstance();
    private final ITaxiDao taxiDao;
    private final AssignmentStrategy strategy;
    private final CustomThreadPool pool;

    private final BookingLogDao logDao = new BookingLogDaoImpl();

    public BookingFacade() {
        ITaxiDao impl = new ITaxiDaoImpl();
        this.taxiDao = new ITaxiDaoProxy(impl);
        this.strategy = new LeastBusyStrategy();
        this.pool = new CustomThreadPool(4, new WTFactory("trip-worker"));
    }

    public BookingFacade(AssignmentStrategy s, int poolSize) {
        ITaxiDao impl = new ITaxiDaoImpl();
        this.taxiDao = new ITaxiDaoProxy(impl);
        this.strategy = s;
        this.pool = new CustomThreadPool(poolSize, new WTFactory("trip-worker"));
    }

    @LoggedAction("bookNow")
    public void bookNow(Booking booking) throws Exception {
        List<Taxi> available = taxiDao.findAvailable();
        Optional<Taxi> chosen = strategy.chooseTaxi(available, booking);
        if (!chosen.isPresent()) {
            log.warn("No taxis available for booking " + booking.getBookingId());
            throw new NoTaxiAvailableException("No taxis available");
        }

        Taxi t = chosen.get();
        booking.setAssignedTaxiCode(t.getTaxiId());
        booking.setStatus(BookingStatus.ONGOING);

        taxiDao.updateStatusAndLocation(t.getTaxiId(), TaxiStatus.ON_TRIP.name(), t.getX(), t.getY());

        pool.submit(() -> {
            try {
                log.info("Trip started for booking " + booking.getBookingId() + " taxi=" + t.getTaxiId());
                int toPickup = Math.abs(t.getX() - booking.getPickupX()) + Math.abs(t.getY() - booking.getPickupY());
                int toDrop = Math.abs(booking.getPickupX() - booking.getDropX()) + Math.abs(booking.getPickupY() - booking.getDropY());
                long ms = Math.max(500, (toPickup + toDrop) * 500L);
                Thread.sleep(ms);

                double fare = computeFare(toPickup + toDrop);
                taxiDao.addEarnings(t.getTaxiId(), fare);

                taxiDao.updateStatusAndLocation(t.getTaxiId(), TaxiStatus.AVAILABLE.name(), booking.getDropX(), booking.getDropY());
                booking.setStatus(BookingStatus.COMPLETED);

                // Log completed booking to DB
                logDao.logBooking(booking);

                log.info("Trip completed " + booking.getBookingId() + " taxi=" + t.getTaxiId() + " fare=" + fare);
            } catch (Exception ex) {
                log.error("Trip simulation error for booking " + booking.getBookingId());
            }
        });
    }

    private double computeFare(int distanceUnits) {
        return 20.0 + 5.0 * distanceUnits;
    }

    public void shutdown() {
        pool.shutdown();
    }


    public List<Booking> getAllBookings() {
        return logDao.getAllBookings();
    }


    public Booking getBookingById(String bookingId) {
        return logDao.getBooking(bookingId);
    }
}
