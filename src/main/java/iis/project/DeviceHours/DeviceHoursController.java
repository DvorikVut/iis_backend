package iis.project.DeviceHours;

import iis.project.DeviceHours.dto.DeviceHoursInfoDTO;
import iis.project.DeviceHours.dto.NewDeviceHoursDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deviceHours")
public class DeviceHoursController {

    private final DeviceHoursService deviceHoursService;

    @GetMapping("/{deviceId}")
    public ResponseEntity<List<DeviceHoursInfoDTO>> getAllByDeviceId(@PathVariable Long deviceId) {
        return ResponseEntity.ok(deviceHoursService.getAllByDeviceId(deviceId));
    }

    @GetMapping("/{deviceHoursId}")
    public ResponseEntity<?> getInfoById(@PathVariable Long deviceHoursId){
        return ResponseEntity.ok(deviceHoursService.getInfoById(deviceHoursId));
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewDeviceHoursDTO newDeviceHoursDTO){
        deviceHoursService.create(newDeviceHoursDTO);
        return ResponseEntity.ok("DeviceHour was created successfully");
    }

    @DeleteMapping("/{deviceHoursId}")
    public ResponseEntity<?> delete(@PathVariable Long deviceHoursId){
        deviceHoursService.deleteById(deviceHoursId);
        return ResponseEntity.ok("DeviceHours deleted successfully");
    }
}
