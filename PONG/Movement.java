
class Movement {
// --- Attributer ---
    double xPos = 0; //Bruke rotasjon fra nullpunkt som x,y posisjon?
    double yPos = 0;
    int speed = 0;

// --- Metoder ---
    public gotoNull() { //Kjorer roboten til nullpunktet i et hjorne

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
