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
import lejos.hardware.Device;
import java.io.Closeable;
import lejos.hardware.Button;


/* Pseudo kode

	Import lejos hardware

	Intitiate sensors and motors

	Kjoer rett frem med mindre: En bil kommer (Lydsensor aktiveres)

	Stopp hvis man kommer til slutten av tunellen til teipen (Hvis fargesensor aktiveres)

*/

public class Oeving3 {
	public static void main(String[] args) throws Exception{

		int test = 0;
		int nedteller = 0;
		boolean serBilenEndenAvTunellen = false;
		boolean rygger = false;
		final long stoppeVarighet = 3000;	// Hvor lenge skal bilen stoppe naar den moeter en bil. (Oppgis i ms)
		final double lydTerskel = 0.9; 		// Hvor hoey lyd maa noe lage for at det skal gjenkjennes som en bil.
		final double fargeTerskel = 0.01;	// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
		final int motorHastighet = 200;
		final int vaskeMotorHastighet = 900;


		Brick brick = BrickFinder.getDefault();
		Port s1 = brick.getPort("S1"); 		// Fargesensor
		Port s2 = brick.getPort("S2"); 		// Lydsensor


		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();

		Motor.B.setSpeed(motorHastighet);
		Motor.C.setSpeed(-motorHastighet);
		Motor.A.setSpeed(vaskeMotorHastighet);

		/* Definerer en fargesensor og fargeAvleser */
		EV3ColorSensor fargesensor = new EV3ColorSensor(s1); // ev3-fargesensor
		SampleProvider fargeLeser = fargesensor.getMode("RGB");  // svart = 0.01..
		float[] fargeSample = new float[fargeLeser.sampleSize()]; // tabell som inneholder avlest verdi

		/* Definerer en lydsensor og lydAvleser */
		NXTSoundSensor lydsensor = new NXTSoundSensor(s2); // NXT-lydsensor
		SampleProvider lydLeser = lydsensor.getDBAMode();  //
		float[] lydSample = new float[lydLeser.sampleSize()]; // tabell som inneholder avlest verdi

		while (true){
			while(true){ // Hvis bilen ikke er i slutten av tunellen, og det ikke er noen andre biler.
				LCD.clear();
				lcd.drawString("Kjoerer...", 0,1);

				fargeLeser.fetchSample(fargeSample, 0);
				lydLeser.fetchSample(lydSample, 0);

				if(nedteller <= 0){
					if(fargeSample[0] < fargeTerskel){
						if(serBilenEndenAvTunellen){
							serBilenEndenAvTunellen = false;
						} else {
							serBilenEndenAvTunellen = true;
						}
						nedteller = 300;
						break;
					}
				}
				nedteller--;
				lcd.drawString(Integer.toString(nedteller), 0,2);

				if(lydSample[0] > lydTerskel){
					Motor.B.stop(true);
					Motor.C.stop(true);
					Motor.A.stop(true);
					Thread.sleep(stoppeVarighet);
				}

				if(serBilenEndenAvTunellen){
					Motor.B.backward();
					Motor.C.backward();
				} else {
					Motor.B.forward();
					Motor.C.forward();
				}
					Motor.A.forward();

				test = Button.readButtons();
				if(Integer.toString(test).contains("2")){
					break;
				}

			}

			test = Button.readButtons();
			if(Integer.toString(test).contains("2")){
				break;
			}
		}
	fargesensor.close();
	lydsensor.close();
	}
}
