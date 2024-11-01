package iis.project.DeviceHours;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceHoursRepository extends JpaRepository<DeviceHours,Long> {
}
