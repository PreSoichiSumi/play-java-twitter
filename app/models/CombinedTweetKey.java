package models;

import com.avaje.ebean.annotation.CreatedTimestamp;

import javax.persistence.*;
import java.util.Date;

@Embeddable
public class CombinedTweetKey{
    public String tweetId;
    public Date postDate=new Date();



    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    /**
     * 複合キーなら必ずoverrideする必要あり
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CombinedTweetKey that = (CombinedTweetKey) o;

        if (!tweetId.equals(that.tweetId)) return false;
        return postDate.equals(that.postDate);

    }

    @Override
    public int hashCode() {
        int result = tweetId.hashCode();
        result = 31 * result + postDate.hashCode();
        return result;
    }

}
