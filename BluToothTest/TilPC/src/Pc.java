import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Pc {
    public static void main(String[] args) throws IOException {
        Socket MyClient;
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            System.out.println("Prøver tilkobling");
            MyClient = new Socket("10.0.1.1", 1111);
            System.out.println("Vellykket");
            in = new DataInputStream(MyClient.getInputStream());
            out = new DataOutputStream((MyClient.getOutputStream()));
        }
        catch (IOException e) {
            System.out.println(e);
        }
        while(true) {
            out.writeUTF(JOptionPane.showInputDialog("Hva vil du skrive ut: "));
        }
    }
}
