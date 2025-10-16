package com.taxisim.service;

import com.taxisim.dao.Booking.IBookingDao;
import com.taxisim.dao.Booking.IBookingDaoImpl;
import com.taxisim.dao.LogBooking.BookingLogDao;
import com.taxisim.dao.LogBooking.BookingLogDaoImpl;
import com.taxisim.dao.Taxi.ITaxiDao;
import com.taxisim.dao.Taxi.ITaxiDaoImpl;
import com.taxisim.dao.Taxi.ITaxiDaoProxy;
import com.taxisim.exceptions.NoTaxiAvailableException;
import com.taxisim.model.*;
import com.taxisim.pool.CustomThreadPool;
import com.taxisim.pool.WTFactory;
import com.taxisim.Strategy.CalFare.*;
import com.taxisim.Strategy.ChooseTaxi.*;
import com.taxisim.service.helper.BookingRequestCoordinator;
import com.taxisim.service.helper.DriverResponder;
import com.taxisim.logging.Logger;
import com.taxisim.logging.LoggedAction;

import java.time.LocalDateTime;
import java.util.List;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BookingService {

    private final Logger log = Logger.getInstance();
    private final ITaxiDao taxiDao;
    private final IBookingDao bookingDao;
    private final BookingLogDao bookingLogDao;
    private final chooseTaxis chooser;
    private final FareCalculator fareCalculator;
    private final CustomThreadPool threadPool;
    private static final int BASE_FARE = 50;
    private static final int NOTIFIABLE= 5;
    //default behavior - normalchoice, normalfare and poolsize of 6
    public BookingService() {
        this(new NormalChoice(NOTIFIABLE),new NormalFare(BASE_FARE),  6);
    }

    public BookingService(chooseTaxisStrategy strategy,FareStrategy farestat, int poolSize) {
        ITaxiDao impl = new ITaxiDaoImpl();
        this.taxiDao = new ITaxiDaoProxy(impl);
        this.bookingDao = new IBookingDaoImpl();
        this.chooser = new chooseTaxis(strategy);
        this.bookingLogDao=new BookingLogDaoImpl();
        this.fareCalculator = new FareCalculator(farestat);
        this.threadPool = new CustomThreadPool(poolSize, new WTFactory("trip-worker"));
    }

    @LoggedAction("bookNow")
    public void bookNow(Booking booking) throws Exception {

        List<Taxi> available = taxiDao.findAvailable();
        List<Taxi> notifiable = chooser.getNotifiableTaxis(available, booking);

        if (notifiable.isEmpty()) {
            throw new NoTaxiAvailableException("No taxis available for booking " + booking.getBookingId());
        }

        log.info("Booking " + booking.getBookingId() + " notifying " + notifiable.size() + " drivers");


        BookingRequestCoordinator coordinator = new BookingRequestCoordinator(booking);


        CountDownLatch latch = new CountDownLatch(notifiable.size());
        for (Taxi taxi : notifiable) {
            threadPool.submit(new DriverResponder(taxi, booking, coordinator, latch));
        }


        boolean accepted = latch.await(3, TimeUnit.SECONDS);

        if (!accepted || coordinator.getAcceptedTaxi() == null) {
            log.warn("No driver accepted booking " + booking.getBookingId());
            booking.setStatus(BookingStatus.FAILED);
            bookingDao.updateStatus(booking.getBookingId(),BookingStatus.FAILED);
            return;
        }


        Taxi assigned = coordinator.getAcceptedTaxi();
        log.info("Booking " + booking.getBookingId() + " accepted by Taxi " + assigned.getTaxiId());

        booking.setAssignedTaxiId(assigned.getTaxiId());
        booking.setStatus(BookingStatus.ONGOING);
        bookingDao.updateStatus(booking.getBookingId(),BookingStatus.ONGOING);

        taxiDao.updateStatusAndLocation(
                assigned.getTaxiId(),
                TaxiStatus.ON_TRIP.name(),
                assigned.getX(),
                assigned.getY()
        );

        // Trip simulation
        threadPool.submit(() -> simulateTrip(booking, assigned));
    }

    private void simulateTrip(Booking booking, Taxi taxi) {
        try {
            log.info("Trip started for booking " + booking.getBookingId() + " | Taxi=" + taxi.getTaxiId());

            int toPickup = Math.abs(taxi.getX() - booking.getPickupX()) +
                    Math.abs(taxi.getY() - booking.getPickupY());
            int toDrop = Math.abs(booking.getPickupX() - booking.getDropX()) +
                    Math.abs(booking.getPickupY() - booking.getDropY());

            int totalDistance = toPickup + toDrop;
            long tripTimeMs = Math.max(500, totalDistance * 500L);
            Thread.sleep(tripTimeMs);

            double fare = fareCalculator.getFare(booking);
            taxiDao.addEarnings(taxi.getTaxiId(), fare);

            taxiDao.updateStatusAndLocation(
                    taxi.getTaxiId(),
                    TaxiStatus.AVAILABLE.name(),
                    booking.getDropX(),
                    booking.getDropY()
            );

            booking.setStatus(BookingStatus.COMPLETED);
            bookingDao.updateStatus(booking.getBookingId(),BookingStatus.COMPLETED);
            bookingDao.save(booking);
            bookingLogDao.logStatusChange(booking,BookingStatus.COMPLETED);

            log.info("Trip completed: " + booking.getBookingId()
                    + " | Taxi=" + taxi.getTaxiId()
                    + " | Fare=" + fare);
        } catch (Exception ex) {
            log.error("Trip simulation failed for " + booking.getBookingId() + ": " + ex.getMessage());
            booking.setStatus(BookingStatus.FAILED);
        }
    }

    public void shutdown() {
        threadPool.shutdown();
    }

}

