package reactive.com.rest.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.circuitbreaker.CircuitBreaker;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactive.com.dal.RxWhiskyDao;
import reactive.com.model.Whisky;

import java.util.List;

/**
 * Handles fetching all whiskies from persistent storage.
 * <p>
 * Created by RLYBD20 on 22/11/2017.
 */
public class GetAllWhiskiesHandler implements Handler<RoutingContext> {

    private final static Logger LOGGER = LoggerFactory.getLogger(GetAllWhiskiesHandler.class);

    private RxWhiskyDao rxWhiskyDao;
    private CircuitBreaker circuitBreaker;

    public GetAllWhiskiesHandler(RxWhiskyDao rxWhiskyDao, CircuitBreaker circuitBreaker) {
        this.rxWhiskyDao = rxWhiskyDao;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public void handle(RoutingContext event) {
        // TODO : this code has to be investigated more deeply.
        circuitBreaker
                .rxExecuteCommandWithFallback(
                        future -> rxWhiskyDao
                                .fetchAll()
                                .map(entry -> entry.mapTo(Whisky.class))
                                // swallow an error by continuing the flow
                                .doOnError(throwable -> {
                                    LOGGER.warn("Something went wrong with fetching all whiskies : {}", throwable.getMessage());
                                })
                                .toList()
                                .subscribe(new ConsumerOnSuccess(event)),
                        fallback -> {
                            LOGGER.warn("Fallback is executed. Details : {}", fallback.getMessage());
                            new ConsumerOnFailure(event).accept(fallback);
                            return new JsonObject().put("message", "Post your issue to our portal.");
                        })
        .subscribe();
    }

    private class ConsumerOnFailure implements Consumer<Throwable> {
        private final RoutingContext event;

        ConsumerOnFailure(RoutingContext event) {
            this.event = event;
        }

        @Override
        public void accept(Throwable throwable) {
            LOGGER.error("Error has occurred while fetching whiskies. Details : {}", throwable.getMessage());
            event
                    .response()
                    .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                    .setStatusMessage(throwable.getMessage())
                    .end();
        }
    }

    private class ConsumerOnSuccess implements Consumer<List<Whisky>> {
        private final RoutingContext event;

        ConsumerOnSuccess(RoutingContext event) {
            this.event = event;
        }

        @Override
        public void accept(List<Whisky> whiskies) throws Exception {
            LOGGER.debug("{} of whiskies has(ve) been fetched successfully.", whiskies.size());
            LOGGER.trace("Collection of whiskies that were retrieved: \n {}", whiskies);
            event
                    .response()
                    .setStatusCode(HttpResponseStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    // FIXME : get rif of replace method here.
                    .end(Json.encodePrettily(whiskies).replace("_", ""));
        }
    }
}
