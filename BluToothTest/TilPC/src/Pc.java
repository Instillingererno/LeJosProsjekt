import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Pc {
    public static void main(String[] args) throws IOException {
        Socket MyClient;
        try {
            MyClient = new Socket("10.0.1.1", 1111);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
}
