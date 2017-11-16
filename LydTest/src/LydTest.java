import lejos.hardware.Sound;
import lejos.hardware.Sounds;
import java.io.File;

public class LydTest {
    public static void main(String[] args) {
		Sound.setVolume(50);
        File a = new File("A.wav");
        File c = new File("C.wav");
        //Sound.playSample(a);
        Sound.playSample(c);
    }
}
