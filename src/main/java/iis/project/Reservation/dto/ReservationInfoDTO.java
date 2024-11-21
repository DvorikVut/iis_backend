package iis.project.Reservation.dto;

import iis.project.Reservation.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record ReservationInfoDTO(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        ReservationStatus status,
        LocalDateTime actualStartDateTime,
        LocalDateTime actualEndDateTime,
        Long userId,
        Long deviceId
) {
}
