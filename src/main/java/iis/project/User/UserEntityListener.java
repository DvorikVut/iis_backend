package iis.project.User;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class UserEntityListener {
    @PostLoad
    @PrePersist
    @PreUpdate
    public void assignRoleBasedOnStudios(User user) {
        boolean hasTeacherStudios = user.getStudiosAsTeacher() != null && !user.getStudiosAsTeacher().isEmpty();
        boolean hasManagerStudios = user.getManagedStudios() != null && !user.getManagedStudios().isEmpty();

        if(user.getRole().equals(Role.ADMIN)) return;

        if (hasTeacherStudios) {
            user.setRole(Role.TEACHER);
        } else if (hasManagerStudios){
            user.setRole(Role.STUDIO_MANAGER);
        } else {
            user.setRole(Role.USER);
        }
    }
}
