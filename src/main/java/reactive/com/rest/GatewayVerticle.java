package reactive.com.rest;

import io.vertx.core.Handler;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import reactive.com.config.EndpointConfigProvider;

/**
 * Represents REST - based gateway that handles all incoming requests.
 * <p>
 * Created by RLYBD20 on 8/11/2017.
 */
public class GatewayVerticle extends AbstractVerticle implements EndpointConfigProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayVerticle.class.getName());

    @Value("${vertx.http.port}")
    private int vertxHttpPort;
    @Value("${assets.folder.location}")
    private String assetsLocation;

    private Handler<RoutingContext> addNewWhiskyHandler;
    private Handler<RoutingContext> getAllWhiskiesHandler;
    private Handler<RoutingContext> updateWhiskyHandler;
    private Handler<RoutingContext> deleteWhiskyHandler;

    public GatewayVerticle(Handler<RoutingContext> addNewWhiskyHandler,
                           Handler<RoutingContext> getAllWhiskiesHandler,
                           Handler<RoutingContext> updateWhiskyHandler,
                           Handler<RoutingContext> deleteWhiskyHandler) {
        this.addNewWhiskyHandler = addNewWhiskyHandler;
        this.getAllWhiskiesHandler = getAllWhiskiesHandler;
        this.updateWhiskyHandler = updateWhiskyHandler;
        this.deleteWhiskyHandler = deleteWhiskyHandler;
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("Starting {} verticle.", GatewayVerticle.class.getName());

        final HttpServer httpServer = vertx.createHttpServer();

        final Router router = Router.router(vertx);

        handleVerticleRouting(router);

        httpServer
                .requestHandler(router::accept)
                .rxListen(vertxHttpPort)
                .subscribe(listener -> {
                    LOGGER.info("{} listens on port {}", GatewayVerticle.class.getName(), listener.actualPort());
                }, error -> {
                    LOGGER.error("Error has occurred while trying to listen to {}", vertxHttpPort);
                });
    }

    public void handleVerticleRouting(final Router vertxRouter) {
        // assets
        StaticHandler assets = StaticHandler.create(assetsLocation);
        vertxRouter.route("/assets/*").handler(assets);
        // this one need to read the request’s body. For performance reason, it should be explicitly enabled
        vertxRouter.route("/api/whiskies*").handler(BodyHandler.create());
        // adding new whisky
        vertxRouter.post(Whisky.ADD).handler(addNewWhiskyHandler);
        // getting all whiskies
        vertxRouter.get(Whisky.GET_ALL).handler(getAllWhiskiesHandler);
        // update existing whisky
        vertxRouter.put(Whisky.UPDATE).handler(updateWhiskyHandler);
        //  remove existing whisky
        vertxRouter.delete(Whisky.DELETE).handler(deleteWhiskyHandler);
        // for testing purposes.
        vertxRouter.get("/api/test/").handler(event -> {
            event.response().end("testMessage");
        });
    }
}
