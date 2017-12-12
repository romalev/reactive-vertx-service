package reactive.com;

import io.reactivex.Single;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Application entry point. Doesn't use any embedded servlet containers - instead it creates verticles and deploys them.
 * <p>
 * Created by RLYBD20 on 8/11/2017.
 */
@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"reactive.com"})
public class Application {

    private final static Logger LOGGER = LoggerFactory.getLogger(Application.class.getName());

    private static Vertx vertx;
    private static ConfigurableApplicationContext appContext;

    private AbstractVerticle gatewayVerticle;

    public Application(final AbstractVerticle gatewayVerticle) {
        this.gatewayVerticle = gatewayVerticle;
    }

    public static void main(String[] args) {
        LOGGER.debug("Butting up an reactive app...");
        vertx = Vertx.vertx();

        appContext = SpringApplication.run(Application.class, args);
    }

    public static Vertx getVertx() {
        return vertx;
    }

    public static ConfigurableApplicationContext getAppContext() {
        return appContext;
    }

    @PostConstruct
    public void init() {
        Single<String> deployment = RxHelper.deployVerticle(vertx, gatewayVerticle);
        deployment.subscribe(id -> {
            LOGGER.info("Verticle {} has been successfully deployed. Id is {}", gatewayVerticle.getClass().getName(), id);
        }, err -> {
            LOGGER.error("Error occurred while deploying a verticle {}. Details: {}", gatewayVerticle.getClass().getName(), err.getMessage());
        });
    }

    @PreDestroy
    public void cleanUp() {
        LOGGER.info("Application is about to get shut down.");
        // clean up logic should be placed here.
    }
}
