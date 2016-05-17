package models;

import com.avaje.ebean.Model;

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
    public Long user_icon_id;

    @Lob
    public byte[] data;

    public static Finder<Long, UserIcon> find =
            new Finder<>(UserIcon.class);

    public UserIcon() {
        super();
    }

    public UserIcon(File file) {
        //try with resources は";"をつかって複数リソースを宣言することもできるよ
        try (InputStream iStream = new BufferedInputStream(new FileInputStream(file))) {
            iStream.read(this.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
