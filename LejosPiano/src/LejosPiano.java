package Øving15;

import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;

public class LejosPiano {
    public static void main(String[] args) {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD LCD = ev3.getTextLCD();
        demThreads thread1 = new demThreads("Thread 1");
        demThreads thread2 = new demThreads("Thread 2");
        thread1.start();
        thread2.start();
    }
}

class demThreads extends Thread {
    private Thread thread;
    private String name;

    public demThreads(String name) {
        this.name = name;
        LCD.drawString("Creating " + name, 50, 50, false);
    }

    public void run () {
        LCD.drawString ("Running" + name, 50, 50, false);
        try {
            for (int i = 0; i < 4; i++) {
                LCD.drawString("Thread" + name + ", " + i, 50, 50, false);
                Thread.sleep(100);
            }
        }
        catch (InterruptedException e) {
            LCD.drawString ("Thread" + name + " is interrupted.", 50, 50, false);
        }
        LCD.drawString ("Thread" + name + " is exiting.", 50, 50, false);
    }

    public void start() {
        LCD.drawString("Starting" + name, 50, 50, false);
        if (thread == null) {
            thread = new Thread(this, name);
            thread.start();
        }
    }
}
