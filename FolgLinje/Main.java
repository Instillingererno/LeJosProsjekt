import lejos.hardware.motor.*;					// Maa inkluderes saa motor kan styres
import lejos.hardware.lcd.*;					// Maa inkluderes saa lcd kan styres
//import lejos.hardware.sensor.NXTColorSensor;	// Maa inkluderes saa fargesensor kan styres
//import lejos.hardware.sensor.NXTSoundSensor;	// Maa inkluderes saa lydsensor kan styres
//import lejos.hardware.sensor.NXTLightSensor;
import lejos.hardware.port.Port;			   	// Maa inkluderes saa porter kan styres
import lejos.hardware.Brick;					// Maa inkluderes saa EV3-klossen kan styres
import lejos.hardware.BrickFinder;				// Maa inkluderes saa EV3-klossen kan ses
import lejos.hardware.ev3.EV3;					// Maa inkluderes saa EV3-biblioteket kan brukes
//import lejos.hardware.Keys;						// Maa inkluderes saa programmet kan lese input fra knappene paa EV3 klossen
//import lejos.hardware.sensor.SensorModes;		// Gir tilgang til forskjellige moduser sensorene kan ha
import lejos.robotics.SampleProvider;			// Maa inkluderes for aa kunne hente informasjon fra sensorene
import lejos.hardware.sensor.*;					// Maa inkluderes saa sensorer kan brukes
//import lejos.hardware.Device;					// Maa inkluderes saa programmet kan referere til EV3-klossen
import lejos.hardware.Button;					// Maa inkluderes saa EV3-biblioteket kan brukes
												// Klassene maa inkluderes saa programmet kan ta bruk av metoder i klassene

/* -----------------------------------------------------------------------------

	Trenger og:
	-Rebound saa hvis den maa svinge skarpt maa den og svinge skarpt naar den
	treffer den svarte streken for aa balansere seg selv

------------------------------------------------------------------------------*/


class main {

	public static void main(String[] args) throws Exception {

		int buttons = 0; // Knapper som er trykket
		boolean cont = true; //Fortsett eller ikke

		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();

		//INIT
		//Variabler
		final double lightFloorS1 = 0.47;	//0.50			// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
		final double colorFloorS4 = 0.05; //0.15			// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
		float backwardSpeed = 450;

		Brick brick = BrickFinder.getDefault();
		Port s1 = brick.getPort("S1"); 				// LysSensor
		Port s4 = brick.getPort("S4"); 				// Fargesensor

		/* Definerer en lyssensor og lysAvleser */
		NXTLightSensor lysSensorS1 = new NXTLightSensor(s1);
		SampleProvider lysLeserS1 = lysSensorS1.getRedMode();
		float[] lysSampleS1 = new float[lysLeserS1.sampleSize()];

		/* Definerer en fargesensor og fargeAvleser */
		EV3ColorSensor fargesensorS4 = new EV3ColorSensor(s4);	 		// Fargesensor2
		SampleProvider fargeLeserS4 = fargesensorS4.getMode("RGB");  	//
		float[] fargeSampleS4 = new float[fargeLeserS4.sampleSize()];	//

		//MOVE
		float turnDelta = 1;
		long startTime = System.nanoTime(); // System.nanoTime();
		long deltaTime = 0;
		int teller = 0;
		int multiplier = 1;
		boolean svart = false;
		int tellerPluss = 1;

		while(cont) {
			buttons = Button.readButtons();
			if(Integer.toString(buttons).equals("2")) {
				cont = false;
			} else if (Integer.toString(buttons).equals("1")) {
				backwardSpeed += 5;
			} else if (Integer.toString(buttons).equals("4")) {
				backwardSpeed -= 5;
			} else if (Integer.toString(buttons).equals("8")) {
				tellerPluss += 1;
			} else if (Integer.toString(buttons).equals("16")) {
				tellerPluss -= 1;
			}
			lcd.drawString("Speed: " + backwardSpeed, 0, 1);
			lcd.drawString("Teller plus: " + tellerPluss, 0, 2);
			lcd.drawString("Colorfloor: " + fargeSampleS4[0], 0, 3);
			lcd.drawString("Lightfloor: " + lysSampleS1[0], 0, 4);

			Motor.C.setSpeed(backwardSpeed + turnDelta * multiplier);
			Motor.D.setSpeed(backwardSpeed - turnDelta * multiplier);
			Motor.C.backward();
			Motor.D.backward();
			fargeLeserS4.fetchSample(fargeSampleS4, 0);
			lysLeserS1.fetchSample(lysSampleS1, 0);
			if(fargeSampleS4[0] < colorFloorS4) {
				multiplier = 1 + teller;
				teller += 1;
			}
			if(lysSampleS1[0] < lightFloorS1) {
				multiplier = -1 - teller*teller;
				teller += tellerPluss;
			}
			if(fargeSampleS4[0] > colorFloorS4 && lysSampleS1[0] > lightFloorS1) {
				teller = 0;
				if(multiplier < 0) {
					multiplier = -1;
				} else {
					multiplier = 1;
				}
			}
		}
		lysSensorS1.close();	//Maa inkluderes saa sensorporter lukkes etter hver programkjoering.
		fargesensorS4.close();
	}
}
