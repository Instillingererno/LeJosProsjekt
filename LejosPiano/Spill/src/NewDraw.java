import com.jogamp.opengl.GL2;

public class NewDraw {
    private float x, y, z;
    private float boxRot = 0f;
    private int direction = 1;
    GL2 gl;
    private float limit = 150f;

    public NewDraw(GL2 gl) {
        this.gl = gl;
    }

    public void drawSquare (float x, float y, float width, float height) {
        width /= 2;
        height /= 2;

        gl.glTranslatef (x, y, 0f);

        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-width, height, 0f);
        gl.glVertex3f(width, height, 0f);
        gl.glVertex3f(width, -height, 0f);
        gl.glVertex3f(-width, -height, 0f);
        gl.glEnd();

        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -20f);
    }

    public void drawPoint(float x, float y, float size){
        gl.glTranslatef(x, y, 0f);

        gl.glPointSize (size);
        gl.glEnable (GL2.GL_POINT_SMOOTH);
        gl.glBegin (GL2.GL_POINTS);
        gl.glVertex3f (0.0f, 0.0f, 0);
        gl.glEnd();

        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -20f);
    }
}