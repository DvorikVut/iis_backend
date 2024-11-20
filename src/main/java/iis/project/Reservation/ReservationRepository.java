package iis.project.Reservation;

import iis.project.Device.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByDeviceId(Long deviceId);
    boolean existsByStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqualAndStatusAndDeviceId(LocalDateTime startDateTime, LocalDateTime endDateTime, ReservationStatus status, Long DeviceId);
    List<Reservation> findAllByStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqualAndDevice(LocalDateTime startDateTime, LocalDateTime endDateTime, Device device);
    List<Reservation> findAllByUserId(Long userId);
    List<Reservation> findAllByStartDateTimeLessThanEqualAndDeviceId(LocalDateTime startDateTime, Long deviceId);
}
