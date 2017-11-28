package reactive.com.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.Json;

/**
 * Represents a POJO that is going to be exchanged between front-end and back-end.
 * <p>
 * Created by RLYBD20 on 9/11/2017.
 */
public class Whisky {

    private String id;

    private String name;
    private String origin;

    public Whisky(String name, String origin, String id) {
        this.name = name;
        this.origin = origin;
    }

    // dummy constructor is needed for jackson
    public Whisky() {

    }

    public String getName() {
        return name;
    }

    public String getOrigin() {
        return origin;
    }

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Whisky)) return false;

        Whisky whisky = (Whisky) o;

        if (!getId().equals(whisky.getId())) return false;
        if (!getName().equals(whisky.getName())) return false;
        return getOrigin().equals(whisky.getOrigin());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getOrigin().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Json.encodePrettily(this);
    }
}
