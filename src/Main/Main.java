package Main;

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
        Main main = new Main();
        main.start();
    }

    public void start() throws IOException, InterruptedException {
        var myGroveBoard = "/dev/cu.usbserial-0001";
        HashMap<Long, Integer> hashmap = new HashMap<>(); // Using Long for time

        // device is used as reference for groveboard here on out
        var device = new FirmataDevice(myGroveBoard);
        device.start();
        device.ensureInitializationIsDone();

        I2CDevice i2cObject = device.getI2CDevice((byte) 0x3C);
        SSD1306 display = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        display.init();

        var myLED = device.getPin(4);
        var moistureSensorPin = device.getPin(15);
        var buttonPin = device.getPin(6);
        moistureSensorPin.setMode(Pin.Mode.ANALOG);

        // Pin for motor pump
        var myMotor = device.getPin(6);
        myMotor.setMode(Pin.Mode.OUTPUT);

        // Creating timers, will assign them to functions later.
        Timer timer1 = new Timer();
        Timer timer2 = new Timer();

        var pumpTask = new MoistCheck(myLED, moistureSensorPin, myMotor, display, hashmap);
        var graphTasker = new GraphTask(hashmap);
        var buttonTask = new buttonActivate(buttonPin, myMotor, display);

        timer1.schedule(pumpTask, 0, 1000);
        timer2.schedule(graphTasker, 0, 1000);
        timer1.schedule(buttonTask, 0, 1000);
    }
// Creates graph based on values given by sensor, with time as x axis
// and moisture level as y axis.
    class GraphTask extends TimerTask {
        private HashMap<Long, Integer> hashmap;
        private final int X_INTERVAL = 5000; // 5 seconds
        private final int Y_INTERVAL = 1; // Intervals of 1 volt from 0 to 5 volts

        public GraphTask(HashMap<Long, Integer> hashmap) {
            this.hashmap = hashmap;
        }

        @Override
        public void run() {
            // Clear the drawing area
            StdDraw.clear();

            // Set up the drawing area
            long currentTime = System.currentTimeMillis();
            long minX = currentTime - 60000; // Show data from the last minute
            long maxX = currentTime;
            double minY = 0;
            double maxY = 5;

            StdDraw.setPenRadius(0.005);
            StdDraw.setPenColor(StdDraw.BLACK);

            StdDraw.setXscale(minX, maxX);
            StdDraw.setYscale(minY, maxY);

            Font label = new Font("Arial", Font.ITALIC, 15);
            StdDraw.setFont(label);
            StdDraw.text((minX + maxX) / 2, minY - 0.5, "Time (s)");
            StdDraw.text(minX - 1, (minY + maxY) / 2, "Moisture Reading (V)", 90);

            Font title = new Font("Arial", Font.BOLD, 20);
            StdDraw.setFont(title);
            StdDraw.text((minX + maxX) / 2, maxY + 0.5, "Plant Moisture Over Time");

            StdDraw.line(minX, minY, maxX, minY); // axis lines
            StdDraw.line(minX, minY, minX, maxY);

            Font lines = new Font("Arial", Font.ITALIC, 10);
            StdDraw.setFont(lines);

            // Other graph attributes such as ticks for x axis
            for (long x = minX + X_INTERVAL; x <= maxX; x += X_INTERVAL) {
                StdDraw.line(x, minY, x, minY + 0.1); // Draw tick mark
                StdDraw.text(x, minY - 0.2, String.valueOf((x - minX) / 1000.0)); // Draw label
            }

            // Other graph attributes such as ticks for y axis
            for (double y = minY + Y_INTERVAL; y <= maxY; y += Y_INTERVAL) {
                StdDraw.line(minX - 0.1, y, minX, y); // Draw tick mark
                StdDraw.text(minX - 0.5, y, String.format("%.1f", y)); // Draw label slightly to the left of the axis
            }

            // Plotting the moisture level over time.
            for (long time : hashmap.keySet()) {
                double moisture = hashmap.get(time);
                double x = time;
                // Find y level for point based on moisture.
                double y = moisture / 204.6; // Convert moisture level to voltage
                StdDraw.point(x, y);
            }

            // Show the graph
            StdDraw.show();
        }
    }
        private String formatTime(long millis) {
            return String.format("%.1f", (millis - System.currentTimeMillis()) / 1000.0);
        }
    }



