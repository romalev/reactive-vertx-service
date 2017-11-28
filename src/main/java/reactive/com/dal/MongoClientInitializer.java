package reactive.com.dal;

import io.reactivex.annotations.NonNull;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactive.com.Application;

import javax.annotation.PostConstruct;

/**
 * Responsible for getting the mongo client properly initialized.
 * <p>
 * Created by RLYBD20 on 17/11/2017.
 */
@Component
public class MongoClientInitializer {

    private final static Logger LOGGER = LoggerFactory.getLogger(MongoClientInitializer.class.getName());

    @NonNull
    private JsonObject config;
    @NonNull
    private MongoClient mongoClient;

    @Value("${mongodb.connection.string}")
    private String connectionString;
    @Value("${mongodb.database.name}")
    private String databaseName;

    @PostConstruct
    public void initConfig() {
        config = new JsonObject()
                .put("connection_string", connectionString)
                .put("db_name", databaseName);
        try {
            mongoClient = MongoClient.createNonShared(Application.getVertx(), config);
            LOGGER.info("Mongo client has been initialized correctly with connection string: {} and db name: {}",
                    connectionString, databaseName);
        } catch (final Exception exception) {
            LOGGER.error("Could NOT initialize mongo client with given connection string: {} and db name: {}. " +
                            "Exception details : {}",
                    connectionString, databaseName, exception.getMessage());
            // shutting down the application entirely.
            Application.getAppContext().close();
        }
    }


    public JsonObject getConfig() {
        return config;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
}
