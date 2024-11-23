package iis.project.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Role {
    @JsonProperty("USER")
    USER,
    @JsonProperty("TEACHER")
    TEACHER,
    @JsonProperty("STUDIO_MANAGER")
    STUDIO_MANAGER,
    @JsonProperty("ADMIN")
    ADMIN
}
