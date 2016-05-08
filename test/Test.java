import aacj.config.CharManager;
import aacj.config.ConfigManager;
import aacj.model.PixelTable;
import util.ConvertionUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by s-sumi on 2016/04/07.
 */
public class Test {
    @org.junit.Test
    public void regexTest(){
        String str="abababaaaaababbaba";
        str=str.replaceAll("a|b","");
        assertTrue(Objects.equals(str,""));
    }

    @org.junit.Test
    public void convertTest(){
        BufferedImage bi;
        try {
            bi = ImageIO.read(this.getClass()
                    .getResource("whiteImage.png"));

            ConfigManager cm = ConvertionUtil.generateConfigManager();
            CharManager charm=new CharManager(cm);
            PixelTable lineImg=ConvertionUtil.img2LineImg(bi,cm);
            String aa= ConvertionUtil.lineImg2AA(lineImg.data,lineImg.width,
                    lineImg.height,charm,cm)[0];

            assertTrue(Objects.equals(
                    aa.replaceAll(" |　|\r|\n","") , "" ));

        }catch(Exception e){
            fail();
        }
    }
}
