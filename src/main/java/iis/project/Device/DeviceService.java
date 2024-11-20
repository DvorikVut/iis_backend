package iis.project.Device;

import iis.project.Device.dto.NewDeviceDTO;
import iis.project.DeviceType.DeviceTypeService;
import iis.project.Exceptions.NotAuthorizedException;
import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.Studio.Studio;
import iis.project.Studio.StudioService;
import iis.project.User.Role;
import iis.project.User.User;
import iis.project.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserService userService;
    private final DeviceTypeService deviceTypeService;
    private final StudioService studioService;

    public Device create(NewDeviceDTO newDeviceDTO) {
        if (!((userService.checkCurrentUserRole(Role.ADMIN)) || (userService.checkCurrentUserRole(Role.TEACHER)))) {
            throw new NotAuthorizedException("You are not authorized to create device");
        }

        Device device = Device.builder()
                .name(newDeviceDTO.name())
                .deviceType(deviceTypeService.getById(newDeviceDTO.deviceType_id()))
                .description(newDeviceDTO.description())
                .yearOfManufacture(newDeviceDTO.yearOfManufacture())
                .purchaseDate(newDeviceDTO.purchaseDate())
                .maximumLoanPeriodInHours(newDeviceDTO.maximumLoanPeriodInHours())
                .studio(studioService.getById(newDeviceDTO.studio_id()))
                .DisabledForBorrowing(false)
                .forAll(newDeviceDTO.forAll())
                .owner(userService.getCurrentUser())
                .build();
        save(device);

        if (device.getForAll())
            allowDeviceToAllUsersInStudio(device.getId());

        return device;
    }

    public void delete(Long device_id) {

        //TODO: DELETE DEVICE WITH LOGIC TO HANDLE RESERVATIONS


        //1 - check if it is borrowed ( Find reservations, where start - currentTime - end)


        //2 - delete all future reservation


        //3 - send emails to users
    }

    public void change(Long device_id, NewDeviceDTO newDeviceDTO) {
        if (!((userService.checkCurrentUserRole(Role.ADMIN)) || (userService.checkCurrentUserRole(Role.TEACHER)))) {
            throw new NotAuthorizedException("You are not authorized to change device");
        }

        checkIfExist(device_id);

        Long owner_id = getById(device_id).getOwner().getId();

        if (!(Objects.equals(userService.getCurrentUser().getId(), owner_id))) {
            throw new NotAuthorizedException("You must be the owner to change the device");
        }

        Device device = getById(device_id);

        device.setDeviceType(deviceTypeService.getById(newDeviceDTO.deviceType_id()));
        device.setName(newDeviceDTO.name());
        device.setDescription(newDeviceDTO.description());
        device.setPurchaseDate(newDeviceDTO.purchaseDate());
        device.setYearOfManufacture(newDeviceDTO.yearOfManufacture());

        save(device);
    }

    public Device getById(Long deviceId) {
        return deviceRepository.getReferenceById(deviceId);
    }
    public void checkIfExist(Long device_id) {
        if (!deviceRepository.existsById(device_id)) {
            throw new ResourceNotFoundException("Device with ID " + device_id + " does not exist");
        }
    }
    public void save(Device device) {
        deviceRepository.save(device);
    }
    public void allowUserToAllDevicesInStudio(Long userId, Long studio_id) {
        Studio studio = studioService.getById(studio_id);
        User user = userService.getById(userId);
        List<Device> devicesInStudio = deviceRepository.findAllByStudio(studio);
        for (Device device : devicesInStudio) {
            if (device.getForAll()) {
                device.getUsers().add(user);
                save(device);
            }
        }
    }
    public void allowDeviceToAllUsersInStudio(Long device_id) {
        Device device = getById(device_id);
        List<User> usersInStudio = studioService.getAllUserByStudioId(device.getStudio().getId());
        for (User user : usersInStudio) {
            device.getUsers().add(user);
        }
    }
    public List<Device> getAllByStudioId(Long studio_id) {
        return deviceRepository.findAllByStudio(studioService.getById(studio_id));
    }
    //Get all the devices that current user can borrow
    public List<Device> getAllByUserCanBorrow(){
        return deviceRepository.findAllByUsersContaining(userService.getById(userService.getCurrentUser().getId()));
    }
    public boolean checkIfCanBorrow(Long userId, Long deviceId){
        return deviceRepository.existsByIdAndUsersContaining(deviceId, userService.getById(userId));
    }
    public List<Device> getAll() {
        return deviceRepository.findAll();
    }
    public List<Device> getAllByOwnerId(Long ownerId){
        return deviceRepository.findAllByOwnerId(ownerId);
    }
    public List<Device> getAllByDeviceTypeId(Long deviceTypeId) {
        return deviceRepository.findAllByDeviceTypeId(deviceTypeId);
    }
}
