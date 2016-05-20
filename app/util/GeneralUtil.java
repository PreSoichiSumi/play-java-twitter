package util;

import com.avaje.ebean.PagedList;
import models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by s-sumi on 2016/05/10.
 */
public class GeneralUtil {
    public static String sha512(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        StringBuilder sb = new StringBuilder();
        md.update(message.getBytes());
        byte[] mb = md.digest();
        for (byte m : mb) {
            String hex = String.format("%02x", m);
            sb.append(hex);
        }
        return sb.toString();
    }
    public static List<User> getRecomUserList(String user_id, Integer pagenum){
        PagedList<User> recomUsers=User.find.where().ne("user_id",user_id).findPagedList(0,3);
        return recomUsers.getList();
    }


}
