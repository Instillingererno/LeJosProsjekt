import com.jogamp.opengl.GL2;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import static javax.swing.JOptionPane.*;

public class Controller implements KeyListener {
    private boolean hit1 = false;
    private boolean hit2 = false;
    private boolean hit3 = false;
    private boolean hit4 = false;


    public Controller() {

    }

    public boolean getHit1() {
        return hit1;
    }

    public boolean getHit2() {
        return hit2;
    }

    public boolean getHit3() {
        return hit3;
    }

    public boolean getHit4(){
        return hit4;
    }

    public void keyTyped (KeyEvent e) {}

    public void keyPressed (KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_1) {
            hit1 = true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_2) {
            hit2 = true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_3) {
            hit3 = true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_4) {
            hit4 = true;
        }
    }

    public void keyReleased (KeyEvent e) {
        if (e.getKeyCode () == KeyEvent.VK_1) {
            hit1 = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_2) {
            hit2 = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_3) {
            hit3 = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_4) {
            hit4 = false;
        }
    }
}
