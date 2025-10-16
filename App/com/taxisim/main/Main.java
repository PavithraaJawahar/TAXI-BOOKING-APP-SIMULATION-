package com.taxisim.main;


import com.taxisim.Strategy.CalFare.FareStrategy;
import com.taxisim.Strategy.CalFare.NormalFare;
import com.taxisim.Strategy.ChooseTaxi.NormalChoice;
import com.taxisim.Strategy.ChooseTaxi.chooseTaxisStrategy;
import com.taxisim.auth.UserFactory;
import com.taxisim.logging.Logger;
import com.taxisim.service.BookingService;
import com.taxisim.ui.MenuHandler;

public class Main {
    public static void main(String[] args) {
        Logger log = Logger.getInstance();
        log.info("Application starting");
        int poolSize=Runtime.getRuntime().availableProcessors()+1;
        //set strategies or use default
        //BookingService service = new BookingService();
        //here we set normal strategy in both cases and pass to service
        chooseTaxisStrategy c=new NormalChoice(7);
        FareStrategy f=new NormalFare(100);
        BookingService service=new BookingService(c,f,poolSize);
        UserFactory userFactory = new UserFactory();
        MenuHandler menu = new MenuHandler(userFactory);
        menu.start();
        service.shutdown();
        log.info("Application exiting");
    }
}

