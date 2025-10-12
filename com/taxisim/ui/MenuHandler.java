package com.taxisim.ui;

import com.taxisim.auth.*;
import com.taxisim.model.ITaxiDaoImpl;
import com.taxisim.service.ITaxiDaoProxy;
import com.taxisim.model.Booking;
import com.taxisim.model.Taxi;
import com.taxisim.model.BookingStatus;
import com.taxisim.model.TaxiStatus;
import com.taxisim.service.BookingFacade;
import com.taxisim.util.Logger;
import com.taxisim.util.LocationUtil;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class MenuHandler {
    private final Scanner sc = new Scanner(System.in);
    private final Logger log = Logger.getInstance();
    private final UserFactory userFactory;
    private final BookingFacade bookingFacade;


    public MenuHandler(UserFactory userFactory, BookingFacade bookingFacade) {
        this.userFactory = userFactory;
        this.bookingFacade = bookingFacade;
    }

    public void start() {
        while (true) {
            System.out.println("1) Login 2) Exit");
            String cmd = sc.nextLine().trim();
            if ("2".equals(cmd)) break;
            if ("1".equals(cmd)) {
                System.out.print("username: "); String u = sc.nextLine().trim();
                System.out.print("password: "); String p = sc.nextLine().trim();
                try {
                    User user = userFactory.login(u, p);
                    routeByRole(user);
                } catch (Exception ex) {
                    log.warn("Login failed: " + ex.getMessage());
                    System.out.println("Login failed: " + ex.getMessage());
                }
            }
        }
    }

    private void routeByRole(User user) throws Exception {
        switch (user.role()) {
            case "ADMIN": adminMenu((Admin) user); break;
            case "DRIVER": driverMenu((Driver) user); break;
            default: riderMenu((Rider) user); break;
        }
    }

    private void adminMenu(Admin admin) throws Exception {
        ITaxiDaoImpl impl = new ITaxiDaoImpl();
        ITaxiDaoProxy proxy = new ITaxiDaoProxy(impl);

        while (true) {
            System.out.println("\nADMIN MENU: 1:Register Taxi 2:List Taxis 3:List Available 4:View All Bookings " +
                    "5:Get Booking by ID 6:Get Taxi by ID 7:Logout");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1": registerTaxi(proxy); break;
                case "2": listTaxis(proxy); break;
                case "3": listAvailable(proxy); break;
                case "4": listAllBookingsFromDB(); break;
                case "5": getBookingByIdFromDB(); break;
                case "6": getTaxiByIdFromDB(proxy); break;
                default: return;
            }
        }
    }

    private void driverMenu(Driver driver) throws Exception {
        ITaxiDaoImpl impl = new ITaxiDaoImpl();
        ITaxiDaoProxy proxy = new ITaxiDaoProxy(impl);

        while (true) {
            System.out.println("\nDRIVER MENU: 1:View My Taxi 2:View My Bookings 3:Logout");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1": viewDriverTaxi(driver, proxy); break;
                case "2": viewDriverBookingsFromDB(driver, proxy); break;
                default: return;
            }
        }
    }

    private void riderMenu(Rider rider) throws Exception {
        while (true) {
            System.out.println("\nRIDER MENU: 1:Create Booking 2:View My Bookings 3:Logout");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1": createBookingFlow(rider); break;
                case "2": viewRiderBookingsFromDB(rider); break;
                default: return;
            }
        }
    }

    private void registerTaxi(ITaxiDaoProxy proxy) throws Exception {
        System.out.print("Driver Name: "); String driver = sc.nextLine();
        System.out.print("Start Location: "); String locStr = sc.nextLine().trim();
        int[] coords = LocationUtil.toCoordinates(locStr);

        String taxiId = "TAXI-" + UUID.randomUUID().toString().substring(0,8);
        Taxi t = new Taxi.Builder(taxiId, driver)
                .location(coords[0], coords[1])
                .status(TaxiStatus.AVAILABLE)
                .earnings(0.0)
                .build();

        proxy.save(t);  // Save to DB
        System.out.println("Taxi registered: " + t);
    }

    private void listTaxis(ITaxiDaoProxy proxy) throws Exception {
        List<Taxi> list = proxy.findAll(); // Fetch from DB
        System.out.println("Taxis:");
        list.forEach(System.out::println);
    }

    private void listAvailable(ITaxiDaoProxy proxy) throws Exception {
        List<Taxi> list = proxy.findAvailable(); // Fetch from DB
        System.out.println("Available taxis:");
        list.forEach(System.out::println);
    }

    private void viewDriverTaxi(Driver driver, ITaxiDaoProxy proxy) throws Exception {
        List<Taxi> taxis = proxy.findAll(); // Fetch from DB
        taxis.stream()
                .filter(t -> t.getDriverName().equals(driver.getUsername()))
                .findFirst()
                .ifPresentOrElse(
                        t -> System.out.println("Your taxi: " + t),
                        () -> System.out.println("No taxi found for you.")
                );
    }

    private void createBookingFlow(Rider rider) throws Exception {
        System.out.print("Pickup location: "); String pickupStr = sc.nextLine().trim();
        System.out.print("Drop location: "); String dropStr = sc.nextLine().trim();

        int[] pcoords = LocationUtil.toCoordinates(pickupStr);
        int[] dcoords = LocationUtil.toCoordinates(dropStr);

        String bookingCode = "B" + UUID.randomUUID().toString().substring(0,8);

        Booking booking = new Booking.Builder(bookingCode, rider.getUsername())
                .pickup(pcoords[0], pcoords[1])
                .drop(dcoords[0], dcoords[1])
                .when(LocalDateTime.now())
                .status(BookingStatus.PENDING)
                .build();

        try {
            bookingFacade.bookNow(booking); // DB-based booking
            System.out.println("Booking accepted: " + booking.getBookingId() +
                    " assigned to taxi " + booking.getAssignedTaxiId());
        } catch (com.taxisim.exceptions.NoTaxiAvailableException nea) {
            nea.handle();
        } catch (Exception ex) {
            log.warn("Booking failed: " + ex.getMessage());
            System.out.println("Booking failed: " + ex.getMessage());
        }
    }


    private void listAllBookingsFromDB() {
        List<Booking> bookings = bookingFacade.getAllBookings(); // fetch from DB
        if (bookings.isEmpty()) System.out.println("No bookings found.");
        else {
            System.out.println("All bookings:");
            bookings.forEach(System.out::println);
        }
    }

    private void getBookingByIdFromDB() {
        System.out.print("Enter Booking ID: "); String id = sc.nextLine().trim();
        Booking b = bookingFacade.getBookingById(id); // DB call
        if (b != null) System.out.println("Booking details: " + b);
        else System.out.println("Booking not found.");
    }

    private void getTaxiByIdFromDB(ITaxiDaoProxy proxy) throws Exception {
        System.out.print("Enter Taxi ID: "); String id = sc.nextLine().trim();
        Optional<Taxi> t = proxy.findById(id); // DB call via proxy
        if (t != null) System.out.println("Taxi details: " + t);
        else System.out.println("Taxi not found.");
    }

    private void viewRiderBookingsFromDB(Rider rider) {
        List<Booking> bookings = bookingFacade.getAllBookings().stream()
                .filter(b -> b.getRiderName().equals(rider.getUsername()))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) System.out.println("You have no bookings.");
        else {
            System.out.println("Your bookings:");
            bookings.forEach(System.out::println);
        }
    }

    private void viewDriverBookingsFromDB(Driver driver, ITaxiDaoProxy proxy) throws Exception {
        List<Taxi> taxis = proxy.findAll(); // all taxis from DB
        List<String> driverTaxiIds = taxis.stream()
                .filter(t -> t.getDriverName().equals(driver.getUsername()))
                .map(Taxi::getTaxiId)
                .toList();

        List<Booking> bookings = bookingFacade.getAllBookings().stream()
                .filter(b -> driverTaxiIds.contains(b.getAssignedTaxiId()))
                .collect(Collectors.toList());

        if (bookings.isEmpty()) System.out.println("You have no bookings.");
        else {
            System.out.println("Your bookings:");
            bookings.forEach(System.out::println);
        }
    }
}
