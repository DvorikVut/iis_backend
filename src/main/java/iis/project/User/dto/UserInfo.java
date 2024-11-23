package iis.project.User.dto;

import iis.project.User.Role;
import lombok.Builder;

@Builder
public record UserInfo(
        Long id,
        String name,
        String surname,
        String email,
        Role role
) {
}
