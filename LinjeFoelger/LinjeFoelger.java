/*	Bilen skal foelge en linje, maa optimaliseres saa den foelger linjen saa fort som mulig  */

import lejos.hardware.motor.*;					// Maa inkluderes saa motor kan styres
import lejos.hardware.lcd.*;					// Maa inkluderes saa lcd kan styres
import lejos.hardware.sensor.NXTColorSensor;	// Maa inkluderes saa fargesensor kan styres
//import lejos.hardware.sensor.NXTSoundSensor;	// Maa inkluderes saa lydsensor kan styres
import lejos.hardware.port.Port;			   	// Maa inkluderes saa porter kan styres
import lejos.hardware.Brick;					// Maa inkluderes saa EV3-klossen kan styres
import lejos.hardware.BrickFinder;				// Maa inkluderes saa EV3-klossen kan ses
import lejos.hardware.ev3.EV3;					// Maa inkluderes saa EV3-biblioteket kan brukes
import lejos.hardware.Keys;						// Maa inkluderes saa programmet kan lese input fra knappene paa EV3 klossen
import lejos.hardware.sensor.SensorModes;		// Gir tilgang til forskjellige moduser sensorene kan ha
import lejos.robotics.SampleProvider;			// Maa inkluderes for aa kunne hente informasjon fra sensorene
import lejos.hardware.sensor.*;					// Maa inkluderes saa sensorer kan brukes
import lejos.hardware.Device;					// Maa inkluderes saa programmet kan referere til EV3-klossen
import lejos.hardware.Button;					// Maa inkluderes saa EV3-biblioteket kan brukes
import javax.swing.Timer;						// Maa inkluderes saa man kan lage en timer og ta tid paa noe
												// Klassene maa inkluderes saa programmet kan ta bruk av metoder i klassene

/*
		Maa gjoeres

	Spoerre om banen svinger mot hoeyre eller venstre foer start, gjoeres for aa bestemme hvilke vei sweng metoden skal svinge 90 grader foer den "sweeper"
	Gjoeres ved at man ber brukeren, trykke paa pilknapp i retning av hvordan banen svinger.

		Kan gjoeres

	Lage en sprint metode, som gjoer at bilen kan kjoere ekstra fort der vi vet at det er rette strekninger.
	Gjoeres ved aa definere en variabel som

	Legge til en "kurv" med forskjellige svarte legodeler bak bilen, kurven roterer via en motor og slipper ut alt innholdet paa banen tidlig i racet. (Styrealgoritmen er immun mot svarte hindringer, men ikke hvit!)

	En "panikk" metode som aktiveres hvis svart ikke har blitt sett av noen av sensorene paa en stund, stopper bilen, snur den 90 grader og gjoer at den beveger seg frem og tilbake til S1 ser den svarte teipen.


*/

public class LinjeFoelger {





