import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Chord;

import javax.sound.midi.MidiUnavailableException;

public class test {
    public static void main(String[] args) throws MidiUnavailableException {
        RealtimePlayer player = new RealtimePlayer();
        Chord chord = new Chord("Cmaj");
        player.startChord(chord);

    }
}
