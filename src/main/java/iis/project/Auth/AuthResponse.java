package iis.project.Auth;

import lombok.Builder;
import lombok.Data;

@Builder
public record AuthResponse(
        String name,
        String token,
        String email
) {
}
