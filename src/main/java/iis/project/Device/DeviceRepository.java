package iis.project.Device;

import iis.project.Studio.Studio;
import iis.project.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device,Long> {
    List<Device> findAllByStudio(Studio studio);
    List<Device> findAllByUsersContaining(User user);
    Boolean existsByIdAndUsersContaining(Long deviceId, User user);
    List<Device> findAllByOwnerId(Long ownerId);
    List<Device> findAllByDeviceTypeId(Long deviceTypeId);
}
