package reactive.com.front.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.functions.Consumer;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactive.com.dal.RxWhiskyDao;
import reactive.com.mappers.Mapper;

/**
 * Responsible for updating whiskies in persistent storage.
 *
 * Created by RLYBD20 on 24/11/2017.
 */
@Component
public class UpdateWhiskyHandler implements Handler<RoutingContext> {

    private final static Logger LOGGER = LoggerFactory.getLogger(UpdateWhiskyHandler.class);
    private final static String ID = "id";

    @Autowired
    private RxWhiskyDao rxWhiskyDao;
    @Autowired
    private Mapper stackTraceToStringMapper;

    @Override
    public void handle(RoutingContext event) {
        LOGGER.debug("Trying to update whisky with id: {}", event.request().getParam(ID));
        rxWhiskyDao
                .update(event.getBodyAsJson(), event.request().getParam(ID))
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
            LOGGER.debug("Entity with id {} has been successfully updated.", jsonObject.getString("_id"));
            event.response()
                    .setStatusCode(HttpResponseStatus.OK.code())
                    .end(jsonObject.encodePrettily());
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
            LOGGER.error("Could update entity with id: {}. Something went wrong : {}",
                    event.request().getParam(ID),
                    stackTraceToStringMapper.map(throwable));
            event.response()
                    .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                    .end("Could update entity. Something went wrong: " + throwable.getMessage());
        }
    }

}