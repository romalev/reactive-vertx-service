package reactive.com.mappers;

/**
 * General interface for mapping things.
 *
 * Created by RLYBD20 on 27/11/2017.
 */
@FunctionalInterface
public interface Mapper<I, O> {
    O map(I input);
}
