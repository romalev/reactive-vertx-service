package reactive.com.front.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactive.com.dal.RxWhiskyDao;
import reactive.com.model.Whisky;

import java.util.List;

/**
 * Handles fetching all whiskies from persistent storage.
 * <p>
 * Created by RLYBD20 on 22/11/2017.
 */
@Component
public class GetAllWhiskiesHandler implements Handler<RoutingContext> {

    private final static Logger LOGGER = LoggerFactory.getLogger(GetAllWhiskiesHandler.class);

    @Autowired
    private RxWhiskyDao rxWhiskyDao;

    @Override
    public void handle(RoutingContext event) {
        rxWhiskyDao
                .fetchAll()
                .map(entry -> entry.mapTo(Whisky.class))
                // swallow an error by continuing the flow
                .onErrorResumeNext(throwable -> {
                    LOGGER.warn("Something went wrong with fetching all whiskies. {}", throwable);
                    return Flowable.empty();
                })
                .toList()
                .subscribe(new ConsumerOnSuccess(event), new ConsumerOnFailure(event));
    }

    private class ConsumerOnFailure implements Consumer<Throwable> {
        private final RoutingContext event;

        public ConsumerOnFailure(RoutingContext event) {
            this.event = event;
        }

        @Override
        public void accept(Throwable throwable) throws Exception {
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

        public ConsumerOnSuccess(RoutingContext event) {
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
