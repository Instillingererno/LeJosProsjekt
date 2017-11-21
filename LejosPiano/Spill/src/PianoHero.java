import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Note;

public class PianoHero extends GLCanvas implements GLEventListener {
    private final int[] furElise = {
            0,0,0,67,
            0,0,64,0,
            0,49,0,0,
            45,0,72,0,
            0,0,74,0,
            0,0,0,75,
            0,0,73,0,
            0,0,0,76,
            0,0,75,0,
            0,0,0,76,
            0,0,75,0,
            0,0,0,76,
            0,0,64,0,
            0,52,0,0,
            45,0,0,70,
            0,0,0,69,
            0,0,67,0,
            0,52,0,0,
            36,0,0,0,
            0,0,0,69,
            0,0,64,0,
            0,52,0,0,
            45,0,69,0,
            0,0,72,0,
            0,0,0,74,
            0,0,70,0,
            0,0,0,76,
            0,0,75,0,
            0,0,0,76,
            0,0,75,0,
            0,0,0,76
    };
    public static boolean[] innafor = {false,false,false,false};
    public static int[] noter = {0,0,0,0};
    private final long timeAtStart;
    private float time;
    private final double takt = 1;
    private final float[][] COLORS = {
            {0.2156f,0.7490f,0.5607f}, // Green
            {0.7490f,0.2901f,0.2156f}, // Red
            {0.8588f,0.8784f,0.2784f}, // Yellow
            {0.2235f,0.5764f,0.8274f}, // Blue
            {1f,1f,1f}
    };

    private static server client;

    public static RealtimePlayer player = null;

    private static final String title = "PianoHero";
    private static final int CANVAS_WIDTH = 1300;
    private static final int CANVAS_HEIGHT = 800;
    public static int score = 0;

    public static SpillObj[] spillObjs;

    private GLU glu;
    private static FPSAnimator anim;
    private GL2 gl;
    private TextRenderer renderer;

    public PianoHero() {
        this.addGLEventListener(this);
        this.addKeyListener(new keyWait());
        timeAtStart = System.currentTimeMillis();
    }

    public void init(GLAutoDrawable drawable) {


        try {
            player = new RealtimePlayer();
            //final Pattern pattern = MidiFileManager.loadPatternFromMidi(new File("Spill/src/beethoven.mid"));
            //player.play(pattern);
        } catch(MidiUnavailableException e) {
            e.printStackTrace();
        } /*catch(InvalidMidiDataException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }*/

        gl = drawable.getGL().getGL2();
        glu = new GLU();
        gl.glClearColor(0.0f, 0.f, 0.0f, 0.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glShadeModel(GL2.GL_SMOOTH);
        renderer = new TextRenderer(new Font("Sans Serif", Font.BOLD, 36));

        /*spillObjs = new SpillObj[] {new SpillObj(gl, -5, 6, COLORS[0]),
                                    new SpillObj(gl, -1.5f, 6, COLORS[1]),
                                    new SpillObj(gl, 2, 6,COLORS[2]),
                                    new SpillObj(gl, 5.5f, 6, COLORS[3])};*/

        int antall = 0;
        for(int i : furElise) antall += (i != 0) ? 1 : 0;
        spillObjs = new SpillObj[antall];
        antall = 0;
        int teller = 1;
        int avstandBunn = 400;
        float avstandMellom = 4f;
        for(int i = 0; i < furElise.length; i++) {
            switch (teller) {
                case 1:
                    if(furElise[i] != 0) {
                        spillObjs[antall] = new SpillObj(gl, -5,avstandBunn + avstandMellom*spillObjs.length - i*avstandMellom, COLORS[0], furElise[i], 0);
                        antall++;
                    }
                    teller++;
                    break;
                case 2:
                    if(furElise[i] != 0) {
                        spillObjs[antall] = new SpillObj(gl, -1.5f,avstandBunn + avstandMellom*spillObjs.length - i*avstandMellom + avstandMellom, COLORS[1], furElise[i], 1);
                        antall++;
                    }
                    teller++;
                    break;
                case 3:
                    if(furElise[i] != 0) {
                        spillObjs[antall] = new SpillObj(gl, 2,avstandBunn + avstandMellom*spillObjs.length - i*avstandMellom + avstandMellom*2, COLORS[2], furElise[i], 2);
                        antall++;
                    }
                    teller++;
                    break;
                case 4:
                    if(furElise[i] != 0) {
                        spillObjs[antall] = new SpillObj(gl, 5.5f,avstandBunn + avstandMellom*spillObjs.length - i*avstandMellom + avstandMellom*3, COLORS[3], furElise[i], 3);
                        antall++;
                    }
                    teller = 1;
                    break;
            }
        }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        gl = drawable.getGL().getGL2();
        if (height == 0) height = 1;
        float aspect = (float) width / (float) height;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45, aspect, 0.1, 10000);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void display(GLAutoDrawable drawable) {
        time = (time = System.currentTimeMillis() - timeAtStart) / 1000;


        //Sjekk hitboxes og noter
        for(int i = spillObjs.length -1; i >= 0; i--) {
            if(spillObjs[i].check()) {
                innafor[spillObjs[i].getLane()] = true;
                noter[spillObjs[i].getLane()] = spillObjs[i].getNote();
                spillObjs[i].color = COLORS[4];
            }
            else if(spillObjs[i].erUnderGrense()) {
                innafor[spillObjs[i].getLane()] = false;
                noter[spillObjs[i].getLane()] = 0;
            }
        }


        gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -17.5f);
        gl.glRotated(-70,1,0,0);

        // Tegn inn spillObj
        for(SpillObj i : spillObjs) i.update();


        gl.glPointSize(10000);
        gl.glColor3f(0f,0f,0f);
        gl.glBegin(GL2.GL_POINTS);
        gl.glVertex3f(0,1,-2);
        gl.glEnd();

        // "Flyplass" eller det området der akkordene skal være når man trykker
        gl.glColor3f(0.36f, 1f, 0.36f);
        drawSquare(-5, -5.8f, 3, 1.8f);
        gl.glColor3f(1, 0.36f, 0.36f);
        drawSquare(-1.5f, -5.8f, 3, 1.8f);
        gl.glColor3f(1.0f, 0.99f, 0.4f);
        drawSquare(2f, -5.8f, 3, 1.8f);
        gl.glColor3f(0.45f, 0.45f, 1.0f);
        drawSquare(5.5f, -5.8f, 3, 1.8f);

        // "Strengene" som notene går nedover. Farge: GUL
        gl.glColor3f(1f, 1f, 1f);
        drawSquare(-5f, 5f, 1.35f, 20f);
        drawSquare(-1.5f, 5f, 1.35f, 20f);
        drawSquare(2f, 5f, 1.35f, 20f);
        drawSquare(5.5f, 5f, 1.35f, 20f);

        gl.glColor3f(0.3372f, 0.3372f, 0.3372f);
        drawSquare(0f, 0f, 15f, 20f);

        gl.glColor3f(0.5098f, 0.3137f, 0.1411f); //Background Color: RED
        drawSquare(0f, 0f, 22f, 20f);

        renderer.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
        renderer.setColor(1.0f, 1.0f, 0.0f, 1.0f);
        renderer.draw("Score: " + score + " Y: " + spillObjs[0].getY(), 20, 650);
        renderer.endRendering();
    }

