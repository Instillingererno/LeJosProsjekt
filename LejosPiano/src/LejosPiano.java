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


enum Type {
    NXT, EV3
}


public class LejosPiano {
    public static void main(String[] args) {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD LCD = ev3.getTextLCD();
        int buttons = 0;
        TrykkSensor ev3Sensor1 = new TrykkSensor(Type.EV3, SensorPort.S1, "Sphee");

        while(true) {
            if(ev3Sensor1.isPressed()) {
                LCD.drawString("It is pressed!", 0, 0);
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

class TrykkSensor {
    private Port port;
    private String output;
    private Type type;
    private Object sensor;
    private boolean isLifted = true;
    float[] sample;

    public TrykkSensor(Type type, Port port, String output) {
        this.type = type;
        this.output = output;
        this.port = port;

        if(this.type == Type.NXT) {
            this.sensor = new NXTTouchSensor(this.port);
        } else if(this.type == Type.EV3) {
            this.sensor = new EV3TouchSensor(this.port);
        }

        sample = new float[1];

    }



    public boolean isPressed() {
        if (isLifted && sensor.fetchSample(sample, 0) == 1) {
            isLifted = false;
            return true;
        }
        else if(!isLifted && sensor.fetchSample(sample, 0) == 1){
            return false;
        }
        else {
            isLifted = true;
            return false;
        }
    }
}
