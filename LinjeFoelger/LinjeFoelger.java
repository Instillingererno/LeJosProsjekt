/*	Bilen skal følge en linje, må optimaliseres så den følger linjen så fort som mulig  */

import lejos.hardware.motor.*;					// Må inkluderes så motor kan styres
import lejos.hardware.lcd.*;					// Må inkluderes så lcd kan styres
import lejos.hardware.sensor.NXTColorSensor;	// Må inkluderes så fargesensor kan styres
//import lejos.hardware.sensor.NXTSoundSensor;	// Må inkluderes så lydsensor kan styres
import lejos.hardware.port.Port;			   	// Må inkluderes så porter kan styres
import lejos.hardware.Brick;					// Må inkluderes så EV3-klossen kan styres
import lejos.hardware.BrickFinder;				// Må inkluderes så EV3-klossen kan ses
import lejos.hardware.ev3.EV3;					// Må inkluderes så EV3-biblioteket kan brukes
//import lejos.hardware.Keys;					// Må inkluderes så programmet kan lese input fra knappene på EV3 klossen
import lejos.hardware.sensor.SensorModes;		// Gir tilgang til forskjellige moduser sensorene kan ha
import lejos.robotics.SampleProvider;			// Må inkluderes for å kunne hente informasjon fra sensorene
import lejos.hardware.sensor.*;					// Må inkluderes så sensorer kan brukes
import lejos.hardware.Device;					// Må inkluderes så programmet kan referere til EV3-klossen
//import lejos.hardware.Button;					// Må inkluderes så EV3-biblioteket kan brukes
import javax.swing.Timer;						// Må inkluderes så man kan lage en timer og ta tid på noe
												// Klassene må inkluderes så programmet kan ta bruk av metoder i klassene

/*
		Må gjøres

	Spørre om banen svinger mot høyre eller venstre før start, gjøres for å bestemme hvilke vei sweng metoden skal svinge 90 grader før den "sweeper"
	Gjøres ved at man ber brukeren, trykke på pilknapp i retning av hvordan banen svinger.

		Kan gjøres

	Lage en sprint metode, som gjør at bilen kan kjøre ekstra fort der vi vet at det er rette strekninger.
	Gjøres ved å definere en variabel som

	Legge til en "kurv" med forskjellige svarte legodeler bak bilen, kurven roterer via en motor og slipper ut alt innholdet på banen tidlig i racet. (Styrealgoritmen er immun mot svarte hindringer, men ikke hvit!)

	En "panikk" metode som aktiveres hvis svart ikke har blitt sett av noen av sensorene på en stund, stopper bilen, snur den 90 grader og gjør at den beveger seg frem og tilbake til S1 ser den svarte teipen.


*/

public class LinjeFoelger {

		// Klassevariabler
		final double fargeTerskelS1 = 1;			// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
		final double fargeTerskelS2 = 1; 			// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
		final int motorHastighet = 400;
		final int rotasjonsHastighetSwing = 100;
		final int motorHastighetRevers = 200;
		final int rotasjonsHastighetSwing = 50;

		boolean høyreEllerVenstre = 0;				//Om = 1, går banen rundt til venstre, ellers høyre (Spørs hvilken vei i banen man kjører)
		// Klassevariabler


