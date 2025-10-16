package com.taxisim.dao.LogBooking;

import com.taxisim.model.Booking;
import com.taxisim.model.BookingStatus;
import java.time.LocalDateTime;

public interface BookingLogDao {
    void logStatusChange(Booking booking,  BookingStatus newStatus) throws Exception;
}


