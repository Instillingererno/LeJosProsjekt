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

/* -----------------------------------------------------------------------------

	Trenger og:
	-Rebound så hvis den må svinge skarpt må den og svinge skarpt når den
	treffer den svarte streken for å balansere seg selv

------------------------------------------------------------------------------*/


class Main {

	public static void main(String[] args) {

		Move unit = new Move(); // Init move object
		Think brain = new Think();
		brain.start();
		int buttons = 0; // Knapper som er trykket
		boolean cont = true; //Fortsett eller ikke

		while(cont) {
			buttons = Button.readButtons();
			if(Integer.toString(buttons).contains("2")) {
				cont = false;
			}
			brain.newStartTime();
			unit.forward();

		}
		unit.exit();
	}
}
class Init { // Initializing

	//Variabler
	final double lightFloorS1 = 0.32;	//0.50			// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
	final double colorFloorS4 = 0.02; //0.15			// Hvor lav RGB verdi maa bakken vaere for at det skal gjenkjennes som svart.
	float forwardSpeed = 100;
	float turnDelta = 0;
	int multiplier = 1;

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

	public void exit() {
		lysSensorS1.close();	//Maa inkluderes saa sensorporter lukkes etter hver programkjoering.
		fargesensorS4.close();	//
	}
	public void setTurnDelta(float delta) {
		turnDelta = delta;
	}
}

class Move extends Init { // Movement
	public void forward() {
		Motor.A.setSpeed(forwardSpeed + turnDelta);
		Motor.B.setSpeed(-(forwardSpeed + turnDelta));
		Motor.A.forward();
		Motor.B.forward();
		fargeLeserS4.fetchSample(fargeSampleS4, 0);
		if(fargeLeserS4[0] < 0.7) {
			unit.setTurnDelta();
		}
	}
}

class Think extends Thread { // Behaviour // Til aa gjoere matte and stuff
	public float turnDelta = 0;
	long startTime = 0; // System.nanoTime();
	long deltaTime = 0;

	public void run() {
		startTime = System.nanoTime();
		while(true) {
			deltaTime = startTime - System.nanoTime();
			turnDelta = (deltaTime * deltaTime) / 10000000;
		}
	}

	public void newStartTime() {
		startTime = System.nanoTime();
	}
	public float getTurnDelta() {
		return turnDelta;
	}
}