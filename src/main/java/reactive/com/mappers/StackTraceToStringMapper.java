package reactive.com.mappers;

import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Maps stack trace elements to string. Ie. - builds a more nicer message out of throwable.
 * <p>
 * Created by RLYBD20 on 27/11/2017.
 */
@Component
public class StackTraceToStringMapper implements Mapper<Throwable, String> {

    @Override
    public String map(Throwable t) {
        // NOTE -> java io.
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
