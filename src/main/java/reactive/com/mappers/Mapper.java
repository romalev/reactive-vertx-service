package reactive.com.mappers;

/**
 * General interface for mapping things.
 * <p>
 * Created by RLYBD20 on 27/11/2017.
 */
@FunctionalInterface
public interface Mapper<I, O> {
    O map(I input);
}
