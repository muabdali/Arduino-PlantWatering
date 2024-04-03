package Main;

import Main.MoistCheck;
import org.firmata4j.IODevice;
import org.firmata4j.firmata.FirmataDevice;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

public class MainTesting {

    // Tests voltage conversion, moistureLevel to voltage.
    @Test
    public void voltageConversion() {
        int insertedValue = 1023;
        double expectedValue = 5.0;
        MoistCheck moistCheck = new MoistCheck(null, null, null, null, null);
        double actualValue = moistCheck.moistureLevelToVoltage(insertedValue);
        assertEquals(expectedValue, actualValue, 0.001); // Specify a delta for floating-point comparisons
    }
    // Tests device initialization.
    @Test
    public void ensureGrove() throws InterruptedException, IOException {
        var myGroveBoard = "/dev/cu.usbserial-0001";
        var device = new FirmataDevice(myGroveBoard);
        device.start();
        device.ensureInitializationIsDone();
    }
}