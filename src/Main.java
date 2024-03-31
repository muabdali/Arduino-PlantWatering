import org.firmata4j.I2CDevice;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import org.firmata4j.Pin;
import edu.princeton.cs.introcs.StdDraw;


import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        var myGroveBoard = "/dev/cu.usbserial-0001";
        HashMap<Integer, Integer> hashmap = new HashMap<>();

        // device is used as reference for groveboard here on out
        var device = new FirmataDevice(myGroveBoard);
        device.start();
        device.ensureInitializationIsDone();

        I2CDevice i2cObject = device.getI2CDevice((byte) 0x3C);
        SSD1306 display = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        display.init();

        var myLED = device.getPin(4);
        var moistureSensorPin = device.getPin(15);
        moistureSensorPin.setMode(Pin.Mode.ANALOG);

        // Pin for motor pump

        var myMotor = device.getPin(6);
        myMotor.setMode(Pin.Mode.OUTPUT);

        // Creating timers, will assign them to functions later.
        Timer timer1 = new Timer();
        Timer timer2 = new Timer();

        var pumpTask = new moistCheck(myLED, moistureSensorPin, myMotor, display);
        var graphTasker = new graphTask(moistureSensorPin, hashmap);

        timer1.schedule(pumpTask, 0 , 1000);
        timer2.schedule(graphTasker, 0 , 1000);

        //Setup STDDRAW

        StdDraw.setPenRadius(0.010);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setXscale(-3,100);
        StdDraw.setYscale(-40,1000);
        Font mainLabel = new Font("Arial", Font.BOLD, 15);
        StdDraw.setFont();

    }
    static class graphTask extends TimerTask {
        private final Pin moistureSensorPin;
        private HashMap hashMap;
        private static long moistureValueforGraph;
        private int extra = 1;


        public graphTask(Pin moistureSensorPin, HashMap<Integer, Integer> hashmap) {
            this.moistureSensorPin = moistureSensorPin;
            this.hashMap = hashmap;

        }

        @Override
        public void run() {
            moistureValueforGraph = moistureSensorPin.getValue();

            hashMap.put(extra, (int)moistureValueforGraph);

            for (int i = 1, i <= extra; i+=2){
                if(hashMap.containsKey(i)){
                    int time = i;
                    int y = (int) hashMap.get(i);
                    StdDraw.text(time, y, "*");
                }
            }
        }
    }
}

