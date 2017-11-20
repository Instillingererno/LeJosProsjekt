import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Chord;

import javax.sound.midi.MidiUnavailableException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Pc {
    public static void main(String[] args) throws IOException, MidiUnavailableException {
        Socket MyClient;
        DataInputStream in = null;
        DataOutputStream out = null;
        Chord[] chords = {new Chord("E"), new Chord("B"), new Chord("C"), new Chord("A")};
        RealtimePlayer player = new RealtimePlayer();

        try {
            System.out.println("Prøver tilkobling");
            MyClient = new Socket("10.0.1.1", 1111);
            System.out.println("Vellykket");
            in = new DataInputStream(MyClient.getInputStream());
            out = new DataOutputStream((MyClient.getOutputStream()));
        } catch (IOException e) {
            System.out.println(e);
        }
        while (true) {
            int input = in.readInt();
            //playsound
            switch (input) {
                case 1:
                    player.startChord(chords[0]);
                    break;
                case 2:
                    player.stopChord(chords[0]);
                    break;
                case 3:
                    player.startChord(chords[1]);
                    break;
                case 4:
                    player.stopChord(chords[1]);
                    break;
                case 5:
                    player.startChord(chords[2]);
                    break;
                case 6:
                    player.stopChord(chords[2]);
                    break;
                case 7:
                    player.startChord(chords[3]);
                    break;
                case 8:
                    player.stopChord(chords[3]);
                    break;
            }
        }
    }
}