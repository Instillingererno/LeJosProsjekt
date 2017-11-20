import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Chord;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;


public class MidiTest {
    public static void main(String[] args) throws MidiUnavailableException, IOException {
        Piano a = new Piano("E");
        Piano c = new Piano("B");
        Piano f = new Piano("Cmaj");
        Piano g = new Piano("A");

        String current = new java.io.File( "." ).getCanonicalPath();
        System.out.println("Current dir:"+current);

        PlayMidiFromFile.run("MidiTest/src/beethoven.mid");

        KeyListener keys = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) {
                // 1 = 49, 2 = 50, 3 = 51, 4 = 52
                switch (e.getKeyCode()) {
                    case 49:
                        a.start();
                        break;
                    case 50:
                        c.start();
                        break;
                    case 51:
                        f.start();
                        break;
                    case 52:
                        g.start();
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 49:
                        a.stop();
                        break;
                    case 50:
                        c.stop();
                        break;
                    case 51:
                        f.stop();
                        break;
                    case 52:
                        g.stop();
                        break;
                }
            }
        };
        JFrame frame = new JFrame("FrameDemo");
        frame.setPreferredSize(new Dimension(1000,1000));
        frame.addKeyListener(keys);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel emptyLabel = new JPanel(new BorderLayout());
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}

class Piano {
    private RealtimePlayer player;
    private Chord chord;
    private boolean Pressed = false;
    public Piano(String chord) throws MidiUnavailableException {
        this.player = new RealtimePlayer();
        this.chord = new Chord(chord);
        this.player.changeInstrument(0);
    }
    public void start() {
        if(!Pressed) {
            this.player.startChord(chord);
            this.Pressed = true;
        }

    }
    public void stop() {
        this.player.stopChord(chord);
        this.Pressed = false;
    }
}
class PlayMidiFromFile{
    public static void run(String url) {
        try {
            final Player player = new Player();
            final Pattern pattern = MidiFileManager.loadPatternFromMidi(new File(url));
            player.play(pattern);
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }
}