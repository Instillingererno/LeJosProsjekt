import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.Sound;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.NXTTouchSensor;

import java.io.File;
import java.util.concurrent.TimeUnit;

enum Type {
    NXT, EV3
}

public class LejosPiano {
    public static void main(String[] args) {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD LCD = ev3.getTextLCD();
        int buttons = 0;
        int teller = 0;
        TrykkSensor A = new TrykkSensor(Type.EV3, SensorPort.S1, "A.wav");
        TrykkSensor C = new TrykkSensor(Type.NXT, SensorPort.S2, "C.wav");
        TrykkSensor F = new TrykkSensor(Type.EV3, SensorPort.S3, "F.wav");
        TrykkSensor G = new TrykkSensor(Type.NXT, SensorPort.S4, "G.wav");

        Server host = new Server();
        host.connect();

        while(!Button.ESCAPE.isDown()) {

            if(A.isPressed()) {
                host.send(A.toString());
                teller++;
                LCD.drawString(Integer.toString(teller),0,1);
            } else if(C.isPressed()) {
                host.send(C.toString());
                teller++;
                LCD.drawString(Integer.toString(teller),0,1);
            } else if(F.isPressed()) {
                host.send(F.toString());
                teller++;
                LCD.drawString(Integer.toString(teller),0,1);
            } else if(G.isPressed()) {
                host.send(G.toString());
                teller++;
                LCD.drawString(Integer.toString(teller),0,1);
            }
        }
    }
    public static void vent() {
        try{
            TimeUnit.SECONDS.sleep(5);
        } catch(Exception e) {
            System.out.println(e);
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

class TrykkSensor {
    private Port port;
    private String output;
    private Type type;
    private NXTTouchSensor nxt;
    private EV3TouchSensor ev3;
    private boolean isLifted = true;
    float[] sample;

    public TrykkSensor(Type type, Port port, String output) {
        this.type = type;
        this.output = output;
        this.port = port;

        if(this.type == Type.NXT) {
            this.nxt = new NXTTouchSensor(this.port);
        } else if(this.type == Type.EV3) {
            this.ev3 = new EV3TouchSensor(this.port);
        }

        this.sample = (this.type == Type.EV3) ? new float[ev3.sampleSize()] : new float[nxt.sampleSize()];

    }

    public String toString() {
        return output;
    }


    public boolean isPressed() {
        if(this.type == Type.EV3) { ev3.fetchSample(sample,0); }
        else { nxt.fetchSample(sample,0); }

        if (isLifted && sample[0] == 1) {
            isLifted = false;
            return true;
        }
        else if(!isLifted && sample[0] == 1){
            return false;
        }
        else {
            isLifted = true;
            return false;
        }
    }
}
