package Main;

import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;

import java.util.HashMap;
import java.util.TimerTask;

public class MoistCheck extends TimerTask {
        private Pin ledPin;
        private Pin moisturePin;
        private Pin motorPin;
        private SSD1306 display;
        private HashMap<Long, Integer> hashmap;

        // declare value of moisture threshold, higher value, more dryness required to activate pump.
        double moistureUpperBound = 2.7;

        public MoistCheck(Pin ledPin, Pin moisturePin, Pin motorPin, SSD1306 display, HashMap<Long, Integer> hashmap) {
            this.ledPin = ledPin;
            this.moisturePin = moisturePin;
            this.motorPin = motorPin;
            this.display = display;
            this.hashmap = hashmap;
        }

        @Override
        public void run() {
            try {
                int moistureLevel = (int) moisturePin.getValue();
                double moistureLevelVolt = moistureLevelToVoltage(moistureLevel);
                long currentTime = System.currentTimeMillis();
                System.out.println("Time: " + currentTime + ", Moisture: " + moistureLevelVolt);
                hashmap.put(currentTime, moistureLevel); // Adding current time and moisture level to hashmap

                if (moistureLevelVolt < moistureUpperBound) {
                    // Soil is wet, sleep for 5 mins.
                    ledPin.setValue(1);
                    motorPin.setValue(0);
                    System.out.println("Soil is wet. Sleeping for 5 mins.");

                    // show on display
                    display.getCanvas().clear();
                    display.getCanvas().drawString(0, 20, "Soil is currently wet");
                    display.display();
                    Thread.sleep(1000); // Sleep for 5 minutes
                } else {
                    // Soil is dry, pumping water.
                    System.out.println("Soil is dry, pumping water.");
                    ledPin.setValue(0);
                    display.getCanvas().clear();
                    display.getCanvas().drawString(0, 20, "The Soil is currently dry, adding water!");
                    display.display();

                    // Dispense water.
                    motorPin.setValue(1);
                    Thread.sleep(2000); // Run pump for 4 seconds
                    motorPin.setValue(0); // Stop pump
                    Thread.sleep(1000); // Wait for 1 second
                }
            } catch (Exception e) {
                System.out.println("Error Reading Moisture Level: " + e.getMessage());
            }
    }

    public double moistureLevelToVoltage(int moistureLevel) {
        return moistureLevel/204.6;
    }

}
