import controllers.HomeController;
import org.junit.Test;

import java.awt.*;
import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by s-sumi on 2016/03/27.
 */
public class UtilTest {
    @Test
    public void isPictureTest() {
        HomeController hc = new HomeController();
        File jpg = new File(this.getClass().getResource("test.jpg").getPath());//同じパッケージに属するならこの書き方でリソースを持ってくることができる
        assertTrue(jpg.exists());
        assertTrue(hc.isPicture(jpg));
    }
    
    @Test
    public void fontTest00() {
        Font f=new Font("Dialog",Font.PLAIN,9);
        assertTrue(f.getFontName().contains(Font.DIALOG));
    }
    @Test
    public void fontTest01() {
        Font f=new Font("Monospaced",Font.PLAIN,9);
        assertFalse(f.getFontName().contains(Font.DIALOG));
    }
    @Test
    public void fontTest02() {
        Font f=new Font("SansSerif",Font.PLAIN,9);
        assertFalse(f.getFontName().contains(Font.DIALOG));
    }
    @Test
    public void fontTest03() {
        Font f=new Font("a;slkdf",Font.PLAIN,9);
        assertTrue(f.getFontName().contains(Font.DIALOG));
    }

    @Test
    public void fontTest04() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            Font f = new Font("MS Gothic", Font.PLAIN, 9);
            assertFalse(f.getFontName().contains(Font.DIALOG));
        }
    }
    @Test
    public void fontTest05() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            Font f = new Font("MS PGothic", Font.PLAIN, 9);
            assertFalse(f.getFontName().contains(Font.DIALOG));
        }
    }

}
