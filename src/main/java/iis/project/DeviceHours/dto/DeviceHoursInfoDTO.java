package iis.project.DeviceHours.dto;

import lombok.Builder;

import iis.project.DeviceHours.DayOfWeek;
import java.time.LocalTime;

@Builder
public record DeviceHoursInfoDTO(
                Long deviceHoursId,
                DayOfWeek dayOfWeek,
                LocalTime startTime,
                LocalTime endTime,
                String roomName,
                Long deviceId,
                String deviceName
) {
}
