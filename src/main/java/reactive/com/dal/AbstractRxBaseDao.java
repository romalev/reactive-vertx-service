package reactive.com.dal;


import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.mongo.MongoClient;

/**
 * Mongo DB specific rx-implementation of basic crud operations.
 * <p>
 * Created by RLYBD20 on 14/11/2017.
 */
public abstract class AbstractRxBaseDao implements RxBaseDao<JsonObject, Flowable, Single, String> {

    // mongo db internal _id field which can't be overridden.
    private static final String MONGODB_ID = "_id";

    /**
     * Returns a mongo client - some sort of persistence manager of MongoDB.
     */
    protected abstract MongoClient getClient();

    /**
     * Returns a name of collection. !Collections are analogous to tables in relational databases.!
     */
    protected abstract String getCollection();

    @Override
    public Single<JsonObject> persist(JsonObject entity) {
        return getClient()
                .rxSave(getCollection(), entity)
                // note: we're not really interested in internal id being returned by mongoDB.
                .map(s -> entity);
    }

    @Override
    public Single<JsonObject> getById(String id) {
        final JsonObject query = new JsonObject().put(MONGODB_ID, id);
        return getClient()
                .rxFindOne(getCollection(), query, new JsonObject());
    }

    @Override
    public Single<JsonObject> update(JsonObject entity, String id) {
        final JsonObject query = new JsonObject().put(MONGODB_ID, id);
        return getClient()
                .rxFindOneAndReplace(getCollection(), query, entity);
    }

    @Override
    public Flowable<JsonObject> fetchAll() {
        return getClient()
                .rxFind(getCollection(), new JsonObject())
                .toFlowable()
                .flatMap(Flowable::fromIterable);
    }

    public Single<JsonObject> remove(String id) {
        return getClient()
                .rxFindOneAndDelete(getCollection(), new JsonObject().put("_id", id));
    }
}
