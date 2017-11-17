import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.Sound;

import java.io.File;

public class LejosPiano {
    public static void main(String[] args) {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD LCD = ev3.getTextLCD();
        int buttons = 0;

        while(true) {
            buttons = Button.readButtons();
            if(Integer.toString(buttons).equals("2")) {
               demThreads newThread = new demThreads("Sphee");
               newThread.start();
            }


        }
    }
}

class demThreads extends Thread {
    private Thread thread;
    private String name;

    public demThreads(String name) {
        this.name = name;
        LCD.drawString("Creating " + name, 0, 1, false);
    }

    public void run () {
        File a = new File("A.wav");
       /* File c = new File("C.wav");
        File f = new File("F.wav");
        File g = new File("G.wav");*/
        Sound.playSample(a);
    }

    public void start() {
        LCD.drawString("Starting" + name, 0, 6, false);
        if (thread == null) {
            thread = new Thread(this, name);
            thread.start();
        }
    }
}
