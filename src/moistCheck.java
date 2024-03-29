import org.firmata4j.Pin;

import java.util.TimerTask;

public class moistCheck extends TimerTask {
    int moistureUpperBound = (int) 3.0;


    private final Pin moistureSensorPin;
    private final Pin myLED;

    public moistCheck(Pin myLED, Pin moistureSensorPin) {
        this.moistureSensorPin = moistureSensorPin;
        this.myLED = myLED;


    }

    @Override
    public void run() {
        try{
            int moistureLevel = (int) moistureSensorPin.getValue();
            if (moistureLevel > moistureUpperBound){
                myLED.setValue(1);
            }
        }catch (Exception e){
            System.out.println("Error Reading Moisture Level");
        }

    }

    // Moisture detector always counting voltage passed, once it passes a thresh hold,
    // light LED and activate pump.
}
