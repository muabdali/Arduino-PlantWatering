package Main;

import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;

import java.io.IOException;
import java.util.TimerTask;

public class buttonActivate extends TimerTask {
    private Pin buttonPin;
    private Pin motorPin;
    private SSD1306 display;
    public buttonActivate(Pin buttonPin, Pin motorPin, SSD1306 display) {
        this.buttonPin = buttonPin;
        this.motorPin = motorPin;
        this.display = display;
    }

    @Override
    public void run() {
        int buttonState = (int) buttonPin.getValue();
        if (buttonState == 1) {
            try {
                motorPin.setValue(1);
                display.getCanvas().clear();
                display.getCanvas().drawString(0, 20, "Manual Override, Applying water.");
                display.display();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
