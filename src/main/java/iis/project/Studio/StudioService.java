package iis.project.Studio;

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


    public StudioService(@Lazy DeviceService deviceService, @Lazy UserService userService, @Lazy StudioRepository studioRepository,@Lazy  StudioInfoDTOMapper studioInfoDTOMapper, @Lazy UserInfoDTOMapper userInfoDTOMapper){
        this.studioRepository = studioRepository;
        this.userService = userService;
        this.deviceService = deviceService;
        this.studioInfoDTOMapper = studioInfoDTOMapper;
        this.userInfoDTOMapper = userInfoDTOMapper;
    }


    public Studio create(NewStudioDTO newStudioDTO){
        if(!userService.checkCurrentUserRole(Role.ADMIN)){
            throw new NotAuthorizedException("You are not authorized to create studio");
        }
        Studio studio = Studio.builder().name(newStudioDTO.name()).build();
        return save(studio);
    }

    public Studio save(Studio studio){
        return studioRepository.save(studio);
    }
    public void delete(Long studio_id){
        if(!userService.checkCurrentUserRole(Role.ADMIN)){
            throw new NotAuthorizedException("You are not authorized to delete studio");
        }

        checkIfExist(studio_id);
        studioRepository.deleteById(studio_id);
    }

    public Studio change(Long studio_id, NewStudioDTO newStudioDTO){
        if(!userService.checkCurrentUserRole(Role.ADMIN))
            throw new NotAuthorizedException("You are not authorized to change studio");

        checkIfExist(studio_id);

        Studio studio = getById(studio_id);
        studio.setName(newStudioDTO.name());
        studioRepository.save(studio);
        return studio;
    }

    public void setManager(Long user_id, Long studio_id){
        if(!userService.checkCurrentUserRole(Role.ADMIN))
            throw new NotAuthorizedException("You are not authorized to add manager to studio");

        userService.checkIfExist(user_id);
        checkIfExist(studio_id);

        Studio studio = getById(studio_id);
        User user = userService.getById(user_id);
        studio.setManager(user);
        user.setRole(Role.STUDIO_MANAGER);
        save(studio);
    }

    public void removeManager(Long studio_id){
        if(!userService.checkCurrentUserRole(Role.ADMIN))
            throw new NotAuthorizedException("You are not authorized to remove manager from studio");

        checkIfExist(studio_id);
        Studio studio = getById(studio_id);
        studio.getManager().setRole(Role.USER);
        studio.setManager(null);
        save(studio);
    }


    public void removeUser(Long studio_id, Long user_id){
        if(!((userService.checkCurrentUserRole(Role.ADMIN)) || (userService.checkCurrentUserRole(Role.STUDIO_MANAGER))))
            throw new NotAuthorizedException("You are not authorized to remove users from studio");

        checkIfExist(studio_id);
        userService.checkIfExist(user_id);

        Studio studio = getById(studio_id);
        User user = userService.getById(user_id);
        List<User> users = studio.getUsers();

        if(!users.contains(user))
            throw new ResourceNotFoundException("User is not registered in this studio");

        users.remove(user);
        studio.setUsers(users);
        save(studio);
    }

    public void addUser(Long studio_id, Long user_id){
        if(!((userService.checkCurrentUserRole(Role.ADMIN)) || (userService.checkCurrentUserRole(Role.STUDIO_MANAGER))))
            throw new NotAuthorizedException("You are not authorized to add users to the studio");

        checkIfExist(studio_id);
        userService.checkIfExist(user_id);


        Studio studio = getById(studio_id);
        User user = userService.getById(user_id);
        if(studio.getUsers().contains(user))
            throw new ResourceAlreadyExistException("User " + user.getEmail() + "is already in studio " + studio.getName());

        studio.getUsers().add(user);
        save(studio);
        deviceService.allowUserToAllDevicesInStudio(user_id,studio_id);
    }
    public void checkIfExist(Long studio_id){
        if(!studioRepository.existsById(studio_id)){
            throw new ResourceNotFoundException("Studio with ID " + studio_id +" does not exist");
        }
    }
    public List<Studio> getAll() {
        return studioRepository.findAll();
    }

    public Studio getById(Long id) {
        return studioRepository.getReferenceById(id);
    }

    public StudioInfo getInfoById(Long id){
        return studioInfoDTOMapper.apply(studioRepository.getReferenceById(id));
    }

    public boolean checkIfUserIsManager(Long user_id, Long studio_id){
        checkIfExist(studio_id);
        userService.checkIfExist(user_id);
        Studio studio = getById(studio_id);

        return Objects.equals(studio.getManager().getId(), user_id);
    }

    public List<User> getAllUserByStudioId(Long studioId){
        return studioRepository.findUsersByStudioId(studioId);
    }

    public List<UserInfo> getAllUsersInfoByStudioId(Long studioId){
        return getById(studioId).getUsers()
                .stream()
                .map(userInfoDTOMapper)
                .collect(Collectors.toList());

    }
    public List<UserInfo> getAllTeachersInfoByStudioId(Long studioId){
        return getById(studioId).getTeachers()
                .stream()
                .map(userInfoDTOMapper)
                .collect(Collectors.toList());
    }

    public void addTeacher(Long userId, Long studioId) {
        Studio studio = getById(studioId);
        User user = userService.getById(userId);

        if(
                !userService.getCurrentUser().getId().equals(studio.getManager().getId())
                        && !userService.checkCurrentUserRole(Role.ADMIN)
        )
            throw new NotAuthorizedException("You are not allowed to add teachers to this studio");

        studio.getTeachers().add(user);
    }

    public void removeTeacher(Long userId, Long studioId){
        Studio studio = getById(studioId);
        User user = userService.getById(userId);

        if(
                !userService.getCurrentUser().getId().equals(studio.getManager().getId())
                && !userService.checkCurrentUserRole(Role.ADMIN)
        )
            throw new NotAuthorizedException("You are not allowed to remove teachers from this studio");


        studio.getTeachers().remove(user);
    }
}
