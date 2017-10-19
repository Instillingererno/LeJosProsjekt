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
        if(lineNr == null) {
            this.lcd.drawString(input, this.padding, this.currentLine * this.lineHeight + this.padding);
            this.currentLine++;
        } else {
            this.lcd.drawString(input, this.padding, linenr * this.lineHeight + this.padding);
        }
    }

    public clearScreen() {
        this.lcd.clear()
        this.currentLine = 0;
    }

    public drawPosition() {

    }
}
