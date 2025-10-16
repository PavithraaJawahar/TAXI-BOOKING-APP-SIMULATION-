package com.taxisim.dao.Taxi;

import com.taxisim.model.Taxi;

import java.util.List;

public interface ITaxiDao {
    void save(Taxi taxi) throws Exception;
    Taxi findById(String taxiCode) throws Exception;
    List<Taxi> findAll() throws Exception;
    List<Taxi> findAvailable() throws Exception;
    void updateStatusAndLocation(String taxiCode, String status, int x, int y) throws Exception;
    void addEarnings(String taxiCode, double amount) throws Exception;
}
