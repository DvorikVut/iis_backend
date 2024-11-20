package iis.project.DeviceHours.dto;

import iis.project.DeviceHours.DeviceHours;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class DeviceHoursInfoDTOMapper implements Function<DeviceHours, DeviceHoursInfoDTO> {
    @Override
    public DeviceHoursInfoDTO apply(DeviceHours deviceHours) {
        return DeviceHoursInfoDTO.builder()
                .startTime(deviceHours.getStartTime())
                .endTime(deviceHours.getEndTime())
                .deviceName(deviceHours.getDevice().getName())
                .roomName(deviceHours.getRoom().getName())
                .deviceId(deviceHours.getDevice().getId())
                .build();
    }
}
