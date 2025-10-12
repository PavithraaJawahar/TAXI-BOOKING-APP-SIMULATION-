package com.taxisim.model;


public class Taxi {
    private final String taxiId;
    private final String driverName;
    private int x;
    private int y;
    private TaxiStatus status;
    private double earnings;

    public Taxi(Builder b) {
        this.taxiId = b.taxiId;
        this.driverName = b.driverName;
        this.x = b.x;
        this.y = b.y;
        this.status = b.status;
        this.earnings = b.earnings;
    }

    public String getTaxiId() { return taxiId; }
    public String getDriverName() { return driverName; }
    public int getX() { return x; }
    public int getY() { return y; }
    public TaxiStatus getStatus() { return status; }
    public double getEarnings() { return earnings; }


    @Override
    public String toString() {
        return String.format("Taxi[%s,%s,(%d,%d),%s,earnings=%.2f]", taxiId, driverName, x, y, status, earnings);
    }

    public static class Builder {
        private final String taxiId;
        private final String driverName;
        private int x = 0;
        private int y = 0;
        private TaxiStatus status = TaxiStatus.AVAILABLE;
        private double earnings = 0.0;

        public Builder(String taxiId, String driverName) {
            this.taxiId = taxiId;
            this.driverName = driverName;
        }
        public Builder location(int x, int y) { this.x = x; this.y = y; return this; }
        public Builder status(TaxiStatus s) { this.status = s; return this; }
        public Builder earnings(double e) { this.earnings = e; return this; }
        public Taxi build() { return new Taxi(this); }
    }
}
