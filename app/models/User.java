package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.Index;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;


/**
 * Created by s-sumi on 2016/05/08.
 */

// uniqueでないuserを格納しようとするとExceptionを吐くのでそれを使ってバリデーションする．
// see http://stackoverflow.com/questions/28906096/
//          play-framework-2-3-how-to-add-unique-constraint-to-sample-application
// undirectedは駄目みたい
// http://stackoverflow.com/questions/24464812/play-framework-2-ebean-manytoone-column-specified-twice
// http://stackoverflow.com/questions/15591198/one-to-many-for-same-entity-class-in-play-framework ←よく参考になった

@Entity
public class User extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Constraints.Required(message = "必須項目です")
    @Constraints.Pattern(value = "\\w{4,16}",
            message = "ユーザIDは英数字で構成され、4文字以上16文字以下です")
    @Column(unique = true)
    @Index
    public String user_id;

    @Constraints.Required
    public String password;

    public String user_name;

    public String biography;

    @OneToOne(cascade = CascadeType.ALL)
    public UserIcon user_icon;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Tweet> tweets;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "relationship_table",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    public List<User> following;

    public User() {
    }

    public User(String userId, String password) {
        this.user_id = userId;
        this.password = password;
    }

    public static Finder<Long, User> find = new Finder<>(User.class);//(class,class)...deprecated
}
