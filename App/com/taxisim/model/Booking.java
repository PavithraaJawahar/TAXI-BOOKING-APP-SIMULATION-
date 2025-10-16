package com.taxisim.model;

import com.taxisim.logging.Logger;
import java.time.LocalDateTime;

public class Booking {

    private static final Logger log = Logger.getInstance();

    private final String bookingId;
    private final String riderName;
    private final int pickupX, pickupY;
    private final int dropX, dropY;
    private final LocalDateTime requestedTime;
    private LocalDateTime completedTime; // made mutable
    private String assignedTaxiId;
    private BookingStatus status;

    public Booking(Builder builder) {
        this.bookingId = builder.bookingId;
        this.riderName = builder.riderName;
        this.pickupX = builder.pickupX;
        this.pickupY = builder.pickupY;
        this.dropX = builder.dropX;
        this.dropY = builder.dropY;
        this.requestedTime = builder.requestedTime;
        this.status = builder.status;
        this.completedTime = builder.completedTime; // initialized if set in builder
    }

    // Status setter with logging
    public void setStatus(BookingStatus newStatus) {
        BookingStatus old = this.status;
        this.status = newStatus;
        log.info(String.format("Booking %s: status changed from %s to %s", bookingId, old, newStatus));
    }

    // Completed time setter with logging
    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
        log.info(String.format("Booking %s: completed time set to %s", bookingId, completedTime));
    }

    // Getters
    public String getBookingId() { return bookingId; }
    public String getRiderName() { return riderName; }
    public int getPickupX() { return pickupX; }
    public int getPickupY() { return pickupY; }
    public int getDropX() { return dropX; }
    public int getDropY() { return dropY; }
    public LocalDateTime getRequestedTime() { return requestedTime; }
    public LocalDateTime getCompletedTime() { return completedTime; }
    public String getAssignedTaxiId() { return assignedTaxiId; }
    public BookingStatus getStatus() { return status; }

    public void setAssignedTaxiId(String taxiId) { this.assignedTaxiId = taxiId; }

    @Override
    public String toString() {
        return String.format(
                "Booking[%s,%s,pick=(%d,%d),drop=(%d,%d),requested=%s,completed=%s,taxi=%s,status=%s]",
                bookingId, riderName, pickupX, pickupY, dropX, dropY, requestedTime, completedTime, assignedTaxiId, status
        );
    }

    // Builder class
    public static class Builder {
        private final String bookingId;
        private final String riderName;
        private int pickupX, pickupY;
        private int dropX, dropY;
        private LocalDateTime requestedTime = LocalDateTime.now();
        private LocalDateTime completedTime;
        private BookingStatus status = BookingStatus.PENDING;

        public Builder(String bookingId, String riderName) {
            this.bookingId = bookingId;
            this.riderName = riderName;
        }

        public Builder pickup(int x, int y) { this.pickupX = x; this.pickupY = y; return this; }
        public Builder drop(int x, int y) { this.dropX = x; this.dropY = y; return this; }
        public Builder when(LocalDateTime t) { this.requestedTime = t; return this; }
        public Builder completed(LocalDateTime t) { this.completedTime = t; return this; }
        public Builder status(BookingStatus s) { this.status = s; return this; }

        public Booking build() {
            return new Booking(this);
        }
    }
}
