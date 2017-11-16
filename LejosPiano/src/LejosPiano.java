import lejos.hardware.ev3.EV3;

import javax.swing.*;

public class LejosPiano {
    public static void main(String[] args) {
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
        System.out.println("Creating " + name);
    }

    public void run () {
        System.out.println ("Running" + name);
        try {
            for (int i = 0; i < 4; i++) {
                System.out.println("Thread" + name + ", " + i);
                Thread.sleep(100);
            }
        }
        catch (InterruptedException e) {
            System.out.println ("Thread" + name + " is interrupted.");
        }
        System.out.println ("Thread" + name + " is exiting.");
    }

    public void start() {
        System.out.println("Starting" + name);
        if (thread == null) {
            thread = new Thread(this, name);
            thread.start();
        }
    }
}
