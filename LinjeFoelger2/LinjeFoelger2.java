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

	SvingeAkselerasjon må være større i retningen banen går.

	En metode som ser på hvor ofte bilen svinger, jo større frekvensNaa av antall svingninger per tid, jo tregere fart.
		Kan gjoeres

	En "panikk" metode som aktiveres hvis svart ikke har blitt sett av noen av sensorene paa en stund, stopper bilen, snur den 90 grader og gjoer at den beveger seg frem og tilbake til S1 ser den svarte teipen.


*/

public class LinjeFoelger2 {

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
		final double fargeTerskelS4 = 0.01; //0.15			// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
		final int motorHastighetMin = 400;
		double motorHastighet = 0;
		final int motorHastighetMax = 900;
		final double motorHastighetAkselerasjon = 0.3;
		final int tidenDetTarÅPassereKryss = 200; 			//ms
		final double svingeAkselerasjon = 0.005;				// 1 %
		final double svingeAkselerasjonBaneRetning = 0.01;				// 2 %
		final double svingeForholdBegynnelse = 1.01;
		final double svingeForholdBegynnelseBaneRetning = 1.05;
		final double SvingeForholdMax = 10.0;				//En motor kan bare kjøre så lite som 1/10 så fort som den andre

		double sisteTid = 0;
		double sisteFrekvens = 0;
		double frekvensNaa = 0;
		double frekvens = 0;

		int antallJusteringer = 0;

		int knappVerdi = 0;
		double svingeForhold = 1.0; 						//1 = rett frem

		boolean venstreEllerHoeyre = true;					//om = true, sving til venstre

		boolean gaarBanenMotVenstreEllerHoeyre = true;		//Om = true, gaar banen rundt til venstre, ellers hoeyre (Spoers hvilken vei i banen man kjoerer)


		//long tidAASvinge = 300000000; //0.3 sek			// Nanosekunder


		motorHastighet = motorHastighetMin;


		while(true){
			lysLeserS1.fetchSample(lysSampleS1, 0);	// Les av farge
			lcd.drawString("Lys/S1: " + lysSampleS1[0], 0,1);
			lcd.drawString("lys/S1: " + verdiTerskelSammenlign(lysSampleS1[0], true, lysTerskelS1), 0,2);

			fargeLeserS4.fetchSample(fargeSampleS4, 0);	//
			lcd.drawString("Farge/S4: " + fargeSampleS4[0], 0,3);
			lcd.drawString("Farge/S4:: " + verdiTerskelSammenlign(fargeSampleS4[0], true, fargeTerskelS4), 0,4);

			// Oppdater frekvens
				sisteTid = System.nanoTime() * 1000000000;
				frekvensNaa = antallJusteringer / sisteTid;
				if(sisteTid > 1){
					sisteFrekvens = frekvensNaa;
					sisteTid = 0;
					frekvensNaa = 0;
					antallJusteringer = 0;
				}
				frekvens = (frekvensNaa + sisteFrekvens)/2;




			// Oppdater svingeforhold
			svingeForhold = oppdaterSvingeForhold(venstreEllerHoeyre, gaarBanenMotVenstreEllerHoeyre, svingeAkselerasjon, svingeAkselerasjonBaneRetning, svingeForhold);

			// Oppdater hastighet
			if(motorHastighet < motorHastighetMax){
				motorHastighet += motorHastighetAkselerasjon;
			}

			// Oppdater sving
			if(venstreEllerHoeyre){
				Motor.C.setSpeed((int)(motorHastighet * (1/svingeForhold)));
				Motor.D.setSpeed((int)-motorHastighet);					//Venstremotor er raskest, hoeyremotor tilpasser seg
				lcd.drawString("HastighetC: " + (int)(motorHastighet * (1/svingeForhold)), 0,5);
				lcd.drawString("HastighetD: " + motorHastighet, 0,6);
			} else{
				Motor.C.setSpeed((int)motorHastighet);					//Hoeyremotor er raskest, venstremotor tilpasser seg
				Motor.D.setSpeed((int)(-motorHastighet * (1/svingeForhold)));
				lcd.drawString("HastighetC: " + motorHastighet, 0,5);
				lcd.drawString("HastighetD: " + (int)(motorHastighet * (1/svingeForhold)), 0,6);
			}

			Motor.C.backward();
			Motor.D.backward();

			if(verdiTerskelSammenlign(lysSampleS1[0], true, lysTerskelS1) || verdiTerskelSammenlign(fargeSampleS4[0], true, fargeTerskelS4)){
				if(venstreEllerHoeyre){
					if(verdiTerskelSammenlign(fargeSampleS4[0], true, fargeTerskelS4)){
						if(gaarBanenMotVenstreEllerHoeyre){
							svingeForhold = svingeForholdBegynnelseBaneRetning;
							venstreEllerHoeyre = false;
						} else{
							svingeForhold = svingeForholdBegynnelse;
							venstreEllerHoeyre = false;
						}
						antallJusteringer += 1;
					}
				} else{
					if(verdiTerskelSammenlign(lysSampleS1[0], true, lysTerskelS1)){
						if(gaarBanenMotVenstreEllerHoeyre){
							svingeForhold = svingeForholdBegynnelse;
							venstreEllerHoeyre = true;
						} else{
							svingeForhold = svingeForholdBegynnelseBaneRetning;
							venstreEllerHoeyre = true;
						}
						antallJusteringer += 1;
					}
				}
			}



			knappVerdi = Button.readButtons();				//
			if(Integer.toString(knappVerdi).contains("2")){	//
				break;										//
			}
		}

