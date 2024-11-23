package iis.project.User.dto;

import iis.project.Studio.dto.StudioInfo;
import iis.project.User.Role;
import lombok.Builder;

import java.util.List;

@Builder
public record UserInfo(
        Long id,
        String name,
        String surname,
        String email,
        Role role
) {
}
