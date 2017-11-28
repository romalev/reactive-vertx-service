package reactive.com.exceptions;

/**
 * Represents a general exception which might happen within application.
 * <p>
 * Created by RLYBD20 on 27/11/2017.
 */
public class ApplicationException extends RuntimeException {

    private Integer code;

    public ApplicationException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public ApplicationException(String message) {
        super(message);
    }

}
