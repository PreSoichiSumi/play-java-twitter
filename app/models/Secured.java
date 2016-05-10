package models;

import controllers.routes;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by s-sumi on 2016/05/10.
 */
public class Secured extends Security.Authenticator {
    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.session().get("username");
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        String returnUrl = ctx.request().uri();
        if (returnUrl == null)
            returnUrl = "/";
        ctx.session().put("returnUrl", returnUrl);
        return redirect(routes.HomeController.loginPage());
    }
}
