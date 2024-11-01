package iis.project.Reservation;

import java.time.LocalDateTime;

public record NewReservationDTO(
        Long device_id,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
}
