import com.google.inject.Inject;
import filters.CustomizedFilter;
import play.Environment;
import play.Mode;
import play.filters.csrf.CSRFFilter;
import play.filters.gzip.GzipFilter;
import play.http.HttpFilters;
import play.mvc.EssentialFilter;

import javax.inject.Singleton;

/**
 * ルートパッケージにあるFilterクラス(つまりこのクラス)にかかれている処理は
 * 全てのリクエストに対して行われる．
 * 他にfilterを追加したければ，同様のクラスを作成し，application.confに
 * play.http.filtersに設定を追加すれば適用される
 *
 * @param env Basic environment settings for the current application.
 * each response.
 */
@Singleton
public class Filters implements HttpFilters {

    private final Environment env;

    @Inject
    GzipFilter gzipFilter;

    @Inject
    CustomizedFilter customizedFilter;

    @Inject
    CSRFFilter csrfFilter;


    @Inject
    public Filters(Environment env) {
        this.env = env;
    }

    @Override
    public EssentialFilter[] filters() {
      // Use the example filter if we're running development mode. If
      // we're running in production or test mode then don't use any
      // filters at all.
      if (env.mode().equals(Mode.DEV)) {
          return new EssentialFilter[]{gzipFilter.asJava(), csrfFilter.asJava()};
      } else {
          return new EssentialFilter[]{customizedFilter, gzipFilter.asJava(), csrfFilter.asJava()};
      }
    }

}
