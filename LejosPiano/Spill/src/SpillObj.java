import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

interface Konstanter {
    float grense = -8;
    float hastighet = 0.1f;
    float width = 1.5f;
    float height = 1;
}

public class SpillObj implements Konstanter {
    private GL2 gl;
    private float x,y;
    private float[] color;

    public SpillObj(GL2 gl, float x, float y, float[] color) {
        this.gl = gl;
        this.x = x;
        this.y = y;
        this.color = color;
    }
    public void update() {
        y -= hastighet;
        draw();
    }
    private void draw() {
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
    public boolean check() {
        return (y < grense && y > grense - 3);
    }
}
