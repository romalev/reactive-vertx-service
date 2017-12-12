package reactive.com.rest.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactive.com.dal.RxWhiskyDao;

/**
 * Responsible for adding new whisky to persistent storage.
 * <p>
 * Created by RLYBD20 on 22/11/2017.
 */
public class AddNewWhiskyHandler implements Handler<RoutingContext> {

    private final static Logger LOGGER = LoggerFactory.getLogger(AddNewWhiskyHandler.class);

    private RxWhiskyDao rxWhiskyDao;

    public AddNewWhiskyHandler(RxWhiskyDao rxWhiskyDao) {
        this.rxWhiskyDao = rxWhiskyDao;
    }

    @Override
    public void handle(RoutingContext event) {
        Observable
                .just(event)
                .map(RoutingContext::getBodyAsString)
                // logging incoming request
                .doOnNext(e -> LOGGER.debug("Trying to add \n {}", e))
                .map(JsonObject::new)
                // persisting an item
                .flatMap(jsonObject -> rxWhiskyDao.persist(jsonObject).toObservable())
                .subscribe(new ConsumerOnSuccess(event), new ConsumerOnFailure(event));
    }

    /**
     * Builds a http response in case of success.
     */
    private class ConsumerOnSuccess implements Consumer<JsonObject> {
        private RoutingContext event;

        public ConsumerOnSuccess(RoutingContext event) {
            this.event = event;
        }

        @Override
        public void accept(JsonObject jsonObject) throws Exception {
            LOGGER.debug("Entity has been persisted successfully: {}", jsonObject);
            event.response()
                    .setStatusCode(HttpResponseStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(jsonObject));
        }
    }

    /**
     * Builds a http response in case of failure.
     */
    private class ConsumerOnFailure implements Consumer<Throwable> {
        private RoutingContext event;

        public ConsumerOnFailure(RoutingContext event) {
            this.event = event;
        }

        @Override
        public void accept(Throwable throwable) throws Exception {
            LOGGER.error("Could not persist an entity. Details : {}", throwable.getMessage());
            event.response()
                    .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                    .setStatusMessage(throwable.getMessage())
                    .end(throwable.getMessage());
        }
    }
}
