package reactive.com.config;

/**
 * Provides all available to application rest endpoints.
 * <p>
 * Created by RLYBD20 on 13/11/2017.
 */
public interface EndpointConfigProvider {

    interface Whisky {
        String GET_ALL = "/api/whiskies";
        String ADD = "/api/whiskies";
        String UPDATE = "/api/whiskies/:id";
        String DELETE = "/api/whiskies/:id";
        String GET_BY_ID = "TODO";
    }
}
