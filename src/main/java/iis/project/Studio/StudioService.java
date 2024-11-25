package iis.project.Studio;

import iis.project.Device.Device;
import iis.project.Device.DeviceService;
import iis.project.Exceptions.NotAuthorizedException;
import iis.project.Exceptions.ResourceAlreadyExistException;
import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.Studio.dto.NewStudioDTO;
import iis.project.Studio.dto.StudioInfo;
import iis.project.Studio.dto.StudioInfoDTOMapper;
import iis.project.User.Role;
import iis.project.User.User;
import iis.project.User.UserService;
import iis.project.User.dto.UserInfo;
import iis.project.User.dto.UserInfoDTOMapper;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StudioService {
    private final StudioRepository studioRepository;
    private final UserService userService;
    private final DeviceService deviceService;
    private final StudioInfoDTOMapper studioInfoDTOMapper;
    private final UserInfoDTOMapper userInfoDTOMapper;

    public StudioService(@Lazy DeviceService deviceService, @Lazy UserService userService, @Lazy StudioRepository studioRepository, @Lazy  StudioInfoDTOMapper studioInfoDTOMapper, @Lazy UserInfoDTOMapper userInfoDTOMapper){
        this.studioRepository = studioRepository;
        this.userService = userService;
        this.deviceService = deviceService;
        this.studioInfoDTOMapper = studioInfoDTOMapper;
        this.userInfoDTOMapper = userInfoDTOMapper;
    }

    /**
     * Create new studio
     * @param newStudioDTO DTO with studio data
     * @return created studio
     */
    public Studio create(NewStudioDTO newStudioDTO){
        if(!userService.checkCurrentUserRole(Role.ADMIN)){
            throw new NotAuthorizedException("You are not authorized to create studio");
        }
        Studio studio = Studio.builder()
                .name(newStudioDTO.name())
                .build();
        save(studio);
        setManager(newStudioDTO.userId(), studio.getId());
        return studio;
    }
    /**
     * Save studio
     * @param studio Studio to save
     * @return saved studio
     */
    public Studio save(Studio studio){
        return studioRepository.save(studio);
    }

    /**
     * Delete studio
     * @param studio_id ID of studio to delete
     */
    @Transactional
    public void delete(Long studio_id){
        Studio studio = getById(studio_id);
        if(!userService.checkCurrentUserRole(Role.ADMIN)){
            throw new NotAuthorizedException("You are not authorized to delete studio");
        }

        checkIfExist(studio_id);
        List<Device> devicesInStudio = deviceService.getAllRawByStudioId(studio_id);

        for(Device device : devicesInStudio){
            deviceService.delete(device.getId());
        }
        removeManager(studio_id);
        studioRepository.deleteById(studio_id);
    }

    /**
     * Change studio
     * @param studio_id ID of studio to change
     * @param newStudioDTO DTO with new studio data
     * @return changed studio
     */
    public Studio change(Long studio_id, NewStudioDTO newStudioDTO){
        if(!userService.checkCurrentUserRole(Role.ADMIN))
            throw new NotAuthorizedException("You are not authorized to change studio");

        checkIfExist(studio_id);

        Studio studio = getById(studio_id);
        studio.setName(newStudioDTO.name());
        studioRepository.save(studio);
        return studio;
    }
    /**
     * Check if studio exist
     * @param studio_id ID of studio
     */
    public void checkIfExist(Long studio_id){
        if(!studioRepository.existsById(studio_id)){
            throw new ResourceNotFoundException("Studio with ID " + studio_id +" does not exist");
        }
    }
    /**
     * Get all studios
     * @return List of studios
     */
    public List<Studio> getAll() {
        return studioRepository.findAll();
    }
    /**
     * Get studio by ID
     * @param id ID of studio
     * @return Studio
     */
    public Studio getById(Long id) {
        return studioRepository.getReferenceById(id);
    }
    /**
     * Get studio info by ID
     * @param id ID of studio
     * @return StudioInfo
     */
    public StudioInfo getInfoById(Long id){
        return studioInfoDTOMapper.apply(studioRepository.getReferenceById(id));
    }
    /**
     * Check if user is manager of studio
     * @param user_id ID of user
     * @param studio_id ID of studio
     * @return true if user is manager of studio
     */
    public boolean checkIfUserIsManager(Long user_id, Long studio_id){
        checkIfExist(studio_id);
        userService.checkIfExist(user_id);
        Studio studio = getById(studio_id);
        return Objects.equals(studio.getManager().getId(), user_id);
    }
    /**
     * Get all users info by studio ID
     * @param studioId ID of studio
     * @return List of UserInfo
     */
    public List<UserInfo> getAllUsersInfoByStudioId(Long studioId){
        return getById(studioId).getUsers()
                .stream()
                .map(userInfoDTOMapper)
                .collect(Collectors.toList());
    }
    /**
     * Get all teachers info by studio ID
     * @param studioId ID of studio
     * @return List of UserInfo
     */
    public List<UserInfo> getAllTeachersInfoByStudioId(Long studioId){
        return getById(studioId).getTeachers()
                .stream()
                .map(userInfoDTOMapper)
                .collect(Collectors.toList());
    }

    /**
     * Set manager to studio
     * @param userId ID of user
     * @param studioId ID of studio
     */
    @Transactional
    public void setManager(Long userId, Long studioId){
        if(!userService.checkCurrentUserRole(Role.ADMIN))
            throw new NotAuthorizedException("You are not authorized to add manager to studio");
        userService.checkIfExist(userId);
        checkIfExist(studioId);
        Studio studio = getById(studioId);
        User user = userService.getById(userId);
        studio.setManager(user);
        save(studio);
        deleteUserFromEveryStudiosAsUser(userId);
        userService.handleRole(userId);
        deviceService.allowUserToAllDevicesInStudio(userId,studioId);
    }
    /**
     * Remove manager from studio
     * @param studio_id ID of studio
     */
    @Transactional
    public void removeManager(Long studio_id){
        if(!userService.checkCurrentUserRole(Role.ADMIN))
            throw new NotAuthorizedException("You are not authorized to remove manager from studio");
        checkIfExist(studio_id);
        Studio studio = getById(studio_id);
        User user = studio.getManager();
        studio.setManager(null);
        save(studio);
        userService.handleRole(user.getId());
        deviceService.removeUserFromUserAccess(user.getId(), studio_id);
    }
    /**
     * Remove user from studio
     * @param studio_id ID of studio
     * @param user_id ID of user
     */
    @Transactional
    public void removeUser(Long studio_id, Long user_id){
        if(!((userService.checkCurrentUserRole(Role.ADMIN)) || (userService.checkCurrentUserRole(Role.STUDIO_MANAGER))))
            throw new NotAuthorizedException("You are not authorized to remove users from studio");
        checkIfExist(studio_id);
        userService.checkIfExist(user_id);
        Studio studio = getById(studio_id);
        User user = userService.getById(user_id);
        studio.getUsers().remove(user);
        save(studio);
        userService.handleRole(user.getId());
        deviceService.removeUserFromUserAccess(user.getId(), studio_id);
    }
    /**
     * Add user to studio
     * @param studioId ID of studio
     * @param userId ID of user
     */
    @Transactional
    public void addUser(Long studioId, Long userId){
        if(!((userService.checkCurrentUserRole(Role.ADMIN)) || (userService.checkCurrentUserRole(Role.STUDIO_MANAGER))))
            throw new NotAuthorizedException("You are not authorized to add users to the studio");

        checkIfExist(studioId);
        userService.checkIfExist(userId);
        Studio studio = getById(studioId);
        User user = userService.getById(userId);
        if(studio.getUsers().contains(user))
            throw new ResourceAlreadyExistException("User " + user.getEmail() + "is already in studio " + studio.getName());

        studio.getUsers().add(user);
        save(studio);
        userService.handleRole(userId);
        deviceService.allowUserToAllDevicesInStudio(userId,studioId);
    }
    /**
     * Add teacher to studio
     * @param userId ID of user
     * @param studioId ID of studio
     */
    @Transactional
    public void addTeacher(Long userId, Long studioId) {
        Studio studio = getById(studioId);
        User newTeacher = userService.getById(userId);
        if(!userService.getCurrentUser().getId().equals(studio.getManager().getId())
                        && !userService.checkCurrentUserRole(Role.ADMIN))
            throw new NotAuthorizedException("You are not allowed to add teachers to this studio");
        deleteUserFromEveryStudiosAsUser(userId);
        studio.getTeachers().add(newTeacher);
        save(studio);
        userService.handleRole(userId);
        deviceService.allowUserToAllDevicesInStudio(userId,studioId);
    }
    /**
     * Remove teacher from studio
     * @param userId ID of user
     * @param studioId ID of studio
     */
    @Transactional
    public void removeTeacher(Long userId, Long studioId){
        Studio studio = getById(studioId);
        User user = userService.getById(userId);
        if(!userService.getCurrentUser().getId().equals(studio.getManager().getId())
                && !userService.checkCurrentUserRole(Role.ADMIN))
            throw new NotAuthorizedException("You are not allowed to remove teachers from this studio");
        studio.getTeachers().remove(user);
        save(studio);
        userService.handleRole(userId);
        deviceService.removeUserFromUserAccess(userId,studioId);
    }
    /**
     * Get all studios by user
     * @return List of StudioInfo
     */
    public List<StudioInfo> getAllByUser(){
        User user = userService.getCurrentUser();
        Role userRole = user.getRole();

        switch (userRole){
            case USER -> {
                return getAllByUserId(user.getId());
            }
            case TEACHER -> {
                return  getAllByTeacherId(user.getId());
            }
            case STUDIO_MANAGER -> {
                return getAllByManagerId(user.getId());
            }
            case ADMIN -> {
                return getAll()
                        .stream()
                        .map(studioInfoDTOMapper)
                        .collect(Collectors.toList());
            }
            default -> throw new RuntimeException("Invalid User Role");
        }
    }
    /**
     * Get all studios by user ID
     * @param userId ID of user
     * @return List of StudioInfo
     */
    public List<StudioInfo> getAllByUserId(Long userId) {
        User user = userService.getById(userId);
        return studioRepository.findAllByUsersContaining(user)
                .stream()
                .map(studioInfoDTOMapper)
                .collect(Collectors.toList());
    }
    /**
     * Get all studios by teacher ID
     * @param teacherId ID of teacher
     * @return List of StudioInfo
     */
    public List<StudioInfo> getAllByTeacherId(Long teacherId) {
        User user = userService.getById(teacherId);
        return studioRepository.findAllByTeachersContaining(user)
                .stream()
                .map(studioInfoDTOMapper)
                .collect(Collectors.toList());
    }
    /**
     * Get all studios by manager ID
     * @param managerId ID of manager
     * @return List of StudioInfo
     */
    public List<StudioInfo> getAllByManagerId(Long managerId) {
        return studioRepository.findAllByManagerId(managerId)
                .stream()
                .map(studioInfoDTOMapper)
                .collect(Collectors.toList());
    }
    /**
     * Delete user from every studio as user
     * @param userId ID of user
     */
    @Transactional
    public void deleteUserFromEveryStudiosAsUser(Long userId){
        User user = userService.getById(userId);
        List<Studio> studios = studioRepository.findAllByUsersContaining(user);
        for(Studio studio : studios){
            studio.getUsers().remove(user);
            save(studio);
        }
    }
}
