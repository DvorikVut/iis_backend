package iis.project.DeviceHours.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record NewDeviceHoursDTO(
        Long device_id,
        Long room_id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
}
