import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Note;

public class PianoHero extends GLCanvas implements GLEventListener {
    private final String[] furElise = {
            "0","0","E3","0",
            "0","B4","0","0",
            "0","0","0","C5",
            "0","0","D5","0",
            "0","0","0","E4",
            "0","0","A3","0",
            "C5","0","0","0",
            "0","0","D5","0",
            "0","0","0","E5",
            "0","0","0","F4",
            "0","0","0","G3",
            "0","0","D5","0",
            "0","0","0","E5",
            "0","0","F5","0",
            "0","0","G4","0",
            "0","C3","0","0",
            "0","E5","0","0",
            "D5","0","0","0",
            "0","0","C5","0",
            "0","0","0","B4",
            "0","0","A3","0",
            "0","0","0","A4",
            "0","0","B4","0",
            "0","0","0","C5",
            "0","0","E4","0",
            "0","0","0","E3",
            "0","0","B4","0",
            "0","A4","0","0",
            "0","E4","0","0",
            "C4","0","0","0",
            "0","0","0","A3",
            "0","0","C5","0",
            "0","0","B4","0",
            "0","G#4","0","0", // Andre opptur
            "0","E4","0","0",
            "0","0","E3","0",
            "0","0","0","B4",
            "0","0","A4","0",
            "0","0","E4","0",
            "0","C4","0","0",
            "0","A3","0","0",
            "0","0","A4","0",
            "0","0","C5","0",
            "0","0","0","D5",
            "0","0","B4","0",
            "0","0","0","E5",
            "0","0","D#5","0",
            "0","0","0","E5",
            "0","0","D#5","0",
            "0","0","0","E5"

    };

    public static boolean[] innafor = {false,false,false,false};
    public static Note[] noter = {null,null,null,null};
    private final float[][] COLORS = {
            {0.2156f,0.7490f,0.5607f}, // Green
            {0.7490f,0.2901f,0.2156f}, // Red
            {0.8588f,0.8784f,0.2784f}, // Yellow
            {0.2235f,0.5764f,0.8274f}, // Blue
            {0.3372f, 0.3372f, 0.3372f}
    };

    public static RealtimePlayer player = null;

    private static final String title = "PianoHero";
    private static final int CANVAS_WIDTH = 1300;
    private static final int CANVAS_HEIGHT = 800;
    public static int score = 0;

    public static SpillObj[] spillObjs;

    private static Socket MyClient;
    private static DataInputStream in = null;
    private static DataOutputStream out = null;

    private GLU glu;
    private static FPSAnimator anim;
    private GL2 gl;
    private TextRenderer renderer;

    public PianoHero() {
        this.addGLEventListener(this);
        this.addKeyListener(new keyWait());
    }

