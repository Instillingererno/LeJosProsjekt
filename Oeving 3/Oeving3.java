/*	Bilen skal vaske en tunell og stoppe midlertidig hvis en bil kommer,
	og stoppe helt om den naar slutten av tunellen.	*/

import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.NXTLightSensor;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.hardware.sensor.NXTSoundSensor;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;


/* Pseudo kode

	Import lejos hardware

	Intitiate sensors and motors

	Kjoer rett frem med mindre: En bil kommer (Lydsensor aktiveres)

	Stopp hvis man kommer til slutten av tunellen til teipen (Hvis fargesensor aktiveres)

*/

public class Oeving3 {
public static void main(String[] args) throws Exception{

	boolean sluttenAvTunellen = false;
	boolean erDetNoenBiler = false;
	final long stoppeVarighet = 3000;	// Hvor lenge skal bilen stoppe naar den moeter en bil. (Oppgis i ms)
	final double lydTerskel = 0.6; 		// Hvor hoey lyd maa noe lage for at det skal gjenkjennes som en bil.
	double fargeTerskel = 0.0;	// Hvor lav RGB verdi maa bakken voere for at det skal gjenkjennes som svart.
	final int motorHastighet = 200;

	Brick brick = BrickFinder.getDefault();
	Port s1 = brick.getPort("S1"); 		// Fargesensor
	Port s2 = brick.getPort("S2"); 		// Lydsensor


	EV3 ev3 = (EV3) BrickFinder.getLocal();
	TextLCD lcd = ev3.getTextLCD();
	Keys keys = ev3.getKeys();

	Motor.B.setSpeed(-motorHastighet);
	Motor.C.setSpeed(motorHastighet);

	/* Definerer en fargesensor og fargeAvleser */
	EV3ColorSensor fargesensor = new EV3ColorSensor(s1); // ev3-fargesensor
    SampleProvider fargeLeser = fargesensor.getMode("RGB");  // svart = 0.01..
    float[] fargeSample = new float[fargeLeser.sampleSize()]; // tabell som inneholder avlest verdi

    /* Definerer en lydsensor og lydAvleser */
	NXTSoundSensor lydsensor = new NXTSoundSensor(s2); // NXT-lydsensor
	SampleProvider lydLeser = lydsensor.getDBAMode();  //
    float[] lydSample = new float[lydLeser.sampleSize()]; // tabell som inneholder avlest verdi


	//Kode for aa definere fargeTerskel (Fargesensor maa se paa det som defineres som svart, naar denne koden kjoeres)
	fargeLeser.fetchSample(fargeSample, 0);

	lcd.drawString("Foer vasking maa fargesensor", 0,1);
	lcd.drawString("plasseres paa sort linje", 0,2);
	lcd.drawString("saa svart kan defineres", 0,3);
	lcd.drawString("trykk paa en knapp for aa", 0,4);
	lcd.drawString("starte kalibrering.", 0,5);
	keys.waitForAnyPress();
	LCD.clear();
	fargeTerskel = fargeSample[0];
	lcd.drawString("Kalibrering ferdig!", 0,1);
	lcd.drawString("Trykk en knapp for aa starte vasking.", 0,2);
	keys.waitForAnyPress();


	// Kode som kjoerer under drift
		while (true){
			sluttenAvTunellen = false;
			erDetNoenBiler = false;

			while(!sluttenAvTunellen){ // Hvis bilen ikke er i slutten av tunellen, og det ikke er noen andre biler.
			LCD.clear();
			lcd.drawString("Kjoerer...", 0,1);

				fargeLeser.fetchSample(fargeSample, 0);
				lydLeser.fetchSample(lydSample, 0);

				if(fargeSample[0] < fargeTerskel){
					sluttenAvTunellen = true;
					break;
				}

				if(lydSample[0] > lydTerskel){
					erDetNoenBiler = true;
					Motor.B.stop(true);
					Motor.C.stop(true);
					Thread.sleep(stoppeVarighet);
				}

				Motor.B.forward();  // Start motor A - kjoer framover
				Motor.C.forward();  // Start motor C - kjoer framover
				Thread.sleep(200);
			}

			LCD.clear();
			lcd.drawString("Kommet til slutten av tunellen!", 0,1);
			lcd.drawString("Trykk en knapp for aa gjenoppta vasking", 0,2);

			Motor.B.stop(true);
			Motor.C.stop(true);
			keys.waitForAnyPress();
			LCD.clear();
		}
	}
}