package iis.project.User;

public record UpdateProfileDTO(
        Long userId,
        String name,
        String surname
) {
}
