package iis.project.User.dto;

import lombok.Builder;

@Builder
public record UserInfo(
        String name,
        String surname,
        String email
) {
}
