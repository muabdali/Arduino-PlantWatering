import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;

import java.util.TimerTask;

public class moistCheck extends TimerTask {
    private final Pin myMotor;
    private final SSD1306 display;
    // declare variables
    int moistureUpperBound = (int) 600;


    private final Pin moistureSensorPin;
    private final Pin myLED;

    public moistCheck(Pin myLED, Pin moistureSensorPin, Pin myMotor, SSD1306 display) {
        this.moistureSensorPin = moistureSensorPin;
        this.myLED = myLED;
        this.myMotor = myMotor;
        this.display = display;


    }

    @Override
    public void run() {
        try{
            int moistureLevel = (int) moistureSensorPin.getValue();
            System.out.println(moistureLevel);
            if (moistureLevel < moistureUpperBound){
                display.getCanvas().clear();
                // Soil is wet, sleep for 5 mins.
                myLED.setValue(1);
                myMotor.setValue(0);
                System.out.println("Soil is wet. Sleeping for 5 mins.");

                // show on display
                display.getCanvas().clear();
                display.getCanvas().drawString(0,20,"Soil is currently wet");
                display.display();
                Thread.sleep(5000);

            }
            else{
                // soil is dry, pumping water.
                System.out.println("Soil is dry, pumping water.");
                myLED.setValue(0);
                display.getCanvas().clear();
                display.getCanvas().drawString(0, 20, "The Soil is currently dry, adding water!");
                display.display();
                // Dispense water.
                try {
                    myMotor.setValue(1);
                    Thread.sleep(4000);
                    myMotor.setValue(0);
                    Thread.sleep(1000);
                }catch (Exception e){
                    System.out.println("Motor Error");
                }
            }
        }catch (Exception e){
            System.out.println("Error Reading Moisture Level");
        }

    }

    // Moisture detector always counting voltage passed, once it passes a thresh hold,
    // light LED and activate pump.
}
