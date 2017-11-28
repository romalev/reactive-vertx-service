package reactive.com.dal;

import io.vertx.reactivex.ext.mongo.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Specific rx-implementation of mongo db base dao -> Whisky.
 * <p>
 * Created by RLYBD20 on 14/11/2017.
 */
@Repository
public class RxWhiskyDao extends AbstractRxBaseDao {

    private static final String COLLECTION_NAME = "Whiskies";

    @Autowired
    private MongoClientInitializer mongoClientInitializer;

    @Override
    protected MongoClient getClient() {
        return mongoClientInitializer.getMongoClient();
    }

    @Override
    protected String getCollection() {
        return COLLECTION_NAME;
    }
}
