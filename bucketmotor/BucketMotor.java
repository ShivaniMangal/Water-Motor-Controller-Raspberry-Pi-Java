/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bucketmotor;

import javax.microedition.midlet.MIDlet;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
/**
 *
 * @author Shivani Mangal
 */
public class BucketMotor extends MIDlet {
    
    Sensor hcsr04;
    private static final int TRIGGER_PIN = 23;
    private static final int ECHO_PIN = 17;
    
    //Define execution of read sensors thread
    private volatile boolean shouldRun = true;
    private ReadSensors sensorsTask;
    
      GPIOPin ledpin;
      GPIOPin relaypin;
    
    @Override
    public void startApp() {
   
        //Initialize Ultrasound sensor
        hcsr04=new Sensor(TRIGGER_PIN, ECHO_PIN);
        //Start read sensors data thread
        sensorsTask=new ReadSensors();
        sensorsTask.start();
    }

    @Override
    public void destroyApp(boolean unconditional) {
        shouldRun=false;
    
       
        try
        {
            relaypin.close();
            ledpin.close();
        }
        catch(IOException ex)
        { Logger.getLogger(BucketMotor.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        hcsr04.close();
    
    }

    
    // Thread to read distance every 5 seconds
    class ReadSensors extends Thread {
        
        private double distance=0.0;
      //  private final double ERROR = 3;
       // private double corrected_level =0.0;
        
        @Override
        public void run() {
            try
            {
                   GPIOPin ledpin = DeviceManager.open(4); 
                   GPIOPin relaypin = DeviceManager.open(8);//GPIO 25
                   relaypin.setValue(true);
                
            while (shouldRun)
            {
                distance = hcsr04.pulse();
            
           relaypin.setValue(true);     
           //corrected_level= 11 + ERROR;
	if(distance<=23.5 && distance>11)//distance>corrected_level)//<=23.5 -> water moves up >11 -> water moves down wrt sensor
        {     
         //corrected_level=11+ERROR;
     
do
{
relaypin.setValue(false);
ledpin.setValue(true);
System.out.println("Water levels are within the permissible range. \nCurrent water level  : "+ distance+" cm");
distance=hcsr04.pulse();
//corrected_level= 11+ERROR;
//Thread.sleep(3);
} 
while(distance<=23.5 && distance>11);//corrected_level);
relaypin.setValue(true);
ledpin.setValue(false);
//check whether to add here
//corrected_level= distance+ERROR;
if(distance<=11) //corrected_level)
{
    relaypin.setValue(true);
System.out.println("WARNING! The tank is at a risk of overflow. Current Water Level :"+ distance +" cm.");
}
} 
else if(distance<=11)//corrected_level)
{
    relaypin.setValue(true);
    System.out.println("Warning! Risk of overflow. Stop filling the tank. Current water level :" + distance + " cm.");
}
        else
{
    //corrected_level=11+distance;
    relaypin.setValue(true);
    System.out.println("Underflow. Proceed to fill up the tank. Current water level :"+distance+" cm.");
}
   
  }
        }
          /*  catch(InterruptedException e)
            {
                 }*/
            catch(IOException e)
            {
               Logger.getLogger(BucketMotor.class.getName()).
                    log(Level.SEVERE, null, e);   
            }
          
   
    }
}
}
