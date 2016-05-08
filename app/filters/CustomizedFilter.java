package filters;

import akka.stream.Materializer;
import play.mvc.Filter;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;

import static play.mvc.Http.HeaderNames.CACHE_CONTROL;
import static play.mvc.Http.HeaderNames.EXPIRES;


/**
 * This is a simple filter that adds a header to all requests. It's
 * added to the application's list of filters by the
 * {@link ExampleFilters} class.
 *
 * @param mat  This object is needed to handle streaming of requests
 *             and responses.
 * @param exec This class is needed to execute code asynchronously.
 *             It is used below by the <code>thenAsyncApply</code> method.
 */
@Singleton
public class CustomizedFilter extends Filter {

    private final Executor exec;

    @Inject
    public CustomizedFilter(Materializer mat, Executor exec) {
        super(mat);
        this.exec = exec;
    }

    @Override
    public CompletionStage<Result> apply(
            Function<RequestHeader, CompletionStage<Result>> next,
            RequestHeader requestHeader) {

        return next.apply(requestHeader).thenApplyAsync(
                result -> result.withHeaders(CACHE_CONTROL, "public, max-age=3600",
                        EXPIRES, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("GMT")).plusWeeks(1))),
                exec
        );
    }

}
