package models;

import com.avaje.ebean.Model;
import org.apache.commons.compress.utils.IOUtils;

import javax.persistence.*;
import java.io.*;

/**
 * Created by s-sumi on 2016/05/17.
 */
@Entity
@Table(name = "user_icon")
public class UserIcon extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long user_icon_id;

    @Lob
    private byte[] data;

    public static Finder<Long, UserIcon> find =
            new Finder<>(UserIcon.class);

    public UserIcon() {
        super();
    }

    public UserIcon(File file) {
        //try with resources は";"をつかって複数リソースを宣言することもできる
        try (InputStream iStream = new BufferedInputStream(new FileInputStream(file))) {
            this.data=IOUtils.toByteArray(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Long getUser_icon_id() {
        return user_icon_id;
    }
}
