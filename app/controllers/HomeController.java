package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Login;
import models.Secured;
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
import play.mvc.Security;
import util.ConvertionUtil;
import views.html.index;
import views.html.login;
import views.html.register;

import javax.inject.Inject;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static util.GeneralUtil.sha512;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    @Inject
    WebJarAssets webJarAssets;
    @Inject
    FormFactory formfactory;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    @Security.Authenticated(models.Secured.class)
    public Result index() {
        return ok(index.render());
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
            session("username", f.get().getUserId());

            String returnUrl = ctx().session().get("returnUrl");
            if (returnUrl == null || Objects.equals(returnUrl, "")
                    || Objects.equals(returnUrl, routes.HomeController.loginPage().absoluteURL(request()))) {
                returnUrl = routes.HomeController.index().url();
            }
            return redirect(returnUrl);
        }
    }

    @Security.Authenticated(Secured.class)
    public Result logout() {
        session().clear();
        return redirect(routes.HomeController.loginPage());
    }

    public Result registerPage() {
        Form<User> f = formfactory.form(User.class);
        return ok(register.render(f));
    }

    public Result register() throws NoSuchAlgorithmException {
        Form<User> f = formfactory.form(User.class).bindFromRequest();
        if (!f.hasErrors()) {
            User u = new User(f.get().userId, sha512(f.get().password));
            try {
                u.save();
            } catch (Exception e) {
                e.printStackTrace();
                return redirect(routes.HomeController.registerPage());
            }
            return redirect(routes.HomeController.loginPage());
        } else {
            return redirect(routes.HomeController.registerPage());
        }


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
