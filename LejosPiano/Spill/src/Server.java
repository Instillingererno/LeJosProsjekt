import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serv = null;
    private Socket s = null;
    private DataInputStream in = null;
    public DataOutputStream out = null;
    private EV3 ev3 = (EV3) BrickFinder.getLocal();
    private TextLCD lcd = ev3.getTextLCD();

    public Server() {}
    public void connect() {
        try {
            serv = new ServerSocket(1111);
            lcd.drawString("Venter på tilkobling", 0, 1);
            s = serv.accept();
            lcd.drawString("Tilkobling vellykket",0,1);
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
        } catch(Exception e) {
            lcd.drawString(e.toString(), 0, 1);
        }
    }
    public void send(String input) {
        try{
            this.out.writeUTF(input);
        } catch(Exception e) {
            System.out.println(e);
        }

    }
}
