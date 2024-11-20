package iis.project.Reservation.dto;

import java.time.LocalDateTime;

public record NewReservationDTO(
        Long device_id,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
}
