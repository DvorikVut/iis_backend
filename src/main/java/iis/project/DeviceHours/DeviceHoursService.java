package iis.project.DeviceHours;

import iis.project.Device.Device;
import iis.project.Device.DeviceService;
import iis.project.DeviceHours.dto.DeviceHoursInfoDTO;
import iis.project.DeviceHours.dto.DeviceHoursInfoDTOMapper;
import iis.project.DeviceHours.dto.NewDeviceHoursDTO;
import iis.project.Exceptions.NotAuthorizedException;
import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.Reservation.Reservation;
import iis.project.Room.RoomService;
import iis.project.User.Role;
import iis.project.User.User;
import iis.project.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceHoursService {
    private final DeviceHoursRepository deviceHoursRepository;
    private final DeviceService deviceService;
    private final RoomService roomService;
    private final UserService userService;
    private final DeviceHoursInfoDTOMapper deviceHoursInfoDTOMapper;

    /**
     * Save DeviceHours
     * @param deviceHours DeviceHours to save
     * @return saved DeviceHours
     */
    public DeviceHours save(DeviceHours deviceHours){
        return deviceHoursRepository.save(deviceHours);
    }

    /**
     * Create DeviceHours
     * @param newDeviceHoursDTO DTO with DeviceHours data
     * @return created DeviceHours
     */
    public DeviceHours create(NewDeviceHoursDTO newDeviceHoursDTO){
        Device device = deviceService.getById(newDeviceHoursDTO.device_id());
        if( !userService.checkCurrentUserRole(Role.ADMIN) && !device.getOwner().equals(userService.getCurrentUser()) ){
            throw new NotAuthorizedException("You are not authorized to create DeviceHour for this device");
        }

        deviceService.checkIfExist(newDeviceHoursDTO.device_id());
        roomService.checkIfExist(newDeviceHoursDTO.room_id());

        DeviceHours deviceHours = DeviceHours.builder()
                .device(device)
                .room(roomService.getById(newDeviceHoursDTO.room_id()))
                .dayOfWeek(newDeviceHoursDTO.dayOfWeek())
                .startTime(newDeviceHoursDTO.startTime())
                .endTime(newDeviceHoursDTO.endTime())
                .build();
        return save(deviceHours);
    }

    /**
     * Get all DeviceHours
     * @param deviceId Device ID
     * @return List of DeviceHours
     */
    public List<DeviceHoursInfoDTO> getAllByDeviceId(Long deviceId){
        return deviceHoursRepository.findAllByDeviceId(deviceId)
                .stream()
                .map(deviceHoursInfoDTOMapper)
                .collect(Collectors.toList());
    }

    /**
     * Get Info about DeviceHours by ID
     * @param deviceHoursId DeviceHours ID
     * @return DeviceHoursInfoDTO
     */
    public DeviceHoursInfoDTO getInfoById(Long deviceHoursId){
        return deviceHoursRepository.findById(deviceHoursId)
                .map(deviceHoursInfoDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("DeviceHours with id " + deviceHoursId + " does not exists"));
    }

    /**
     * Delete DeviceHours by ID
     * @param deviceHoursId DeviceHours ID
     */
    public void deleteById(Long deviceHoursId) {
        DeviceHours deviceHours = getById(deviceHoursId);
        Device device = deviceService.getById(deviceHours.getDevice().getId());
        User currentUser = userService.getCurrentUser();
        if(!currentUser.equals(device.getOwner()) && !userService.checkCurrentUserRole(Role.ADMIN))
            throw new NotAuthorizedException("You are not allowed to delete this DeviceHours");
        deviceHoursRepository.deleteById(deviceHoursId);
    }



    /** Get DeviceHours by ID
        * @param deviceHoursId DeviceHours ID
        * @return DeviceHours
     */
    private DeviceHours getById(Long deviceHoursId) {
        return deviceHoursRepository.findById(deviceHoursId).orElseThrow(() -> new ResourceNotFoundException("DeviceHours with ID " + deviceHoursId + " does not exists"));
    }
}
