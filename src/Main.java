import org.firmata4j.I2CDevice;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.IODevice;
import org.firmata4j.ssd1306.SSD1306;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;

import java.io.IOException;
import java.util.Timer;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        var myGroveBoard = "/dev/cu.usbserial-0001";

        // device is used as reference for groveboard here on out
        var device = new FirmataDevice(myGroveBoard);
        device.start();
        device.ensureInitializationIsDone();

        var myLED = device.getPin(4);
        var moistureSensorPin = device.getPin(14);
        moistureSensorPin.setMode(Pin.Mode.ANALOG);

        var task = new moistCheck(myLED, moistureSensorPin);

    }
}