package iis.project.User;

import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.User.dto.UpdateProfileDTO;
import iis.project.User.dto.UserInfo;
import iis.project.User.dto.UserInfoDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final UserInfoDTOMapper userInfoDTOMapper;

    /**
     * Get current user
     * @return current user
     */
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new RuntimeException("User ne user nah");
        }
    }
    /**
     * Check if current user has role
     * @param role role to check
     * @return true if user has role
     */
    public boolean checkCurrentUserRole(Role role){
        return getCurrentUser().getRole() == role;
    }
    /**
     * Check if user with this ID exists
     * @param id ID of user to check
     */
    public void checkIfExist(Long id){
        if(!userRepo.existsById(id)){
            throw new ResourceNotFoundException("User with this ID does not exist");
        }
    }
    /**
     * Get user by ID
     * @param userId ID of user
     * @return user with this ID
     */
    public User getById(Long userId) {
        return userRepo.getReferenceById(userId);
    }
    /**
     * Get current user info
     * @return current user info
     */
    public UserInfo getCurrentUserInfo() {
        return userInfoDTOMapper.apply(getCurrentUser());
    }
   /**
     * Get all users by Role
    * @param role Role to get users by
     * @return List of all users
     */
    public List<UserInfo> getAllByRole(Role role) {
        return userRepo.findAllByRole(role)
                .stream()
                .map(userInfoDTOMapper)
                .collect(Collectors.toList());
    }


    /**
     * Save user
     * @param user User to save
     *
     */
    public void save(User user) {
        userRepo.save(user);
    }

    /**
     * Handle user role
     * @param userId ID of user
     *
     */
    public void handleRole(Long userId){
        User user = getById(userId);

        System.out.println(user.getStudiosAsTeacher());
        System.out.println(user.getManagedStudios());

        boolean hasTeacherRoles = user.getStudiosAsTeacher() != null && !user.getStudiosAsTeacher().isEmpty();
        boolean hasManagerRoles = user.getManagedStudios() != null && !user.getManagedStudios().isEmpty();

        if(hasTeacherRoles)
            user.setRole(Role.TEACHER);
        else if (hasManagerRoles)
            user.setRole(Role.STUDIO_MANAGER);
        else user.setRole(Role.USER);

        save(user);
    }

    /**
     * Update user profile
     * @param updateProfileDTO DTO with new user data
     */
    public void update(UpdateProfileDTO updateProfileDTO) {
        User user = getById(updateProfileDTO.userId());
        user.setName(updateProfileDTO.name());
        user.setSurname(updateProfileDTO.surname());
        save(user);
    }

    /**
     * Get user info by ID
     * @param userId ID of user
     * @return user info
     */
    public UserInfo getInfoById(Long userId) {
        return userInfoDTOMapper.apply(getById(userId));
    }
}
