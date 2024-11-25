package iis.project.DeviceHours;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceHoursRepository extends JpaRepository<DeviceHours,Long> {
    List<DeviceHours> findAllByDeviceId(Long deviceId);
    void deleteAllByDeviceId(Long deviceId);
}
