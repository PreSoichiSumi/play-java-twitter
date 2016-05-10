package models;


import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import java.security.NoSuchAlgorithmException;

import static util.GeneralUtil.sha512;

/**
 * Created by s-sumi on 2016/05/10.
 */
public class Login {
    @Constraints.Required
    @Constraints.Pattern(value = "\\w{4,16}",
            message = "ユーザIDは英数字で構成され、4文字以上16文字以下です")
    private String userId;
    @Constraints.Required
    private String password;

    /**
     * return null if the parameters are valid
     *
     * @return null or error message
     * @throws NoSuchAlgorithmException
     */
    public String validate() throws NoSuchAlgorithmException {
        if (authenticate(userId, password) == null) {
            return "Invalid userId or password";
        }
        return null;
    }

    public static User authenticate(String userId, String password)
            throws NoSuchAlgorithmException {
        Model.Finder<Long, User> find = new Model.Finder<Long, User>(User.class);
        String hashedPassword = "";
        if (password != null) {
            hashedPassword = sha512(password);
        }
        return find.where()
                .eq("user_id", userId)
                .eq("password", hashedPassword)
                .findUnique();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
