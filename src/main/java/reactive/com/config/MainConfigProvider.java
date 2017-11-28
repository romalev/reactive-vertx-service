package reactive.com.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * A bridge between application properties and application itself, represents application.properties.
 * <p>
 * Created by RLYBD20 on 13/11/2017.
 */
@Service
public class MainConfigProvider {
    @Value("${vertx.http.port}")
    private int vertxHttpPort;
    @Value("${assets.folder.location}")
    private String assetsLocation;

    public int getVertxHttpPort() {
        return vertxHttpPort;
    }

    public String getAssetsLocation() {
        return assetsLocation;
    }
}
