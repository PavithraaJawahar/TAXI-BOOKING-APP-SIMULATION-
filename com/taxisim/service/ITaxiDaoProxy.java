package com.taxisim.service;

import com.taxisim.model.ITaxiDao;
import com.taxisim.model.Taxi;
import com.taxisim.util.Logger;

import java.util.List;
import java.util.Optional;

public class ITaxiDaoProxy implements ITaxiDao {
    private final ITaxiDao delegate;
    private final Logger log = Logger.getInstance();

    public ITaxiDaoProxy(ITaxiDao delegate) {
        this.delegate = delegate;
    }

    @Override
    public Taxi save(Taxi t) throws Exception {
        log.info("[DAO-Proxy] save called for " + t.getTaxiId());
        return delegate.save(t);
    }

    @Override
    public Optional<Taxi> findById(String taxiCode) throws Exception {
        log.info("[DAO-Proxy] findById " + taxiCode);
        return delegate.findById(taxiCode);
    }

    @Override
    public List<Taxi> findAll() throws Exception {
        log.info("[DAO-Proxy] findAll taxis");
        return delegate.findAll();
    }

    @Override
    public List<Taxi> findAvailable() throws Exception {
        log.info("[DAO-Proxy] findAvailable taxis");
        return delegate.findAvailable();
    }

    @Override
    public void updateStatusAndLocation(String taxiCode, String status, int x, int y) throws Exception {
        log.info("[DAO-Proxy] updateStatusAndLocation " + taxiCode);
        delegate.updateStatusAndLocation(taxiCode, status, x, y);
    }

    @Override
    public void addEarnings(String taxiCode, double amount) throws Exception {
        log.info("[DAO-Proxy] addEarnings " + taxiCode + " amount=" + amount);
        delegate.addEarnings(taxiCode, amount);
    }
}

