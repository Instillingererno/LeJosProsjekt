import jm.audio.AOException;
import jm.audio.Instrument;
import jm.audio.synth.Envelope;
import jm.audio.synth.Pluck;
import jm.audio.synth.StereoPan;
import jm.audio.synth.Volume;
import jm.gui.cpn.JmMidiPlayer;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.Play;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static jm.constants.Durations.SEMIBREVE;

public class MidiTest {

    public static void main(String[] args) throws MidiUnavailableException, IOException {
        createNote();
    }

    public static void createNote(){
        Score s = new Score();
        s.setTempo(60);
        Instrument[] insts = new Instrument[1];

        Note n = new Note(80, SEMIBREVE, 120);
        Part p = new Part("Sine", 0);
        s.addPart(p);

        insts[0] = new SineInst(44100);

        Play.audio(s, insts);
    }
}

