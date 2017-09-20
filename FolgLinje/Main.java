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


class Main {

	public static void main(String[] args) {



	}

}
class Init { // Initializing

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

	public exit() {
		lysSensorS1.close();	//Maa inkluderes saa sensorporter lukkes etter hver programkjoering.
		fargesensorS4.close();	//
	}

}

class Move extends Init { // Movement

}

class Think extends Init { // Behaviour

}

