package iis.project.Auth;

import iis.project.User.Role;
import lombok.Builder;
import lombok.Data;

@Builder
public record AuthResponse(
        Long id,
        String surname,
        String name,
        String token,
        String email,
        Role role
) {
}
