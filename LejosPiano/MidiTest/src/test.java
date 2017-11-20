import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Chord;

import javax.sound.midi.MidiUnavailableException;

public class test {
    public static void main(String[] args) throws MidiUnavailableException, InterruptedException {
        RealtimePlayer player = new RealtimePlayer();
        Chord cmaj = new Chord("Cmaj");
        Chord b = new Chord("B");
        player.startChord(cmaj);
        Thread.sleep(0000);
        player.startChord(b);
        Thread.sleep(2000);
        player.stopChord(cmaj);
        player.stopChord(b);

    }
}
