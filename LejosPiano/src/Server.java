import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;

public class Server {
    private ServerSocket serv = null;
    private Socket s = null;
    private DataInputStream in = null;
    public DataOutputStream out = null;
    private EV3 ev3 = (EV3) BrickFinder.getLocal();
    private TextLCD lcd = ev3.getTextLCD();

    public void accept() throws IOException {
        LCD.clear();
        lcd.drawString("Venter på tilkobling", 0, 1);
        s = serv.accept();
        lcd.drawString("Tilkobling vellykket",0,1);
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());
    }
    public void connect() {
        try {
            serv = new ServerSocket(1111);
            accept();
        } catch(Exception e) {
            lcd.drawString(e.toString(), 0, 1);
        }
    }
}