    private void drawSquare(float x, float y, float width, float height) {
        width /= 2;
        height /= 2;

        gl.glPushMatrix();
        gl.glTranslatef(x, y, 0f);

        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-width, height, 0f);
        gl.glVertex3f(width, height, 0f);
        gl.glVertex3f(width, -height, 0f);
        gl.glVertex3f(-width, -height, 0f);
        gl.glEnd();
        gl.glPopMatrix();

    }

    public void dispose(GLAutoDrawable drawable) {
    }

    public static void main(String[] args) {
        boolean fortsett = false;
        int teller = 0;

        while (fortsett) {
            teller++;
            try {
                client = new server();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(teller == 1) {
                fortsett = JOptionPane.showConfirmDialog(null, "Det blitt forsøkt å kontakte EV3 enheten, fortsett å kontakte EV3?", "JA for å fortsette, nei for å stoppe", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            }
        }

        GLCanvas canvas = new PianoHero();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        anim = new FPSAnimator(canvas, 60);
        anim.start();

        final JFrame frame = new JFrame();
        frame.getContentPane().add(canvas);
        frame.setTitle(title);
        frame.setDefaultCloseOperation((WindowConstants.EXIT_ON_CLOSE));
        frame.pack();
        frame.setVisible(true);

        if(fortsett) {
            try {
                while(true) {
                    switch (client.getInt()) {
                        case 1:
                            score += (innafor[0]) ? 10 : -10;
                            break;
                        case 2:
                            break;
                        case 3:
                            score += (innafor[1]) ? 10 : -10;
                            break;
                        case 4:
                            break;
                        case 5:
                            score += (innafor[2]) ? 10 : -10;
                            break;
                        case 6:
                            break;
                        case 7:
                            score += (innafor[3]) ? 10 : -10;
                            break;
                        case 8:
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
class server {
    Socket MyClient;
    DataInputStream in = null;
    DataOutputStream out = null;

    public server() throws IOException {
        try {
            System.out.println("Prøver tilkobling");
            MyClient = new Socket("10.0.1.1", 1111);
            System.out.println("Vellykket");
            in = new DataInputStream(MyClient.getInputStream());
            out = new DataOutputStream((MyClient.getOutputStream()));
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    public int getInt() throws IOException {
        return in.readInt();
    }
}

class keyWait implements KeyListener {
    boolean keyLiftet = true;
    @Override
    public void keyTyped(KeyEvent e) { }
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()-48) {
            case 1:
                PianoHero.score += (PianoHero.innafor[0]) ? 10 : -10;
                if(PianoHero.noter[0] != 0) PianoHero.player.play(new Note(PianoHero.noter[0]));
                break;
            case 2:
                PianoHero.score += (PianoHero.innafor[1]) ? 10 : -10;
                if(PianoHero.noter[1] != 0) PianoHero.player.play(new Note(PianoHero.noter[1]));
                break;
            case 3:
                PianoHero.score += (PianoHero.innafor[2]) ? 10 : -10;
                if(PianoHero.noter[2] != 0) PianoHero.player.play(new Note(PianoHero.noter[2]));
                break;
            case 4:
                PianoHero.score += (PianoHero.innafor[3]) ? 10 : -10;
                if(PianoHero.noter[3] != 0) PianoHero.player.play(new Note(PianoHero.noter[3]));
                break;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) { }
}