	public static void main(String[] args) throws Exception{

		Brick brick = BrickFinder.getDefault();
		Port s1 = brick.getPort("S1"); 				// Fargesensor1
		Port s2 = brick.getPort("S2"); 				// Fargesensor2

		Motor.A.setSpeed(motorHastighet);
		Motor.B.setSpeed(-motorHastighet);

		/* Definerer en fargesensor og fargeAvleser */
		EV3ColorSensor fargesensorS1 = new EV3ColorSensor(s1);	 		// Fargesensor1
		SampleProvider fargeLeserS1 = fargesensorS1.getMode("RGB");  	//
		float[] fargeSampleS1 = new float[fargeLeserS1.sampleSize()];	//

		/* Definerer en fargesensor og fargeAvleser */
		EV3ColorSensor fargesensorS2 = new EV3ColorSensor(s2);	 		// Fargesensor2
		SampleProvider fargeLeserS2 = fargesensorS2.getMode("RGB");  	//
		float[] fargeSampleS2 = new float[fargeLeserS2.sampleSize()];	//


		while(true){
			fargeLeserS1.fetchSample(fargeSampleS1, 0);	// Les av farge
			fargeLeserS2.fetchSample(fargeSampleS2, 0);	//

			if(verdiTerskelSammenlign(fargeSampleS1[0], true, fargeTerskelS1)){	// Sammenligner verdi og terskel og returnerer overUnder?true/false.
				Motor.A.Stop(true);
				Motor.B.Stop();

				//Rygg til  ser svart linje
				Motor.A.Backward();
				Motor.B.Backward();

				while(verdiTerskelSammenlign(fargeSampleS1[0], true, fargeTerskelS1)){
					fargeLeserS1.fetchSample(fargeSampleS1, 0);	// Les av farge

				}

				Motor.A.Stop(true);
				Motor.B.Stop(true);

			} else if(verdiTerskelSammenlign(fargeSampleS2[0], true, fargeTerskelS2)){	// Sammenligner verdi og terskel og returnerer overUnder?true/false.
				sweep();
			} else {
				Motor.A.Forward();
				Motor.B.Forward();
			}
		}


	/*
		Bilen

		2x fargesensor eller 1x fargesensor og 1x lyssensor
		sSenter	// fargesensor som peker ned på bakken, står mellom hjulene slik at posisjonen til sensoren ikke forandres ved rotasjon.
		sForan  // farge/lyssensor som peker ned på bakken, står foran bilen og brukes for styring.




	*/



	/*
		Pseudo kode

		while(true){  // Kjør så lenge sensorene ser svar, hvis ikke; utfør funksjoner som søker etter svart linje for sensor det gjelder.
					  // Hvis rett etter kjøring, sSenter ikke ser svart linje, rygg til sSenter kan se svart linje.

			StoppÅKjøre;

			if(erVerdiOverTerskel(sForan)){
				sweep();	Roter til svart linje sees av sForan
			}

			if(erVerdiOverTerskel(sSenter)){
				finnLinje();
			}

			while(erVerdiUnderTerskel(sForan) && erVerdiUnderTerskel(sSenter)){ // Kjør til en av sensorene ikke ser linjen
				Kjør;
			}
				StoppÅKjøre;

			if(erVerdiOverTerskel(sSenter)){
				RyggTilLinjeKanSees;
		 	}


		}


		funksjon sweep (){
			while(true){
				int a;
				int b;
				int c;

				Roter 90 grader;
				Start timer.
				Beveg sakte mot høyre til linje kan sees og skriv tid inn i int a;
				Fortsett bevegelse til linjen ikke kan sees og skriv in tid inn i int b;
				Skriv tiden inn i c som er mellom tid a og b. F.eks og a = 1 og b = 3, er tiden c = 2;
				Roter tilbake til rotasjonen bilen hadde i tid c;
				Bekreft at sForan ser svart linje, om ikke, gjenta while loop.
				Om sForan ser svart linje; break;
			}
		}




		funksjon erVerdiUnder/OverTerskel (int verdi, boolean overUnder, int svartTerskelForIndividuellSensor)	//boolean overUnder, Over = true Under = false
			if(verdi <= svartTerskelForIndividuellSensor)
				return 1
			else {
				return 0
			}
		}
	*/




	fargesensorS1.close();	//Må inkluderes så sensorporter lukkes etter hver programkjøring.
	fargesensorS2.close();	//
	}

	public static void sweep(boolean høyreEllerVenstre){		// høyreEllerVenstre = 1, Går banen mot høyre eller venstre i forhold til startposisjon
		double tidStartAvSvartLinje = 0;
		double tidSluttAvSvartLinje = 0;
		double tidMellom = 0;
		boolean settSvartLinje = false;

		//Roter venstre/høyre 90 grader før neste linje utføres
		if(høyreEllerVenstre == true){
			//Roter 90 grader til venstre
		} else {
			//Roter 90 grader til høyre
		}

		//Sett rotasjon til rotasjonsHastighetSweep
		Motor.A.setSpeed(rotasjonsHastighetSweep);
		Motor.B.setSpeed(-rotasjonsHastighetSweep);

		//start timer
		long startTid = System.nanoTime();


		if(høyreEllerVenstre == true){
			//Begynn rotasjon mot høyre
			Motor.A.Forward();
			Motor.B.Backward();
		} else {
			//Begynn rotasjon mot venstre
			Motor.A.Backward();
			Motor.B.Forward();
		}

		long a = System.nanoTime - startTid;

		//tidStartAvSvartLinje = tiden når svart linje sees
		while(verdiTerskelSammenlign(fargeSampleS2[0], true, fargeTerskelS2)){ // Sammenligner verdi og terskel og returnerer overUnder?true/false.
			fargeLeserS2.fetchSample(fargeSampleS2, 0);
		}


		//tidSluttAvSvartLinje = tiden når ikke svart linje sees, etter svart linje har blitt sett
		long b = System.nanoTime - startTid;
		//Regn ut tidMellom
		long c = (b - a) / 2;
		//Roter sakte tilbake i (Nåverende tid - tidMellom) sekunder

		b = System.nanotime;
		if(høyreEllerVenstre == true){
			while((System.nanotime) < (c + b)){
				//Begynn rotasjon mot høyre
				Motor.A.Forward();
				Motor.B.Backward();
			}
		} else {
			while((System.nanotime) < (c + b)){
				//Begynn rotasjon mot venstre
				Motor.A.Backward();
				Motor.B.Forward();
			}
		}


		//Tilbakestill motorhastighet til motorhastighet
		Motor.A.setSpeed(motorHastighet);
		Motor.B.setSpeed(-motorHastighet);

			//Om svart linje faktisk ikke sees, pga feil, vil hovedwhileloop gjenta sweep;

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