	public static void main(String[] args) throws Exception{

		Brick brick = BrickFinder.getDefault();
		Port s1 = brick.getPort("S1"); 				// Fargesensor1
		Port s2 = brick.getPort("S2"); 				// Fargesensor2

		/* Definerer en fargesensor og fargeAvleser */
		EV3ColorSensor fargesensorS1 = new EV3ColorSensor(s1);	 		// Fargesensor1
		SampleProvider fargeLeserS1 = fargesensorS1.getMode("RGB");  	//
		float[] fargeSampleS1 = new float[fargeLeserS1.sampleSize()];	//

		/* Definerer en fargesensor og fargeAvleser */
		EV3ColorSensor fargesensorS2 = new EV3ColorSensor(s2);	 		// Fargesensor2
		SampleProvider fargeLeserS2 = fargesensorS2.getMode("RGB");  	//
		float[] fargeSampleS2 = new float[fargeLeserS2.sampleSize()];	//


		// Klassevariabler
		final double fargeTerskelS1 = 1;			// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
		final double fargeTerskelS2 = 1; 			// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
		final int motorHastighet = 400;
		final int motorHastighetRevers = 200;
		final int rotasjonsHastighetSweep = 50;
		int knappVerdi = 0;

		boolean hoeyreEllerVenstre = false;				//Om = 1, gaar banen rundt til venstre, ellers hoeyre (Spoers hvilken vei i banen man kjoerer)

		Motor.A.setSpeed(motorHastighet);
		Motor.B.setSpeed(-motorHastighet);


		while(true){
			fargeLeserS1.fetchSample(fargeSampleS1, 0);	// Les av farge
			fargeLeserS2.fetchSample(fargeSampleS2, 0);	//

			if(verdiTerskelSammenlign(fargeSampleS1[0], true, fargeTerskelS1)){	// Sammenligner verdi og terskel og returnerer overUnder?true/false.
				Motor.A.stop(true);
				Motor.B.stop();

				//Rygg til  ser svart linje
				Motor.A.backward();
				Motor.B.backward();

				while(verdiTerskelSammenlign(fargeSampleS1[0], true, fargeTerskelS1)){
					fargeLeserS1.fetchSample(fargeSampleS1, 0);	// Les av farge

				}

				Motor.A.stop(true);
				Motor.B.stop(true);

			} else if(verdiTerskelSammenlign(fargeSampleS2[0], true, fargeTerskelS2)){	// Sammenligner verdi og terskel og returnerer overUnder?true/false.
			//SWEEP
			//SWEEP
		double tidStartAvSvartLinje = 0;
		double tidSluttAvSvartLinje = 0;
		double tidMellom = 0;
		boolean settSvartLinje = false;

		//Roter venstre/hoeyre 90 grader foer neste linje utfoeres
		if(hoeyreEllerVenstre == true){
			//Roter 90 grader til venstre
		} else {
			//Roter 90 grader til hoeyre
		}

		//Sett rotasjon til rotasjonsHastighetSweep
		Motor.A.setSpeed(rotasjonsHastighetSweep);
		Motor.B.setSpeed(-rotasjonsHastighetSweep);

		//start timer
		long startTid = System.nanoTime();


		if(hoeyreEllerVenstre == true){
			//Begynn rotasjon mot hoeyre
			Motor.A.forward();
			Motor.B.backward();
		} else {
			//Begynn rotasjon mot venstre
			Motor.A.backward();
			Motor.B.forward();
		}

		long a = System.nanoTime() - startTid;

		//tidStartAvSvartLinje = tiden naar svart linje sees
		while(verdiTerskelSammenlign(fargeSampleS2[0], true, fargeTerskelS2)){ // Sammenligner verdi og terskel og returnerer overUnder?true/false.
			fargeLeserS2.fetchSample(fargeSampleS2, 0);
		}


		//tidSluttAvSvartLinje = tiden naar ikke svart linje sees, etter svart linje har blitt sett
		long b = System.nanoTime() - startTid;
		//Regn ut tidMellom
		long c = (b - a) / 2;
		//Roter sakte tilbake i (Naaverende tid - tidMellom) sekunder

		b = System.nanoTime();
		if(hoeyreEllerVenstre == true){
			while((System.nanoTime()) < (c + b)){
				//Begynn rotasjon mot hoeyre
				Motor.A.forward();
				Motor.B.backward();
			}
		} else {
			while((System.nanoTime()) < (c + b)){
				//Begynn rotasjon mot venstre
				Motor.A.backward();
				Motor.B.forward();
			}
		}


		//Tilbakestill motorhastighet til motorhastighet
		Motor.A.setSpeed(motorHastighet);
		Motor.B.setSpeed(-motorHastighet);

			//SWEEP
			//SWEEP

			} else {
				Motor.A.forward();
				Motor.B.forward();
			}

			knappVerdi = Button.readButtons();				//
			if(Integer.toString(knappVerdi).contains("2")){	//
				break;										//
			}

		}




	fargesensorS1.close();	//Maa inkluderes saa sensorporter lukkes etter hver programkjoering.
	fargesensorS2.close();	//
	}



	public static boolean verdiTerskelSammenlign(double verdi, boolean overUnder, double terskelSomSkalBrukes){ // Sammenligner verdi og terskel og returnerer overUnder?true/false.
		if(overUnder = true){
			if((verdi * 100) > terskelSomSkalBrukes){
				return true;
			} else {
				return false;
			}
		} else {
			if((verdi * 100) < terskelSomSkalBrukes){
				return true;
			} else {
				return false;
			}
		}
	}
}
