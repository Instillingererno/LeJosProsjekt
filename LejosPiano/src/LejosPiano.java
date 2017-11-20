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

enum Type {
    NXT, EV3
}

public class LejosPiano {

    public static void main(String[] args) throws IOException {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD LCD = ev3.getTextLCD();

        TrykkSensor s1 = new TrykkSensor(Type.EV3, SensorPort.S1);
        TrykkSensor s2 = new TrykkSensor(Type.NXT, SensorPort.S2);
        TrykkSensor s3 = new TrykkSensor(Type.EV3, SensorPort.S3);
        TrykkSensor s4 = new TrykkSensor(Type.NXT, SensorPort.S4);

        //Button.UP.addButtonListener();
        while(!Button.ESCAPE.isDown()) {
            Server host = new Server();
            host.connect();

            LCD.clear();
            while(!Button.ESCAPE.isDown() && host.isConnected()) {
                try {
                    //PRESSED
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

                    //RELEASED
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
                    LCD.clear();
                    LCD.drawString("Tilkobling mistet, prøver på ny",0,1);
                }
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
    public String getSample() {
        return Arrays.toString(sample);
    }
}