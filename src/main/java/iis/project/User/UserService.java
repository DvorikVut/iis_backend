package iis.project.User;

import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.Studio.StudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new RuntimeException("User ne user nah");
        }
    }

    public boolean checkCurrentUserRole(Role role){
        return getCurrentUser().getRole() == role;
    }

    public void checkIfExist(Long id){
        if(!userRepo.existsById(id)){
            throw new ResourceNotFoundException("User with this ID does not exist");
        }
    }

    public User getById(Long userId) {
        return userRepo.getReferenceById(userId);
    }
}
