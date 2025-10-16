package com.taxisim.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private static Logger instance; //instance should always be private static
    //declared static because it must be accessed only through the class reference and not through the object of the class
    private BufferedWriter writer;

    private Logger() //always a private constructor - so that any obj instantition call from outside the class is not permitted
    {
        try
        {
            File log=new File("log");
            if(!log.exists())
            {
                log.mkdirs();
            }
            FileWriter fw=new FileWriter("log/taxisim.log",true);
            this.writer=new BufferedWriter(fw);
        }
        catch(IOException e)
        {
            throw new RuntimeException("Error in logging",e);
        }
    }

    public static synchronized Logger getInstance() { //atleast 1 public static method to return the same instance on every subsequent call to create a obj
        if (instance == null) instance = new Logger();
        return instance;
    }

    public synchronized void write(String type,String msg) {
        try {

            String logEntry=String.format("[%s] %d - %s%n",type,System.currentTimeMillis(),msg);
            writer.write(logEntry);
            writer.flush();
        }
        catch (IOException e)
        {
            System.err.println("Logging failed: " + e.getMessage());
        }
    }

    public void info(String msg)
    {
        write("INFO", msg);
    }
    public void warn(String msg)
    {
        write("WARN", msg);
    }
    public void error(String msg)
    {
        write("ERROR", msg);
    }

}
