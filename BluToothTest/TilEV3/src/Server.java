import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;

public class Server {
    public static void connect() {
        ServerSocket serv = null;
        Socket s = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD lcd = ev3.getTextLCD();
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
        while(true) {
            try{
                String input = in.readUTF();
                //int input = in.readInt();
                lcd.drawString(input, 0, 1);

            } catch(Exception e) {
                lcd.drawString(e.toString(), 0, 1);
            }
        }
    }
    public static void main(String[] args) throws IOException {
        connect();

    }
}
