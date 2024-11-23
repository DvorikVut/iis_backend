package iis.project.Reservation.dto;

import iis.project.Reservation.Reservation;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ReservationInfoDTOMapper implements Function<Reservation, ReservationInfoDTO> {
    @Override
    public ReservationInfoDTO apply(Reservation reservation) {
        return ReservationInfoDTO.builder()
                .actualEndDateTime(reservation.getActualEndDateTime())
                .actualStartDateTime(reservation.getActualStartDateTime())
                .startDateTime(reservation.getStartDateTime())
                .endDateTime(reservation.getEndDateTime())
                .status(reservation.getStatus())
                .userId(reservation.getUser().getId())
                .deviceId(reservation.getDevice().getId())
                .deviceName(reservation.getDevice().getName())
                .build();
    }
}
