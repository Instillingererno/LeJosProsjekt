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
import java.io.IOException;
import java.util.concurrent.TimeUnit;

enum Type {
    NXT, EV3
}

public class LejosPiano {
    public static Server host = new Server();
    public static void main(String[] args) throws IOException {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD LCD = ev3.getTextLCD();
        int buttons = 0;
        int teller = 0;
        TrykkSensor s1 = new TrykkSensor(Type.EV3, SensorPort.S1, "A.wav");
        TrykkSensor s2 = new TrykkSensor(Type.NXT, SensorPort.S2, "C.wav");
        TrykkSensor s3 = new TrykkSensor(Type.EV3, SensorPort.S3, "F.wav");
        TrykkSensor s4 = new TrykkSensor(Type.NXT, SensorPort.S4, "G.wav");


        host.connect();

        while(!Button.ESCAPE.isDown()) {
            if(s1.isPressed()) host.out.writeInt(1);
            if(s1.isReleased()) host.out.writeInt(2);

            if(s2.isPressed()) host.out.writeInt(3);
            if(s2.isReleased()); host.out.writeInt(4);

            if(s3.isPressed()) host.out.writeInt(5);
            if(s3.isReleased()); host.out.writeInt(6);

            if(s4.isPressed()) host.out.writeInt(7);
            if(s4.isReleased()); host.out.writeInt(8);
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

class TrykkSensor {
    private Port port;
    private String output;
    private Type type;
    private NXTTouchSensor nxt;
    private EV3TouchSensor ev3;
    private boolean isLifted = true;
    private boolean isPressed = false;
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
        if(this.type == Type.EV3) ev3.fetchSample(sample,0);
        else nxt.fetchSample(sample,0);

        if (isLifted && sample[0] == 1) {
            isLifted = false;
            return true;
        }

        return false;
    }
    public boolean isReleased() {
        if(this.type == Type.EV3) ev3.fetchSample(sample, 0);
        else nxt.fetchSample(sample, 0);

        if(!isLifted && sample[0] == 0) {
            isLifted = true;
            return true;
        }

        return false;
    }
}
