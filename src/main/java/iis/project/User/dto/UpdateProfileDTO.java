package iis.project.User.dto;

public record UpdateProfileDTO(
        Long userId,
        String name,
        String surname
) {
}
