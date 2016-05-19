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
    private Long id;

    @Constraints.Required(message = "必須項目です")
    @Constraints.Pattern(value = "\\w{4,16}",
            message = "ユーザIDは英数字で構成され、4文字以上16文字以下です")
    @Column(unique = true)
    @Index
    private String user_id;

    @Constraints.Required
    private String password;

    private String user_name;

    private String biography;

    @OneToOne(cascade = CascadeType.ALL)
    private UserIcon user_icon;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Tweet> tweets;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "relationship_table",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    private List<User> following;

    public User() {
        super();
    }

    public User(String userId, String password) {
        this.user_id = userId;
        this.password = password;
    }

    public static Finder<Long, User> find = new Finder<>(User.class);//(class,class)...deprecated

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public UserIcon getUser_icon() {
        return user_icon;
    }

    public void setUser_icon(UserIcon user_icon) {
        this.user_icon = user_icon;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public List<User> getFollowing() {
        return following;
    }

    public void setFollowing(List<User> following) {
        this.following = following;
    }
}
