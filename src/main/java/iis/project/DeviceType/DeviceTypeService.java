package iis.project.DeviceType;

import iis.project.DeviceType.dto.NewDeviceTypeDTO;
import iis.project.Exceptions.NotAuthorizedException;
import iis.project.Exceptions.ResourceAlreadyExistException;
import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.User.Role;
import iis.project.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceTypeService {
    private final DeviceTypeRepository deviceTypeRepository;
    private final UserService userService;

    public DeviceType create(NewDeviceTypeDTO newDeviceTypeDTO){
       if(!((userService.checkCurrentUserRole(Role.ADMIN) || userService.checkCurrentUserRole(Role.STUDIO_MANAGER)))){
           throw new NotAuthorizedException("You are not authorized to create Device Type");
        }

        if(deviceTypeRepository.existsByName(newDeviceTypeDTO.name())){
            throw new ResourceAlreadyExistException("DeviceType with this name already exist");
        }

        DeviceType deviceType = DeviceType.builder().name(newDeviceTypeDTO.name()).build();
        return save(deviceType);
    }

    public void delete(Long id){
        if(!((userService.checkCurrentUserRole(Role.ADMIN) || userService.checkCurrentUserRole(Role.STUDIO_MANAGER)))){
            throw new NotAuthorizedException("You are not authorized to delete Device Type");
        }
        if(notExist(id))
            throw new ResourceNotFoundException("Device type with this ID does not exist");


        //TODO: DEVICE_TYPE -> OTHERS  TO ALL DEVICES WITH THIS

        deviceTypeRepository.deleteById(id);
    }

    public DeviceType change(NewDeviceTypeDTO newDeviceTypeDTO, Long id){
        if(!((userService.checkCurrentUserRole(Role.ADMIN) || userService.checkCurrentUserRole(Role.STUDIO_MANAGER)))){
            throw new NotAuthorizedException("You are not authorized to change Device Type");
        }

        if(notExist(id))
            throw new ResourceNotFoundException("Device type with this ID does not exist");

        DeviceType deviceType = getById(id);
        deviceType.setName(newDeviceTypeDTO.name());
        return save(deviceType);
    }


    public DeviceType save(DeviceType deviceType){
        return deviceTypeRepository.save(deviceType);
    }

    public DeviceType getById(Long id){
        return deviceTypeRepository.getReferenceById(id);
    }

    public boolean notExist(Long id){
        return !deviceTypeRepository.existsById(id);
    }
    public List<DeviceType> getAll() {
        return deviceTypeRepository.findAll();
    }
}
