package models;
import com.avaje.ebean.Model;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;


/**
 * Created by s-sumi on 2016/05/08.
 */
public class User extends Model {
    @Id
    public Long id;
    public String userId;
    public String password;
    public String biography;
}
