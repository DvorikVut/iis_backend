package iis.project.Reservation;

import iis.project.Device.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqualAndDevice(LocalDateTime startDateTime, LocalDateTime endDateTime, Device device);
}
