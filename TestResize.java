import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TestResize {
     
    public static void main(String[] args) {
    
        // Si quiero redimensionar esta imagen (reemplazar imagen)
        /*
        * @param String inputName
        * @param int width
        * @param int height
        */
        Resize.execute("imagen3",1024,768);

        // Si quiero redimensionar y colocarle un nuevo nombre (conservar imagen original)

        /**
        * @param String inputName
        * @param String outputName
        * @param int width
        * @param int height
        */
        Resize.execute("imagen3","imagen3Resize",200,300);
    }
}