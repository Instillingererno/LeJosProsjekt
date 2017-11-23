import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Chord;

import javax.sound.midi.MidiUnavailableException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Pc {
    public static void main(String[] args) throws IOException, MidiUnavailableException {
        Socket MyClient = null;
        DataInputStream in = null;
        Chord[] chords = {new Chord("Gmaj"), new Chord("Dmaj"), new Chord("Emin"), new Chord("Cmaj")}; //G  D E C
        RealtimePlayer player = new RealtimePlayer();

        //player.changeInstrument(125); Gjør det mulig å endre instrument akkordene spilles

        // Kontakter EV3-en
        while(MyClient == null) {
            try {
                System.out.println("Prøver tilkobling");
                MyClient = new Socket("10.0.1.1", 1111);
                System.out.println("Vellykket");
                in = new DataInputStream(MyClient.getInputStream());
            } catch (IOException e) {
                System.out.println(e);
            }
        }

        //Continue
        while (true) {
            int input = in.readInt(); //Venter på int fra EV3 som brukes i switch
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