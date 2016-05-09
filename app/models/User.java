package models;

import com.avaje.ebean.Model;
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
    public String userId;

    @Constraints.Required
    @Constraints.Pattern(value = "\\w{4,16}",
            message = "パスワードは英数字で構成され、4文字以上16文字以下です")
    public String password;

    public String userName;

    public String biography;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Tweet> tweets;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "relationship_table",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "followingId"))
    public List<User> following;

    public User() {
    }

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public static Finder<Long, User> find = new Finder<Long, User>(User.class);//(class,class)...deprecated
}
