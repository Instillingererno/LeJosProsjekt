import javax.swing.*;
import java.awt.*;
import static javax.swing.JOptionPane.*;
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
    private static final int CANVAS_WIDTH = 1500;
    private static final int CANVAS_HEIGHT = 1500;
    private float rotAngle = 30f;
    private float movementY = 0.1f;
    private int score = 0;
    private float grense = 6.5f;

    private GLU glu;
    private static FPSAnimator anim;
    private static GL2 gl;
    private NewDraw draw, draw2, draw3, draw4;
    private TextRenderer renderer;
    private Random random;

    public PianoHero (){this.addGLEventListener(this);}

    public void init (GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        gl.glClearColor(0.0f, 0.f, 0.0f, 0.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glShadeModel(GL2.GL_SMOOTH);

        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -20f);
        gl.glColor3f(1.0f,0.0f, 0.0f);
        draw = new NewDraw(gl, -5f, 8.5f);
        draw2 = new NewDraw(gl, -1.5f, 8.5f);
        draw3 = new NewDraw(gl, 2f, 10f);
        draw4 = new NewDraw(gl, 5.5f, 12f);
        random = new Random();
        renderer = new TextRenderer(new Font ("Sans Serif", Font.BOLD, 36));
        gl.glTranslatef(0f, 0f,20f);
    }

    public void reshape (GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        if (height == 0) height = 1;
        float aspect = (float) width / (float) height;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45, aspect, 0.1, 10000);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void display (GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -20f);


        gl.glColor3f(0f, 0f, 0f);
        draw.drawPoint(-5f, draw.getY(), 100f);
        draw.setYPos(draw.getY() - movementY);
        draw2.drawPoint(-1.5f, draw2.getY(), 100f);
        draw2.setYPos(draw2.getY() - movementY);
        draw3.drawPoint(2f, draw3.getY(), 100f);
        draw3.setYPos(draw3.getY() - movementY);
        draw4.drawPoint(5.5f, draw4.getY(), 100f);
        draw4.setYPos(draw4.getY() - movementY);

        checkBottom();

        // "Flyplass" Farge: GRØNN
        gl.glColor3f(0f, 1f, 0f);
        draw.drawSquare(-5, -5.8f, 3, 1.8f);
        draw.drawSquare(-1.5f, -5.8f, 3, 1.8f);
        draw.drawSquare(2f, -5.8f, 3, 1.8f);
        draw.drawSquare(5.5f, -5.8f, 3, 1.8f);

        // "Strengene" som notene går nedover. Farge: GUL
        gl.glColor3f(1.0f, 1.0f, 0.0f);
        draw.drawSquare(-5f, 5f,1.35f, 20f);
        draw.drawSquare(-1.5f, 5f,1.35f, 20f);
        draw.drawSquare(2f, 5f,1.35f, 20f);
        draw.drawSquare(5.5f, 5f,1.35f, 20f);

        gl.glColor3f(0.2f, 0.5f, 1.0f);
        draw.drawSquare(0f, 0f, 15f, 20f);

        gl.glColor3f(1.0f, 0.0f, 0.0f); //Background Color: RED
        draw.drawSquare(0f, 0f,22f, 20f);

        renderer.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
        renderer.setColor(1.0f, 1.0f, 0.0f, 1.0f);
        renderer.draw ("Score: " + score, 20, 650);
        renderer.endRendering();
        System.out.println (getRandomNumber());
    }

    private void checkBottom() {
        if (draw.getY() <= -grense) {
            draw.setYPos(getRandomNumber());
            score -= 10;
        }
        else if(draw2.getY() <= -grense) {
            draw2.setYPos(getRandomNumber());
            score -= 10;
        }
        else if (draw3.getY() <= -grense){
            draw3.setYPos(getRandomNumber());
            score -= 10;
        }
        else if (draw4.getY() <= -grense) {
            draw4.setYPos(getRandomNumber());
            score -= 10;
        }
    }

    public float getRandomNumber() {
        float rng = ((random.nextFloat() + 0.5f) * 10) - 2f;
        return rng;
    }

    public void dispose (GLAutoDrawable drawable) {}

    public static void main (String[] args) {
        GLCanvas canvas = new PianoHero();
        canvas.setPreferredSize (new Dimension (CANVAS_WIDTH, CANVAS_HEIGHT));
        anim = new FPSAnimator(canvas, 60, true);
        anim.start();

        final JFrame frame = new JFrame();
        frame.getContentPane().add(canvas);
        frame.setTitle(title);
        frame.setDefaultCloseOperation((WindowConstants.EXIT_ON_CLOSE));
        frame.pack();
        frame.setVisible(true);
    }
}
