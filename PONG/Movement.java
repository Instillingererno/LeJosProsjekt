//Blir denne unødvendig med koordinat klassen?
class Movement {
// --- Attributer ---
    double xPos = 0; //Bruke rotasjon fra nullpunkt som x,y posisjon?
    double yPos = 0;
    double angle = 0;
    int speed = 0;

// --- Metoder ---
    public void newAngle(double newAngle) {
        this.angle = newAngle;
    }

    public void setSpeed(int newSpeed) {
        this.speed = newSpeed;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
