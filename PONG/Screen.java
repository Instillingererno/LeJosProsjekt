// --- Kommentarer ---
// Ma testes!!
//
//
//
// -------------------

// --- Imports ---
//skjerm kontroller
import lejos.hardware.lcd.*; //?????


class Screen {
// --- Attributer ---
    int currentLine = 0;
    int lineHeight = 10;
    int padding = 10;

    LCD lcd = new LCD();

// --- Metoder ---
    public drawString(String input, int lineNr) {
        this.lcd.drawString(input, padding, current * lineHeight + padding);
        this.currentLine++;
    }

    public clearScreen() {
        this.lcd.clear()
        this.currentLine = 0;
    }

    public drawPosition() {

    }
}
