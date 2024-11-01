package iis.project.DeviceHours;

import iis.project.Device.Device;
import iis.project.Device.DeviceService;
import iis.project.Exceptions.NotAuthorizedException;
import iis.project.Room.RoomService;
import iis.project.User.Role;
import iis.project.User.User;
import iis.project.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;

@Service
@RequiredArgsConstructor
public class DeviceHoursService {
    private final DeviceHoursRepository deviceHoursRepository;
    private final DeviceService deviceService;
    private final RoomService roomService;
    private final UserService userService;

    public DeviceHours save(DeviceHours deviceHours){
        return deviceHoursRepository.save(deviceHours);
    }

    public DeviceHours create(NewDeviceHoursDTO newDeviceHoursDTO){
        Device device = deviceService.getById(newDeviceHoursDTO.device_id());
        if(!(userService.checkCurrentUserRole(Role.ADMIN)) || (device.getOwner().equals(userService.getCurrentUser()))){
            throw new NotAuthorizedException("You are not authirized to create DeviceHour for this device");
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
}