    public void init(GLAutoDrawable drawable) {

        try {
            player = new RealtimePlayer();
        } catch(MidiUnavailableException e) {
            e.printStackTrace();
        }

        gl = drawable.getGL().getGL2();
        glu = new GLU();
        gl.glClearColor(0.0f, 0.f, 0.0f, 0.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glShadeModel(GL2.GL_SMOOTH);
        renderer = new TextRenderer(new Font("Sans Serif", Font.BOLD, 36));

        int antall = 0;
        for(String i : furElise) antall += (i != "0") ? 1 : 0;
        spillObjs = new SpillObj[antall];
        antall = 0;
        int teller = 1;
        int avstandBunn = 800;
        float avstandMellom = 4f;
        for(int i = 0; i < furElise.length; i++) {
            switch (teller) {
                case 1:
                    if(furElise[i] != "0") {
                        spillObjs[antall] = new SpillObj(gl, -5,avstandBunn + avstandMellom*spillObjs.length - i*avstandMellom, COLORS[0], furElise[i], 0);
                        antall++;
                    }
                    teller++;
                    break;
                case 2:
                    if(furElise[i] != "0") {
                        spillObjs[antall] = new SpillObj(gl, -1.5f,avstandBunn + avstandMellom*spillObjs.length - i*avstandMellom + avstandMellom, COLORS[1], furElise[i], 1);
                        antall++;
                    }
                    teller++;
                    break;
                case 3:
                    if(furElise[i] != "0") {
                        spillObjs[antall] = new SpillObj(gl, 2,avstandBunn + avstandMellom*spillObjs.length - i*avstandMellom + avstandMellom*2, COLORS[2], furElise[i], 2);
                        antall++;
                    }
                    teller++;
                    break;
                case 4:
                    if(furElise[i] != "0") {
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

        //Sjekk hitboxes og noter
        for(int i = spillObjs.length -1; i >= 0; i--) {
            if(spillObjs[i].check()) {
                innafor[spillObjs[i].getLane()] = true;
                noter[spillObjs[i].getLane()] = new Note(spillObjs[i].getNote());
                spillObjs[i].color = COLORS[4];
            }
            else if(spillObjs[i].erUnderGrense()) {
                innafor[spillObjs[i].getLane()] = false;
                noter[spillObjs[i].getLane()] = null;
            }
        }


        gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -17.5f);
        gl.glRotated(-70,1,0,0);

        // Tegn inn spillObj
        for(SpillObj i : spillObjs) i.update();


        /*gl.glPointSize(10000);
        gl.glColor3f(0f,0f,0f);
        gl.glBegin(GL2.GL_POINTS);
        gl.glVertex3f(0,1,-2);
        gl.glEnd();*/

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
        renderer.draw("Score: " + score, 20, 650);
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
        boolean fortsett = true;

        while(MyClient == null && fortsett) {
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
                    switch (in.readInt()) {
                        case 1:
                            score += (innafor[0]) ? 10 : -10;
                            if(noter[0] != null) player.startNote(noter[0]);
                            break;
                        case 2:
                            if(noter[0] != null) player.stopNote(noter[0]);
                            break;
                        case 3:
                            score += (innafor[1]) ? 10 : -10;
                            if(noter[1] != null) player.startNote(noter[1]);
                            break;
                        case 4:
                            if(noter[1] != null) player.stopNote(noter[1]);
                            break;
                        case 5:
                            score += (innafor[2]) ? 10 : -10;
                            if(noter[2] != null) player.startNote(noter[2]);
                            break;
                        case 6:
                            if(noter[2] != null) player.stopNote(noter[2]);
                            break;
                        case 7:
                            score += (innafor[3]) ? 10 : -10;
                            if(noter[3] != null) player.startNote(noter[3]);
                            break;
                        case 8:
                            if(noter[3] != null) player.stopNote(noter[3]);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class keyWait implements KeyListener {
    boolean[] keyLiftet = {true,true,true,true};
    @Override
    public void keyTyped(KeyEvent e) { }
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()-48) {
            case 1:
                if(keyLiftet[0]) {
                    PianoHero.score += (PianoHero.innafor[0]) ? 10 : -10;
                    if(PianoHero.noter[0] != null) PianoHero.player.startNote(PianoHero.noter[0]);
                    keyLiftet[0] = false;
                }
                break;
            case 2:
                if(keyLiftet[1]) {
                    PianoHero.score += (PianoHero.innafor[1]) ? 10 : -10;
                    if(PianoHero.noter[1] != null) PianoHero.player.startNote(PianoHero.noter[1]);
                    keyLiftet[1] = false;
                }
                break;
            case 3:
                if(keyLiftet[2]) {
                    PianoHero.score += (PianoHero.innafor[2]) ? 10 : -10;
                    if(PianoHero.noter[2] != null) PianoHero.player.startNote(PianoHero.noter[2]);
                    keyLiftet[2] = false;
                }
                break;
            case 4:
                if(keyLiftet[3]) {
                    PianoHero.score += (PianoHero.innafor[3]) ? 10 : -10;
                    if(PianoHero.noter[3] != null) PianoHero.player.startNote(PianoHero.noter[3]);
                    keyLiftet[3] = false;
                }
                break;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()-48) {
            case 1:
                keyLiftet[0] = true;
                if(PianoHero.noter[0] != null) PianoHero.player.stopNote(PianoHero.noter[0]);
                break;
            case 2:
                keyLiftet[1] = true;
                if(PianoHero.noter[1] != null) PianoHero.player.stopNote(PianoHero.noter[1]);
                break;
            case 3:
                keyLiftet[2] = true;
                if(PianoHero.noter[2] != null) PianoHero.player.stopNote(PianoHero.noter[2]);
                break;
            case 4:
                keyLiftet[3] = true;
                if(PianoHero.noter[3] != null) PianoHero.player.stopNote(PianoHero.noter[3]);
                break;
        }
    }
}