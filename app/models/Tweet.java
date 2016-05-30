package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.Index;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by s-sumi on 2016/05/08.
 */
@Entity
public class Tweet extends Model {
    @EmbeddedId
    public CombinedTweetKey tweetKey;

    @Constraints.Required
    public String content;

    @ManyToOne
    public User user;

    public CombinedTweetKey getTweetKey() {
        return tweetKey;
    }

    public void setTweetKey(CombinedTweetKey tweetKey) {
        this.tweetKey = tweetKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static Finder<Long, Tweet> find = new Finder<>(Tweet.class);

}


