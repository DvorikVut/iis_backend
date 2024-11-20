package iis.project.DeviceType;

import iis.project.Device.Device;
import iis.project.Device.DeviceService;
import iis.project.DeviceType.dto.NewDeviceTypeDTO;
import iis.project.EmailSender.EmailSenderService;
import iis.project.Exceptions.NotAuthorizedException;
import iis.project.Exceptions.ResourceAlreadyExistException;
import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.User.Role;
import iis.project.User.User;
import iis.project.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceTypeService {
    private final DeviceTypeRepository deviceTypeRepository;
    private final UserService userService;
    private final DeviceService deviceService;
    private final EmailSenderService emailSenderService;

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

    public void delete(Long deviceTypeId){
        if(!((userService.checkCurrentUserRole(Role.ADMIN) || userService.checkCurrentUserRole(Role.STUDIO_MANAGER)))){
            throw new NotAuthorizedException("You are not authorized to delete Device Type");
        }
        if(notExist(deviceTypeId))
            throw new ResourceNotFoundException("Device type with this ID does not exist");

        DeviceType deviceType = getById(deviceTypeId);
        List<Device> devices = deviceService.getAllByDeviceTypeId(deviceTypeId);
        DeviceType othersDeviceType = deviceTypeRepository.findByName("others");
        List<User> ownersToNotify = new ArrayList<>();
        for(Device device : devices){
            device.setDeviceType(othersDeviceType);
            deviceService.save(device);
            ownersToNotify.add(device.getOwner());
        }
        for(User owner : ownersToNotify){
            emailSenderService.sendSimpleMessage(owner.getEmail(),"Device type changed to Others", "Hi, some devices, that you have as an owner were moved to OTHERS Device type, because " + deviceType.getName() + " was deleted, you need to pick another type");
        }
        deviceTypeRepository.deleteById(deviceTypeId);
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
