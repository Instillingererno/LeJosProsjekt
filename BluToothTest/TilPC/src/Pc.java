import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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
            String input = in.readUTF();
            playSound(input);
        }
    }
    public static synchronized void playSound(final String url) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            Pc.class.getResourceAsStream(url));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
}
