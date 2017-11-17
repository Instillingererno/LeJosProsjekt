import lejos.hardware.BrickFinder;
import lejos.hardware.Sound;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;

import java.io.File;

public class LydTest {
    public static void main(String[] args) {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD lcd = ev3.getTextLCD();
        Sound.setVolume(50);

        lcd.drawString("Laster A",0,1);
        File a = new File("A.wav");

        lcd.drawString("Laster C",0,1);
        File c = new File("C.wav");

        lcd.drawString("Laster F",0,1);
        File f = new File("F.wav");

        lcd.drawString("Laster G",0,1);
        File g = new File("G.wav");


        lcd.drawString("Spiller A",0,1);
        Sound.playSample(a);

        lcd.drawString("Spiller C",0,2);
        Sound.playSample(c);

        lcd.drawString("Spiller G",0,3);
        Sound.playSample(g);

        lcd.drawString("Spiller F",0,4);
        Sound.playSample(f);


        lcd.drawString("Lukker", 0,6);
        ev3 = null;
        lcd = null;
        a = null;
        c = null;
        f = null;
        g = null;

        System.exit(0);
    }
}
