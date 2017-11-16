// --- Kommentarer ---
// Bruk port A og B til motorene
//
//
//
// -------------------

// --- Enums ---
public Enums MotorStates {
    FLOAT, STOP;
}

// --- Imports ---
import lejos.hardware.motor.*;


class Movement {
// --- Attributer ---
    double xPos = 0; //Bruke rotasjon fra nullpunkt som x,y posisjon?
    double yPos = 0;
    int speed = 100;
    int resetSpeed = 50;

// --- Metoder ---
    public void gotoNull() { //Kjorer roboten til nullpunktet i et hjorne
        Motor.B.setSpeed(resetSpeed);
        Motor.A.setSpeed(resetSpeed);
        Motor.B.backward();
        Motor.A.backward();
        Delay.msDelay(1500);
        this.motorStop(MotorState.STOP);
    }

    public void goto(double degreeX, double degreeY) {
        xPos += degreeX; // <-- What the fuck, Sveinung? Adde en vinkel til en posisjon?
        yPos += degreeY;
        Motor.B.rotate(degreeX);
        Motor.A.rotate(degreeY);
    }

    public void setSpeed(int newSpeed) {
        speed = newSpeed;
    }

    public void motorStop(MotorState input) {
        switch(input) {
            case FLOAT:
                Motor.A.flt();
                Motor.B.flt();
                break;
            case STOP:
                Motor.A.stop();
                Motor.B.stop();
                break;
        }
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}