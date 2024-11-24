package iis.project.User;

import iis.project.Logger.MyLogger;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.context.annotation.Bean;


public class UserEntityListener {
    @PostLoad
    @PrePersist
    @PreUpdate
    @Bean
    public void assignRoleBasedOnStudios(User user) {

        System.out.println("Updating role for user: " + user.getId());
        boolean hasTeacherStudios = user.getStudiosAsTeacher() != null && !user.getStudiosAsTeacher().isEmpty();
        boolean hasManagerStudios = user.getManagedStudios() != null && !user.getManagedStudios().isEmpty();

        if(user.getRole().equals(Role.ADMIN)) return;

        if (hasTeacherStudios) {
            System.out.println("Updating role TEACHER for user: " + user.getId());
            user.setRole(Role.TEACHER);
        } else if (hasManagerStudios){
            System.out.println("Updating role MANAGER for user: " + user.getId());
            user.setRole(Role.STUDIO_MANAGER);
        } else {
            user.setRole(Role.USER);
        }
    }
}
