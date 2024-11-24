package iis.project.User;

import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.Studio.StudioService;
import iis.project.User.dto.UserInfo;
import iis.project.User.dto.UserInfoDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final UserInfoDTOMapper userInfoDTOMapper;

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
    public UserInfo getCurrentUserInfo() {
        return userInfoDTOMapper.apply(getCurrentUser());
    }
    public List<UserInfo> getAllByRole(Role role) {
        return userRepo.findAllByRole(role)
                .stream()
                .map(userInfoDTOMapper)
                .collect(Collectors.toList());
    }

    public void save(User user) {
        userRepo.save(user);
    }

    public void handleRole(Long userId){
        User user = getById(userId);

        boolean hasTeacherRoles = user.getStudiosAsTeacher() != null && !user.getStudiosAsTeacher().isEmpty();
        boolean hasManagerRoles = user.getManagedStudios() != null && !user.getManagedStudios().isEmpty();

        if(hasTeacherRoles)
            user.setRole(Role.TEACHER);
        else if (hasManagerRoles)
            user.setRole(Role.STUDIO_MANAGER);
        else user.setRole(Role.USER);

        save(user);
    }

    public void update(UpdateProfileDTO updateProfileDTO) {
        User user = getById(updateProfileDTO.userId());
        user.setName(updateProfileDTO.name());
        user.setSurname(updateProfileDTO.surname());
        save(user);
    }
}
