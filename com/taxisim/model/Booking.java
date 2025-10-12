package com.taxisim.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class Booking {
    private final String bookingId;
    private final String riderName;
    private final int pickupX, pickupY;
    private final int dropX, dropY;
    private final LocalDateTime requestedTime;
    private String assignedTaxiId;
    private BookingStatus status;

    private List<BookingObserver> observers = new ArrayList<>();

    public interface BookingObserver {
        void onStatusChange(Booking b, BookingStatus oldStatus, BookingStatus newStatus);
    }

    public Booking(Builder b) {
        this.bookingId = b.bookingId;
        this.riderName = b.riderName;
        this.pickupX = b.pickupX; this.pickupY = b.pickupY;
        this.dropX = b.dropX; this.dropY = b.dropY;
        this.requestedTime = b.requestedTime;
        this.status = b.status;
    }



    private void notifyObservers(BookingStatus oldS, BookingStatus newS) {
        if (observers == null) return;
        for (BookingObserver o : observers) {
            try { o.onStatusChange(this, oldS, newS); } catch (Exception ex) {  }
        }
    }

    public void setStatus(BookingStatus newStatus) {
        BookingStatus old = this.status;
        this.status = newStatus;
        notifyObservers(old, newStatus);
    }


    public String getBookingId() { return bookingId; }
    public String getRiderName() { return riderName; }
    public int getPickupX() { return pickupX; }
    public int getPickupY() { return pickupY; }
    public int getDropX() { return dropX; }
    public int getDropY() { return dropY; }
    public LocalDateTime getRequestedTime() { return requestedTime; }
    public String getAssignedTaxiId() { return assignedTaxiId; }
    public BookingStatus getStatus() { return status; }
    public void setAssignedTaxiCode(String c) { this.assignedTaxiId = c; }

    @Override
    public String toString() {
        return String.format("Booking[%s,%s,pick=(%d,%d),drop=(%d,%d),%s,taxi=%s,status=%s]",
                bookingId, riderName, pickupX, pickupY, dropX, dropY, requestedTime, assignedTaxiId, status);
    }

    public static class Builder {
        private final String bookingId;
        private final String riderName;
        private int pickupX, pickupY;
        private int dropX, dropY;
        private LocalDateTime requestedTime = LocalDateTime.now();
        private BookingStatus status = BookingStatus.PENDING;

        public Builder(String bookingCode, String riderName) {
            this.bookingId = bookingCode;
            this.riderName = riderName;
        }
        public Builder pickup(int x, int y) { this.pickupX = x; this.pickupY = y; return this; }
        public Builder drop(int x, int y) { this.dropX = x; this.dropY = y; return this; }
        public Builder when(LocalDateTime t) { this.requestedTime = t; return this; }
        public Builder status(BookingStatus s) { this.status = s; return this; }
        public Booking build() { return new Booking(this); }
    }
}