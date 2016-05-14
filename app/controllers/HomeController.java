package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Login;
import models.Secured;
import models.Tweet;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import util.ConvertionUtil;
import views.html.*;

import javax.inject.Inject;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static util.GeneralUtil.sha512;

public class HomeController extends Controller {
    @Inject
    WebJarAssets webJarAssets;
    @Inject
    FormFactory formfactory;

    @Security.Authenticated(models.Secured.class)
    public Result index() {
        User u = User.find.where()
                .eq("user_id", session("user_id"))
                .findUnique();
        List<Tweet> list = u == null ? new ArrayList<>() :
                (u.tweets == null ? new ArrayList<>() : u.tweets);
        return ok(index.render(list));
    }

    //セッションにCSRFトークンを格納
    @AddCSRFToken
    public Result loginPage() {
        Form<Login> f = formfactory.form(Login.class);
        return ok(login.render(f));
    }

    //セッションに正しいCSRFトークンが格納されていないとリクエストを受け付けない(?)
    @RequireCSRFCheck
    public Result authenticate() { //dynamicform.get()...return null if the key does not exist
        Form<Login> f = formfactory.form(Login.class).bindFromRequest();  //dynamicformはモデルに関係しないフォームデータを扱う場合に使用する
        if (f.hasErrors()) {
            return badRequest(login.render(f));
        } else {
            //playはデフォルトでセッションにusernameが格納されているかでログイン状態を判断．
            //これに加えてセッションにCSRFTokenが正しく格納されているかでログイン状態を安全に判定できる
            String userId = f.get().getUserId();
            session("username", userId);

            //マイページなど用にユーザデータをセッションに格納
            try {
                User u = User.find.where()
                        .eq("user_id", userId).findUnique();

                //このnullチェックについて．session(key)の仕様を考えればまあわかる
                //http://webcache.googleusercontent.com/search?
                // q=cache:fSj3W9xRfswJ:microscopium.eyesaac.com/2015/11/27/play2redirect/+&cd=3
                // &hl=ja&ct=clnk&gl=jp
                if (u.user_id != null)
                    session("user_id", u.user_id);

                if (u.user_name != null)
                    session("user_name", u.user_name);

                if (u.biography != null)
                    session("biography", u.biography);

            } catch (Exception e) {
                e.printStackTrace();
                session().clear();
                return redirect(routes.HomeController.loginPage());
            }
            String returnUrl = ctx().session().get("returnUrl");
            if (returnUrl == null || Objects.equals(returnUrl, "")
                    || Objects.equals(returnUrl, routes.HomeController.loginPage().absoluteURL(request()))) {
                returnUrl = routes.HomeController.index().url();
            }
            return redirect(returnUrl);
        }
    }

    @Security.Authenticated(models.Secured.class)
    public Result tweetPage() {
        Form<Tweet> f = formfactory.form(Tweet.class);
        return ok(tweet.render(f));
    }

    @RequireCSRFCheck
    public Result tweet() {
        Form<Tweet> f = formfactory.form(Tweet.class).bindFromRequest();
        if (!f.hasErrors()) {
            Tweet t = new Tweet(f.get().content);
            try {
                t.user = User.find.where().eq("user_id", session("user_id")).findUnique();
                t.save();
            } catch (Exception e) {
                e.printStackTrace();
                //return redirect(routes.HomeController.tweetPage());
                return internalServerError(tweet.render(f));
            }
            return redirect(routes.HomeController.index());
        } else {
            return redirect(routes.HomeController.tweetPage());
        }
    }

    @Security.Authenticated(models.Secured.class)
    public Result myPage() {
        Form<User> f = formfactory.form(User.class);
        return ok(mypage.render(f));
    }

    public Result registerPage() {
        Form<User> f = formfactory.form(User.class);
        return ok(register.render(f));
    }

    public Result register() throws NoSuchAlgorithmException {
        Form<User> f = formfactory.form(User.class).bindFromRequest();
        if (!f.hasErrors()) {
            User u = new User(f.get().user_id, sha512(f.get().password));
            try {
                u.save();
            } catch (Exception e) {
                e.printStackTrace();
                return Results.badRequest(register.render(f));
            }
            return redirect(routes.HomeController.loginPage());
        } else {
            return Results.badRequest(register.render(f));
        }
    }

    @Security.Authenticated(Secured.class)
    public Result logout() {
        session().clear();
        return redirect(routes.HomeController.loginPage());
    }


    /**
     * ファイルはフォームデータの一部として送られてこない．
     * フォームのデータに添付されて送られてくる．
     * http://stackoverflow.com/questions/9452375/how-to-get-the-upload-file-with-other-inputs-in-play2#9587052
     *
     * @return
     */
    public Result aaConvert() {
        MultipartFormData.FilePart picture = request().body().asMultipartFormData().getFile("picture");
        //Map<String,String[]> form= request().body(). asMultipartFormData().asFormUrlEncoded();//checkboxは値がないときにはmapに要素すら無いので注意
        DynamicForm form = formfactory.form().bindFromRequest();
        if (picture != null) {
            File file = (File) picture.getFile();

            String aa = ConvertionUtil.aaConvertion(file, form);
            ObjectNode result = Json.newObject();
            result.put("aa", aa);
            return ok(result);
        }
        return badRequest("picture is null");
    }

    public boolean isPicture(File file) {
        String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if (file.isFile() && (
                ext.equals("png") || ext.equals("jpeg") || ext.equals("jpg")))
            return true;
        return false;
    }


}
