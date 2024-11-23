package iis.project.Device;

import com.mysql.cj.log.Log;
import iis.project.Device.dto.NewDeviceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(deviceService.getAll());
    }
    @GetMapping("/user")
    public ResponseEntity<?> getAllUserCanBorrow(){
        return ResponseEntity.ok(deviceService.getAllByUserCanBorrow());
    }
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getAllByOwnerId(@PathVariable Long ownerId){
        return ResponseEntity.ok(deviceService.getAllByOwnerId(ownerId));
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewDeviceDTO newDeviceDTO){
        return ResponseEntity.ok(deviceService.create(newDeviceDTO));
    }
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<?> delete(@PathVariable Long deviceId){
        deviceService.delete(deviceId);
        return ResponseEntity.ok("Device deleted successfully");
    }
    @PutMapping("/{deviceId}")
    public ResponseEntity<?> update(@PathVariable Long deviceId, @RequestBody  NewDeviceDTO newDeviceDTO){
        deviceService.change(deviceId, newDeviceDTO);
        return ResponseEntity.ok("Device information was updated successfully");
    }
}