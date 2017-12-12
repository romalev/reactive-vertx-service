package reactive.com.rest.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.functions.Consumer;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactive.com.dal.RxWhiskyDao;
import reactive.com.mappers.Mapper;

import java.util.Objects;

/**
 * Responsible for deleting whiskies from persistent storage.
 * <p>
 * Created by RLYBD20 on 24/11/2017.
 */
public class DeleteWhiskyHandler implements Handler<RoutingContext> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DeleteWhiskyHandler.class);
    private final static String ID = "id";


    private RxWhiskyDao rxWhiskyDao;
    private Mapper<Throwable, String> stackTraceToStringMapper;

    public DeleteWhiskyHandler(RxWhiskyDao rxWhiskyDao, Mapper<Throwable, String> stackTraceToStringMapper) {
        this.rxWhiskyDao = rxWhiskyDao;
        this.stackTraceToStringMapper = stackTraceToStringMapper;
    }

    @Override
    public void handle(RoutingContext event) {
        LOGGER.debug("Trying to remove whisky with id : {}", event.request().getParam(ID));
        rxWhiskyDao
                .remove(event.request().getParam(ID))
                .doOnError(new ConsumerOnFailure(event))
                .doOnSuccess(new ConsumerOnSuccess(event))
                .subscribe();

    }

    private static class ConsumerOnSuccess implements Consumer<JsonObject> {
        private final RoutingContext event;

        ConsumerOnSuccess(RoutingContext event) {
            this.event = event;
        }

        @Override
        public void accept(JsonObject jsonObject) throws Exception {
            if (Objects.isNull(jsonObject) || jsonObject.isEmpty()) {
                LOGGER.warn("Whisky with id {} was not found.", event.request().getParam(ID));
                event.response()
                        .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
                        .setStatusMessage("Whisky was not found.")
                        .end("Whisky was not found.");
            } else {
                LOGGER.debug("Whisky with id {} has been deleted.", event.request().getParam(ID));
                event.response()
                        .setStatusCode(HttpResponseStatus.OK.code())
                        .setStatusMessage("Whisky has been deleted")
                        .end();
            }
        }
    }

    private class ConsumerOnFailure implements Consumer<Throwable> {
        private final RoutingContext event;

        ConsumerOnFailure(RoutingContext event) {
            this.event = event;
        }

        @Override
        public void accept(Throwable throwable) throws Exception {
            LOGGER.error("Error has occurred while removing whisky with id: {}. Details: {}",
                    event.request().getParam(ID), stackTraceToStringMapper.map(throwable));
            event.response()
                    .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                    .setStatusMessage(throwable.getMessage())
                    .end(throwable.getMessage());
        }
    }
}

