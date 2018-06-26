/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bucketmotor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 *
 * @author Shivani Mangal
 */
public class Sensor {
    private final int PULSE = 10000;        // #10 µs pulse = 10,000 ns
    private final int SPEEDOFSOUND = 34029; // Speed of sound = 34029 cm/s

    private GPIOPin trigger = null;
    private GPIOPin echo = null;
    public Sensor(int _trigger, int _echo) {
        try
        {
trigger = (GPIOPin) DeviceManager.open(new GPIOPinConfig(0, _trigger, 
GPIOPinConfig.DIR_OUTPUT_ONLY, GPIOPinConfig.MODE_OUTPUT_PUSH_PULL,
       GPIOPinConfig.TRIGGER_NONE, false));

echo = (GPIOPin) DeviceManager.open(new GPIOPinConfig(0, _echo, 
GPIOPinConfig.DIR_INPUT_ONLY, GPIOPinConfig.MODE_INPUT_PULL_UP, 
GPIOPinConfig.TRIGGER_NONE, false));

       Thread.sleep(5);  
 
        }
        catch(IOException e)
        {
        }
        catch(InterruptedException i)
        {
        }
    }
public double pulse() {
  long distance = 0;
  try {
      trigger.setValue(true); //Send a pulse trigger; must be 1 and 0 with a 10 µs wait
      Thread.sleep(PULSE);// wait 10 µs
      trigger.setValue(false);
      long starttime = System.nanoTime(); //ns
      long stop = starttime;
      long start = starttime;
      //echo will go 0 to 1 and needs to save time for that. 2 seconds difference
      while ((!echo.getValue()) && (start < starttime + 1000000000L * 2)) {
          start = System.nanoTime();
      }
      while ((echo.getValue()) && (stop < starttime + 1000000000L * 2)) {
          stop = System.nanoTime();
      }
      long delta = (stop - start);
      distance = delta * SPEEDOFSOUND; // echo from 0 to 1 depending on object distance
  } catch (IOException ex) {
      Logger.getGlobal().log(Level.WARNING,ex.getMessage());
  }
  catch(InterruptedException e)
  {
      
  }
  return distance / 2.0 / (1000000000L); // cm/s
}
public void close() {
try
{
    if ((trigger!=null) && (echo!=null)){
           trigger.close();
        echo.close();
        }

}
catch(IOException i)
{
}
}
    
    
}
