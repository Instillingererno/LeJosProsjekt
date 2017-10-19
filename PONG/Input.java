//Klasse for aa haandtere input fra bruker paa ev3 maskinen

// --- Imports ---
import lejos.hardware.Button;


class Input extends Thread{
    public run() {
        boolean cont = true;
        while(cont) {
            switch(waitForAnyPress()) {
                case ID_ENTER:

                    break;
                case ID_LEFT:

                    break;
                case ID_RIGHT:

                    break;
                case ID_UP:

                    break;
                case ID_DOWN:

                    break;

                case ID_ESCAPE:
                    System.exit(0);
                    break;
            }
        }

    }
}
