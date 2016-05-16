import java.util.Objects;

import static org.junit.Assert.assertTrue;

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


}
