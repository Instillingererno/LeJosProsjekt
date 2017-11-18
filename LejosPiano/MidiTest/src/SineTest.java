import jm.JMC;
import jm.music.data.*;
import jm.util.View;
import jm.util.Write;
import jm.audio.Instrument;
import jm.util.Play;

public final class SineTest implements JMC {

    public static void main(String[] args){
        Score s = new Score();
        s.setTempo(60);
        int numbOfTones = 10;
        Instrument[] insts = new Instrument[numbOfTones];

        for(int i=0; i<numbOfTones;i++) {
            Note n = new Note((int)(Math.random() * 40 + 60),
                    SEMIBREVE, (int)(Math.random() * 60 + 60));
            n.setPan(Math.random());
            Phrase phr = new Phrase(n, Math.random() * 10.0);
            Part p = new Part("Sine", i);
            p.addPhrase(phr);
            s.addPart(p);

            insts[i] = new SineInst(44100);
        }
        View.print(s);
        Play.audio(s, insts);
    }
}