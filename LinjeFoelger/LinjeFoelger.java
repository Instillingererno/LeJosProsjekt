/*	Bilen skal foelge en linje, maa optimaliseres saa den foelger linjen saa fort som mulig  */

import lejos.hardware.motor.*;					// Maa inkluderes saa motor kan styres
import lejos.hardware.lcd.*;					// Maa inkluderes saa lcd kan styres
import lejos.hardware.sensor.NXTColorSensor;	// Maa inkluderes saa fargesensor kan styres
//import lejos.hardware.sensor.NXTSoundSensor;	// Maa inkluderes saa lydsensor kan styres
import lejos.hardware.sensor.NXTLightSensor;
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
		Port s1 = brick.getPort("S1"); 				// LysSensor
		Port s4 = brick.getPort("S4"); 				// Fargesensor

		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();

		/* Definerer en lyssensor og lysAvleser */
		NXTLightSensor lysSensorS1 = new NXTLightSensor(s1);
		SampleProvider lysLeserS1 = lysSensorS1.getRedMode();
		float[] lysSampleS1 = new float[lysLeserS1.sampleSize()];

		/* Definerer en fargesensor og fargeAvleser */
		EV3ColorSensor fargesensorS4 = new EV3ColorSensor(s4);	 		// Fargesensor2
		SampleProvider fargeLeserS4 = fargesensorS4.getMode("RGB");  	//
		float[] fargeSampleS4 = new float[fargeLeserS4.sampleSize()];	//

		// Klassevariabler
		final double lysTerskelS1 = 0.4;	//0.50			// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
		final double fargeTerskelS4 = 0.04; //0.15			// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
		final int motorHastighet = 900;
		final int motorHastighetRevers = 400;
		final int rotasjonsHastighetSweep = 60;
		final int motorHastighetFoersteSwing = 300;
		int knappVerdi = 0;
		long tidAASvinge = 300000000;						// Nanosekunder

		boolean venstreEllerHoeyre = true;				//Om = 1, gaar banen rundt til venstre, ellers hoeyre (Spoers hvilken vei i banen man kjoerer)

		Motor.C.setSpeed(-motorHastighet);
		Motor.D.setSpeed(motorHastighet);


		while(true){
			lysLeserS1.fetchSample(lysSampleS1, 0);	// Les av farge
			lcd.drawString("Lys/S1: " + lysSampleS1[0], 0,1);
			lcd.drawString("lys/S1: " + verdiTerskelSammenlign(lysSampleS1[0], true, lysTerskelS1), 0,2);

			fargeLeserS4.fetchSample(fargeSampleS4, 0);	//
			lcd.drawString("Farge/S4: " + fargeSampleS4[0], 0,3);
			lcd.drawString("Farge/S4:: " + verdiTerskelSammenlign(fargeSampleS4[0], true, fargeTerskelS4), 0,4);

			Motor.C.backward();
			Motor.D.backward();





			if(verdiTerskelSammenlign(lysSampleS1[0], false, lysTerskelS1)){	// Sammenligner verdi og terskel og returnerer underOver?true/false.
				Motor.C.stop(true);
				Motor.D.stop();

				Motor.C.setSpeed(motorHastighetRevers);
				Motor.D.setSpeed(-motorHastighetRevers);


				//Rygg til  ser svart linje
				Motor.C.forward();
				Motor.D.forward();

				while(verdiTerskelSammenlign(lysSampleS1[0], false, lysTerskelS1)){
					lysLeserS1.fetchSample(lysSampleS1, 0);	// Les av farge
					lcd.drawString("Lys/S1: " + lysSampleS1[0], 0,1);
					lcd.drawString("lys/S1: " + verdiTerskelSammenlign(lysSampleS1[0], true, lysTerskelS1), 0,2);

					fargeLeserS4.fetchSample(fargeSampleS4, 0);	//
					lcd.drawString("Farge/S4: " + fargeSampleS4[0], 0,3);
					lcd.drawString("Farge/S4:: " + verdiTerskelSammenlign(fargeSampleS4[0], true, fargeTerskelS4), 0,4);
				}

				Motor.C.stop(true);
				Motor.D.stop();

				Motor.C.setSpeed(motorHastighet);
				Motor.D.setSpeed(-motorHastighet);

			} else if(verdiTerskelSammenlign(fargeSampleS4[0], false, fargeTerskelS4)){	// Sammenligner verdi og terskel og returnerer underOver?true/false.
			//SWEEP
			//SWEEP

				long tidStartAvSvartLinje = 0;
				long tidSluttAvSvartLinje = 0;
				long tidMellom = 0;
				long startTid = 0;
				boolean settSvartLinje = false;

				Motor.C.setSpeed(motorHastighetFoersteSwing);
				Motor.D.setSpeed(-motorHastighetFoersteSwing);

				//Roter venstre/hoeyre 90 grader foer neste linje utfoeres
				startTid = System.nanoTime();

				C.synchronizeWith(new RegulatedMotor[]{D});
				C.startSynchronization();

				while(System.nanoTime() < startTid + tidAASvinge){
					if(venstreEllerHoeyre == true){
						//Roter 90 grader til venstre
						Motor.C.forward();
						Motor.D.backward();

					} else {
						//Roter 90 grader til hoeyre
						Motor.C.backward();
						Motor.D.forward();
					}
				}

				left.endSynchronization();

				//Sett fart til rotasjonsHastighetSweep
				Motor.C.setSpeed(rotasjonsHastighetSweep);
				Motor.D.setSpeed(-rotasjonsHastighetSweep);

				//start timer
				startTid = System.nanoTime();

				if(venstreEllerHoeyre == true){
					//Begynn rotasjon mot hoeyre
					Motor.C.backward();
					Motor.D.forward();
				} else {
					//Begynn rotasjon mot venstre
					Motor.C.forward();
					Motor.D.backward();
				}

				//tidStartAvSvartLinje = tiden naar svart linje sees
				while(verdiTerskelSammenlign(fargeSampleS4[0], false, fargeTerskelS4)){ // Sammenligner verdi og terskel og returnerer underOver?true/false.
			lysLeserS1.fetchSample(lysSampleS1, 0);	// Les av farge
			lcd.drawString("Lys/S1: " + lysSampleS1[0], 0,1);
			lcd.drawString("lys/S1: " + verdiTerskelSammenlign(lysSampleS1[0], true, lysTerskelS1), 0,2);

			fargeLeserS4.fetchSample(fargeSampleS4, 0);	//
			lcd.drawString("Farge/S4: " + fargeSampleS4[0], 0,3);
			lcd.drawString("Farge/S4:: " + verdiTerskelSammenlign(fargeSampleS4[0], true, fargeTerskelS4), 0,4);
				}

				tidStartAvSvartLinje = System.nanoTime();

				while(verdiTerskelSammenlign(fargeSampleS4[0], true, fargeTerskelS4)){ // Sammenligner verdi og terskel og returnerer underOver?true/false.
			lysLeserS1.fetchSample(lysSampleS1, 0);	// Les av farge
			lcd.drawString("Lys/S1: " + lysSampleS1[0], 0,1);
			lcd.drawString("lys/S1: " + verdiTerskelSammenlign(lysSampleS1[0], true, lysTerskelS1), 0,2);

			fargeLeserS4.fetchSample(fargeSampleS4, 0);	//
			lcd.drawString("Farge/S4: " + fargeSampleS4[0], 0,3);
			lcd.drawString("Farge/S4:: " + verdiTerskelSammenlign(fargeSampleS4[0], true, fargeTerskelS4), 0,4);
				}

				tidSluttAvSvartLinje = System.nanoTime();

				//tidSluttAvSvartLinje = tiden naar ikke svart linje sees, etter svart linje har blitt sett
				//Regn ut tidMellom
				tidMellom = (tidSluttAvSvartLinje - tidStartAvSvartLinje) / 2;
				//Roter sakte tilbake i (Naaverende tid - tidMellom) sekunder



				startTid = System.nanoTime();
				if(venstreEllerHoeyre == true){

					Motor.C.forward();
					Motor.D.backward();

					Thread.sleep(tidMellom / 1000000);
				} else {
					Motor.C.backward();
					Motor.D.forward();

					Thread.sleep(tidMellom / 1000000);
				}



				Motor.C.stop(true);
				Motor.D.stop();




				//Tilbakestill motorhastighet til motorhastighet
				Motor.C.setSpeed(motorHastighet);
				Motor.D.setSpeed(-motorHastighet);

			//SWEEP
			//SWEEP

			} else {
				Motor.C.setSpeed(motorHastighet);
				Motor.D.setSpeed(-motorHastighet);
				Motor.C.backward();
				Motor.D.backward();
			}

			knappVerdi = Button.readButtons();				//
			if(Integer.toString(knappVerdi).contains("2")){	//
				break;										//
			}

		}




	lysSensorS1.close();	//Maa inkluderes saa sensorporter lukkes etter hver programkjoering.
	fargesensorS4.close();	//
	}



	public static boolean verdiTerskelSammenlign(double verdi, boolean underOver, double terskelSomSkalBrukes){ // Sammenligner verdi og terskel og returnerer underOver?true/false.
		if(underOver == true){
			if((verdi) < terskelSomSkalBrukes){
				return true;
			} else {
				return false;
			}
		} else {
			if((verdi) > terskelSomSkalBrukes){
				return true;
			} else {
				return false;
			}
		}
	}
}
