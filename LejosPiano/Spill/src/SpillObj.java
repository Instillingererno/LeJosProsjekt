import com.jogamp.opengl.GL2;

interface Konstanter {
    float grense = -2;
    float grenseHoyde = 3;
    float hastighet = 0.5f;
    float width = 1.5f;
    float height = 1;
}

public class SpillObj implements Konstanter {
    private GL2 gl;
    private float x,y;
    public float[] color;
    private final String note;
    private final int lane;

    public SpillObj(GL2 gl, float x, float y, float[] color, String note, int lane) {
        this.gl = gl;
        this.x = x;
        this.y = y;
        this.color = color;
        this.note = note;
        this.lane = lane;
    }
    public void update() {
        y -= hastighet;
        draw();
    }
    private void draw() {
        gl.glColor3f(0f,0f,0f);
        gl.glPointSize(20);
        gl.glBegin(GL2.GL_POINTS);
        gl.glVertex3f(0,-5,0);
        gl.glEnd();
        gl.glPushMatrix();
        gl.glTranslatef(x, y, 0f);
        gl.glColor3f(0f,0f,0f);
        gl.glPointSize(20);
        gl.glBegin(GL2.GL_POINTS);
        gl.glVertex3f(0,0,0);
        gl.glEnd();
        gl.glColor3fv(color, 0);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-width, height, 0f);
        gl.glVertex3f(width, height, 0f);
        gl.glVertex3f(width, -height, 0f);
        gl.glVertex3f(-width, -height, 0f);
        gl.glEnd();
        gl.glPopMatrix();

    }
    public boolean check() {
        return (y < grense && y > grense - 20);
    }
    public boolean erUnderGrense() {
        return y < grense - grenseHoyde;
    }
    public String getNote() {
        return note;
    }
    public float getY() {
        return y;
    }
    public int getLane() {
        return lane;
    }
}
