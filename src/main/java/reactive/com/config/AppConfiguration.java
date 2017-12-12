package reactive.com.config;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactive.com.dal.MongoClientInitializer;
import reactive.com.dal.RxWhiskyDao;
import reactive.com.mappers.Mapper;
import reactive.com.mappers.StackTraceToStringMapper;
import reactive.com.rest.GatewayVerticle;
import reactive.com.rest.handlers.AddNewWhiskyHandler;
import reactive.com.rest.handlers.DeleteWhiskyHandler;
import reactive.com.rest.handlers.GetAllWhiskiesHandler;
import reactive.com.rest.handlers.UpdateWhiskyHandler;

/**
 * Contains spring based application configuration.
 * <p>
 * Created by RLYBD20 on 12/12/2017.
 */
@Configuration
public class AppConfiguration {

    @Bean
    public Mapper<Throwable, String> getStackTraceToStringMapper() {
        return new StackTraceToStringMapper();
    }

    @Bean
    public MongoClientInitializer getMongoClientInitializer() {
        return new MongoClientInitializer();
    }

    @Bean
    public RxWhiskyDao getRxWhiskyDao() {
        return new RxWhiskyDao(getMongoClientInitializer());
    }

    @Bean
    public Handler<RoutingContext> getAddNewWhiskyHandler() {
        return new AddNewWhiskyHandler(getRxWhiskyDao());
    }

    @Bean
    public Handler<RoutingContext> getGetAllWhiskiesHandler() {
        return new GetAllWhiskiesHandler(getRxWhiskyDao());
    }

    @Bean
    public Handler<RoutingContext> getUpdateWhiskyHandler() {
        return new UpdateWhiskyHandler(getRxWhiskyDao(), getStackTraceToStringMapper());
    }

    @Bean
    public Handler<RoutingContext> getDeleteWhiskyHandler() {
        return new DeleteWhiskyHandler(getRxWhiskyDao(), getStackTraceToStringMapper());
    }

    @Bean
    public GatewayVerticle getGatewayVerticle() {
        return new GatewayVerticle(getAddNewWhiskyHandler(),
                getGetAllWhiskiesHandler(),
                getUpdateWhiskyHandler(),
                getDeleteWhiskyHandler());
    }
}
