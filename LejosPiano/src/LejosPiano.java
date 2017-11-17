import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;

public class LejosPiano {
    public static void main(String[] args) {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD LCD = ev3.getTextLCD();
        demThreads thread1 = new demThreads("Thread 1", 0);

        demThreads thread2 = new demThreads("Thread 2", 10000);
        thread1.start();
        thread2.start();
    }
}

class demThreads extends Thread {
    private Thread thread;
    private String name;
    private int wait;

    public demThreads(String name, int wait) {
        this.name = name;
        this.wait = wait;
        LCD.drawString("Creating " + name, 0, 1, false);
    }

    public void run () {
		try{
			Thread.sleep(wait);
		} catch(Exception e) {
			//idfk
			LCD.drawString("wtf happened", 0, 1, true);
		}

        LCD.drawString ("Running" + name, 0, 2, false);
        try {
            for (int i = 0; i < 4; i++) {
                LCD.drawString("Thread" + name + ", " + i, 0, 3, false);
                Thread.sleep(10000);
            }
        }
        catch (InterruptedException e) {
            LCD.drawString ("Thread" + name + " is interrupted.", 0, 4, false);
        }
        LCD.drawString ("Thread" + name + " is exiting.", 0, 5, false);
    }

    public void start() {
        LCD.drawString("Starting" + name, 0, 6, false);
        if (thread == null) {
            thread = new Thread(this, name);
            thread.start();
        }
    }
}
