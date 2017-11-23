import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.NXTTouchSensor;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

// Enum til bruk å skille mellom NXT trykksensorer og EV3 trykksensorer
enum Type {
    NXT, EV3
}

// Hovedklassen som kjører på EV3-en, håndterer bruk av TrykkSensor og Server klassen
public class LejosPiano {
    public static void main(String[] args) throws IOException {

        // Init til å kunne skrive til EV3-skjermen
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD LCD = ev3.getTextLCD();

        // Init av trykksensorene
        TrykkSensor s1 = new TrykkSensor(Type.EV3, SensorPort.S1);
        TrykkSensor s2 = new TrykkSensor(Type.NXT, SensorPort.S2);
        TrykkSensor s3 = new TrykkSensor(Type.NXT, SensorPort.S3);
        TrykkSensor s4 = new TrykkSensor(Type.NXT, SensorPort.S4);

        // Init av server
        Server host = new Server();
        // connect() lytter etter pc-en
        host.connect();
        while(!Button.ESCAPE.isDown()) {
            LCD.clear();
            while(!Button.ESCAPE.isDown()) {
                vent(); // vent() legger inn en 4 ms delay mellom hver gang trykksensorene sjekkes
                        // dette gjøres fordi trykksensorene kan gi et vekslene signal om de er trykket mens de blir trykket ned
                try {
                    /*
                        EV3-en sender en int mellom 1 og 8 til pc-en der oddetallene er når en trykksensor er blitt trykket ned
                        og partallene er når trykksensorene har blitt løftet etter å ha vært trykket.
                        Det lar oss ha sustain i akkorder eller noter som spilles.
                     */

                    // Sjekker hver av trykksensorene om de er trykket
                    if(s1.isPressed()) {
                        host.out.writeInt(1);
                        LCD.clear(1);
                        LCD.drawString("S1: Pressed" + "  " + s1.getSample(),0,1);
                    }
                    if(s2.isPressed()) {
                        host.out.writeInt(3);
                        LCD.clear(2);
                        LCD.drawString("S2: Pressed" + "  " + s2.getSample(),0,2);
                    }
                    if(s3.isPressed()) {
                        host.out.writeInt(5);
                        LCD.clear(3);
                        LCD.drawString("S3: Pressed" + "  " + s3.getSample(),0,3);
                    }
                    if(s4.isPressed()) {
                        host.out.writeInt(7);
                        LCD.clear(4);
                        LCD.drawString("S4: Pressed" + "  " + s4.getSample(),0,4);
                    }

                    // Sjekker om hver av trykksensorene har blitt løftet opp fra å være trykket ned
                    if(s1.isReleased()) {
                        host.out.writeInt(2);
                        LCD.drawString("S1: Released" + " " + s1.getSample(),0,1);
                    }
                    if(s2.isReleased()) {
                        host.out.writeInt(4);
                        LCD.drawString("S2: Released" + " " + s2.getSample(),0,2);
                    }
                    if(s3.isReleased()) {
                        host.out.writeInt(6);
                        LCD.drawString("S3: Released" + " " + s3.getSample(),0,3);
                    }
                    if(s4.isReleased()) {
                        host.out.writeInt(8);
                        LCD.drawString("S4: Released" + " " + s4.getSample(),0,4);
                    }
                } catch(Exception e) {
                    /*
                        Tidligere så prøvde vi å sjekke isConnected() på Socket men den ga true selv om PC-en ikke hadde
                        programmet kjørende, derfor håndterer heller EV3-en unntaket som blir kastet når den prøver å
                        sende data til PC-en og PC-en ikke kan ta imot.
                     */
                    LCD.clear();
                    LCD.drawString("Tilkobling mistet",0,1);
                    LCD.drawString("Prøver på ny",0,2);
                    host.accept();
                }
            }
        }
    }
    // Metode som holder programmet i 4 ms.
    private static void vent() {
        try{
            TimeUnit.MILLISECONDS.sleep(4);
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}

/*
    Klasse som håndterer init'en til trykksensorene
    og har metoder for å sjekke om sensoren har blitt trykket og løftet.
 */
class TrykkSensor {
    private Port port;
    private Type type;
    private NXTTouchSensor nxt;
    private EV3TouchSensor ev3;
    private boolean isLifted = true;
    private float[] sample;

    public TrykkSensor(Type type, Port port) {
        this.type = type;
        this.port = port;

        if(this.type == Type.NXT) {
            this.nxt = new NXTTouchSensor(this.port);
        } else if(this.type == Type.EV3) {
            this.ev3 = new EV3TouchSensor(this.port);
        }

        this.sample = (this.type == Type.EV3) ? new float[ev3.sampleSize()] : new float[nxt.sampleSize()];

    }
    public boolean isPressed() {
        if(this.type == Type.EV3) ev3.fetchSample(this.sample,0);
        else nxt.fetchSample(this.sample,0);

        if (isLifted && this.sample[0] == 1) {
            isLifted = false;
            return true;
        }

        return false;
    }
    public boolean isReleased() {
        if(!isLifted && this.sample[0] == 0) {
            isLifted = true;
            return true;
        }
        return false;
    }
    public String getSample() { // Brukes for å skrive ut sensor verdien til EV3-skjermen
        return Arrays.toString(sample);
    }
}