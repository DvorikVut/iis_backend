package iis.project.User.dto;

import lombok.Builder;

@Builder
public record UserInfo(
        Long id,
        String name,
        String surname,
        String email
) {
}