			/*

				om høyre treffer svart strek, hør bare etter om venstre treffer og omvendt.

				while{
					oppdaterSensorer;
					oppdaterMotorHastighet;

					hvis venstreEllerHoeyre
						Motor.A.setSpeed(motorfart * (1/svingeForhold));
						Motor.B.setSpeed(motorfart);					//Venstremotor er raskest, hoeyremotor tilpasser seg
					else
						Motor.A.setSpeed(motorfart);					//Hoeyremotor er raskest, venstremotor tilpasser seg
						Motor.B.setSpeed(motorfart * (1/svingeForhold));




					Øk svingeForhold med svingAkselerasjon;

					hvis !(venstresensor && hoeyresensor)
						hvis venstreEllerHoeyre
							Hvis høyresensor
								hvis gaarBanenMotVenstreEllerHoeyre = true
									svingeForhold = svingeForholdBegynnelseBaneRetning
									venstreEllerHoeyre = false;
								else
									svingeForhold = svingeForholdBegynnelse
									venstreEllerHoeyre = false;

						else
							Hvis venstresensor
								hvis gaarBanenMotVenstreEllerHoeyre = true
									svingeForhold = svingeForholdBegynnelse;
									venstreEllerHoeyre = true;
								else
									svingeForhold = svingeForholdBegynnelseBaneRetning;
									venstreEllerHoeyre = true;

					else
						kjør rett frem;
						thread.pause(tidenDetTarÅPassereKryss);




				}


			*/





	lysSensorS1.close();	//Maa inkluderes saa sensorporter lukkes etter hver programkjoering.
	fargesensorS4.close();	//
	}

	public static double oppdaterSvingeForhold(boolean venstreEllerHoeyre, boolean gaarBanenMotVenstreEllerHoeyre, double svingeAkselerasjon, double svingeAkselerasjonBaneRetning, double svingeForhold){
		if(venstreEllerHoeyre){
			if(gaarBanenMotVenstreEllerHoeyre){
				svingeForhold += svingeAkselerasjonBaneRetning;
			} else{
				svingeForhold += svingeAkselerasjon;
			}
		} else{
			if(gaarBanenMotVenstreEllerHoeyre){
				svingeForhold += svingeAkselerasjon;
			} else{
				svingeForhold += svingeAkselerasjonBaneRetning;
			}
		}

		return svingeForhold;
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
