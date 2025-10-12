package com.taxisim.model;

import java.util.List;
import java.util.Optional;

public interface ITaxiDao {
    Taxi save(Taxi t) throws Exception;
    Optional<Taxi> findById(String taxiId) throws Exception;
    List<Taxi> findAll() throws Exception;
    List<Taxi> findAvailable() throws Exception;
    void updateStatusAndLocation(String taxiCode, String status, int x, int y) throws Exception;
    void addEarnings(String taxiCode, double amount) throws Exception;
}
