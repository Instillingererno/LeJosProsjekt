import javax.swing.*;
import java.awt.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

public class PianoHero extends GLCanvas implements GLEventListener {
    private static final String title = "PianoHero";
    private static final int CANVAS_WIDTH = 1300;
    private static final int CANVAS_HEIGHT = 800;
    private float rotAngle = 30f;
    private float movementY = 0.1f;
    private static int score = 0;
    private float grense = 8f;
    private float t = 0f;

    private static boolean[] pressed = {false,false,false,false};

    private GLU glu;
    private static FPSAnimator anim;
    private static GL2 gl;
    private NewDraw draw, draw2, draw3, draw4;
    private TextRenderer renderer;
    private Random random;

    public PianoHero() {
        this.addGLEventListener(this);
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        gl.glClearColor(0.0f, 0.f, 0.0f, 0.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glShadeModel(GL2.GL_SMOOTH);

        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -20f);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        draw = new NewDraw(gl, -5f, 8.5f);
        draw2 = new NewDraw(gl, -1.5f, 8.5f);
        draw3 = new NewDraw(gl, 2f, 10f);
        draw4 = new NewDraw(gl, 5.5f, 12f);
        random = new Random();
        renderer = new TextRenderer(new Font("Sans Serif", Font.BOLD, 36));
        gl.glTranslatef(0f, 0f, 20f);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        if (height == 0) height = 1;
        float aspect = (float) width / (float) height;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45, aspect, 0.1, 10000);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -20f);

        // "Flyplass" eller det området der akkordene skal være når man trykker
        gl.glColor3f(0.36f, 1f, 0.36f);
        draw.drawSquare(-5, -5.8f, 3, 1.8f);
        gl.glColor3f(1, 0.36f, 0.36f);
        draw.drawSquare(-1.5f, -5.8f, 3, 1.8f);
        gl.glColor3f(1.0f, 0.99f, 0.4f);
        draw.drawSquare(2f, -5.8f, 3, 1.8f);
        gl.glColor3f(0.45f, 0.45f, 1.0f);
        draw.drawSquare(5.5f, -5.8f, 3, 1.8f);

        // "Strengene" som notene går nedover. Farge: GUL
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        draw.drawSquare(-5f, 5f, 1.35f, 20f);
        draw.drawSquare(-1.5f, 5f, 1.35f, 20f);
        draw.drawSquare(2f, 5f, 1.35f, 20f);
        draw.drawSquare(5.5f, 5f, 1.35f, 20f);

        gl.glColor3f(1f, 0.0f, 0.8f);
        draw.drawSquare(0f, 0f, 15f, 20f);

        gl.glColor3f(1.0f, 0.0f, 0.0f); //Background Color: RED
        draw.drawSquare(0f, 0f, 22f, 20f);

        renderer.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
        renderer.setColor(1.0f, 1.0f, 0.0f, 1.0f);
        renderer.draw("Score: " + score, 20, 650);
        renderer.endRendering();

        t += 0.02f;
        System.out.println ("Tid: " + t);
    }



    public float getRandomNumber() {
        float rng = ((random.nextFloat() + 0.5f) * 10) + 4f;
        return rng;
    }

    public void dispose(GLAutoDrawable drawable) {
    }

    public static void main(String[] args) {
        GLCanvas canvas = new PianoHero();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        anim = new FPSAnimator(canvas, 60, true);
        anim.start();

        final JFrame frame = new JFrame();
        frame.getContentPane().add(canvas);
        frame.setTitle(title);
        frame.setDefaultCloseOperation((WindowConstants.EXIT_ON_CLOSE));
        frame.pack();
        frame.setVisible(true);

        try {
            server client = new server();
            while(true) {
                switch (client.getInt()) {
                    case 1:
                        pressed[0] = true;
                        break;
                    case 2:
                        pressed[0] = false;
                        break;
                    case 3:
                        pressed[1] = true;
                        break;
                    case 4:
                        pressed[1] = false;
                        break;
                    case 5:
                        pressed[2] = true;
                        break;
                    case 6:
                        pressed[2] = false;
                        break;
                    case 7:
                        pressed[3] = true;
                        break;
                    case 8:
                        pressed[3] = false;
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
class server {
    Socket MyClient;
    DataInputStream in = null;
    DataOutputStream out = null;

    public server() throws IOException {
        while(MyClient == null) {
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
    }
    public int getInt() throws IOException {
        return in.readInt();
    }
}

