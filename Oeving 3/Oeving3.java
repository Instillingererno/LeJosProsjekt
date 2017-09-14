/*	Bilen skal vaske en tunell og stoppe midlertidig hvis en bil kommer,
	og stoppe helt om den naar slutten av tunellen.	*/

import lejos.hardware.motor.*;					// Må inkluderes så motor kan styres
import lejos.hardware.lcd.*;					// Må inkluderes så lcd kan styres
import lejos.hardware.sensor.NXTColorSensor;	// Må inkluderes så fargesensor kan styres
import lejos.hardware.sensor.NXTSoundSensor;	// Må inkluderes så lydsensor kan styres
import lejos.hardware.port.Port;			   	// Må inkluderes så porter kan styres
import lejos.hardware.Brick;					// Må inkluderes så EV3-klossen kan styres
import lejos.hardware.BrickFinder;				// Må inkluderes så EV3-klossen kan ses
import lejos.hardware.ev3.EV3;					// Må inkluderes så EV3-biblioteket kan brukes
import lejos.hardware.Keys;						// Må inkluderes så programmet kan lese input fra knappene på EV3 klossen
import lejos.hardware.sensor.SensorModes;		// Gir tilgang til forskjellige moduser sensorene kan ha
import lejos.robotics.SampleProvider;			// Må inkluderes for å kunne hente informasjon fra sensorene
import lejos.hardware.sensor.*;					// Må inkluderes så sensorer kan brukes
import lejos.hardware.Device;					// Må inkluderes så programmet kan referere til EV3-klossen
import lejos.hardware.Button;					// Må inkluderes så EV3-biblioteket kan brukes
												// Klassene må inkluderes så programmet kan ta bruk av metoder i klassene.
public class Oeving3 {
	public static void main(String[] args) throws Exception{

		int knappVerdi = 0;
		int nedteller = 0;							// Brukes til å telle ned hvor ofte bilen kan forandre kjøreretning.
		final int hvorOfteKanBilenSnu = 300;		// Bilen kan snu hver 300ede gang while loopen kjører.
		boolean skalBilenRygge = false;
		boolean rygger = false;
		final long stoppeVarighet = 3000;			// Hvor lenge skal bilen stoppe naar den moeter en bil. (Oppgis i ms)
		final double lydTerskel = 0.9; 				// Hvor hoey lyd maa noe lage for at det skal gjenkjennes som en bil.
		final double fargeTerskel = 0.01;			// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
		final int motorHastighet = 200;
		final int vaskeMotorHastighet = 900;


		Brick brick = BrickFinder.getDefault();
		Port s1 = brick.getPort("S1"); 				// Fargesensor
		Port s2 = brick.getPort("S2"); 				// Lydsensor


		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();

		Motor.B.setSpeed(motorHastighet);
		Motor.C.setSpeed(-motorHastighet);
		Motor.A.setSpeed(vaskeMotorHastighet);

		/* Definerer en fargesensor og fargeAvleser */
		EV3ColorSensor fargesensor = new EV3ColorSensor(s1);	 	// ev3-fargesensor
		SampleProvider fargeLeser = fargesensor.getMode("RGB");  	//
		float[] fargeSample = new float[fargeLeser.sampleSize()];	//

		/* Definerer en lydsensor og lydAvleser */
		NXTSoundSensor lydsensor = new NXTSoundSensor(s2); 			// NXT-lydsensor
		SampleProvider lydLeser = lydsensor.getDBAMode(); 			//
		float[] lydSample = new float[lydLeser.sampleSize()]; 		//

		while(true){
			LCD.clear();
			lcd.drawString("Kjoerer...", 0,1);

			fargeLeser.fetchSample(fargeSample, 0);
			lydLeser.fetchSample(lydSample, 0);

			if(nedteller <= 0){
				if(fargeSample[0] < fargeTerskel){
					if(skalBilenRygge){
						skalBilenRygge = false;
					} else {
						skalBilenRygge = true;
					}
					nedteller = hvorOfteKanBilenSnu;
				}
			}
			nedteller--;

			if(lydSample[0] > lydTerskel){
				Motor.B.stop(true);
				Motor.C.stop(true);
				Motor.A.stop(true);
				Thread.sleep(stoppeVarighet);
			}

			if(skalBilenRygge){
				Motor.B.backward();
				Motor.C.backward();
			} else {
				Motor.B.forward();
				Motor.C.forward();
			}
			Motor.A.forward();

			knappVerdi = Button.readButtons();				//
			if(Integer.toString(knappVerdi).contains("2")){	//
				break;										//
			}												//Må inkluderes så kompileren ser at koden under kan kjøres.
		}
	fargesensor.close();	//Må inkluderes så sensorporter lukkes etter hver programkjøring.
	lydsensor.close();		//
	}
}
