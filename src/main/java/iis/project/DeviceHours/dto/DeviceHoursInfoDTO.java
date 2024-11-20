package iis.project.DeviceHours.dto;

import lombok.Builder;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Builder
public record DeviceHoursInfoDTO(
                DayOfWeek dayOfWeek,
                LocalTime startTime,
                LocalTime endTime,
                String roomName,
                Long deviceId,
                String deviceName
) {
}
