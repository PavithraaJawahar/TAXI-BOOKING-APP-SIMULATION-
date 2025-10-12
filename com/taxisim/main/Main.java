package com.taxisim.main;

import com.taxisim.assignment.LeastBusyStrategy;
import com.taxisim.service.BookingFacade;
import com.taxisim.auth.UserFactory;
import com.taxisim.util.Logger;
import com.taxisim.ui.MenuHandler;

public class Main {
    public static void main(String[] args) {
        Logger log = Logger.getInstance();
        log.info("Application starting");
        int poolSize=Runtime.getRuntime().availableProcessors()+1;
        BookingFacade bookingFacade = new BookingFacade(new LeastBusyStrategy(), poolSize);
        UserFactory userFactory = new UserFactory();
        MenuHandler menu = new MenuHandler(userFactory, bookingFacade);
        menu.start();
        bookingFacade.shutdown();
        log.info("Application exiting");
    }
}
