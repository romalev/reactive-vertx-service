package reactive.com.dal;

/**
 * Represents a parent class for all basic dao operations. It's written in rx - like manner.
 * Note: Reactive streams implementors can ONLY implement this interface.
 * </p>
 * <ul>
 * <li>E - represents an entity type that is going to be persisted. </li>
 * <li>M - represents a flow of items that is going to be fetched. </li>
 * <li>S - represents a single result of an operation.</li>
 * <li>ID - represents an id of an entity.</li>
 * </ul>
 * <p>
 * Created by RLYBD20 on 10/11/2017.
 */
public interface RxBaseDao<E, M, S, ID> {

    S getById(ID Id);

    S persist(E entity);

    S update(E entity, ID ID);

    M fetchAll();

}
