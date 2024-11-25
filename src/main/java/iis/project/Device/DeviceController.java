package iis.project.Device;

import iis.project.Device.dto.NewDeviceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(deviceService.getAll());
    }
    @GetMapping("/{deviceId}")
    public ResponseEntity<?> getInfoById(@PathVariable Long deviceId){
        return ResponseEntity.ok(deviceService.getInfoById(deviceId));
    }
    @GetMapping("/users-can-borrow/{deviceId}")
    public ResponseEntity<?> getUsersThatCanBorrowDevice(@PathVariable Long deviceId){
        return ResponseEntity.ok(deviceService.getAllUsersThatCanBorrowDevice(deviceId));
    }
    @GetMapping("/user")
    public ResponseEntity<?> getAllUserCanBorrow(){
        return ResponseEntity.ok(deviceService.getAllByUserCanBorrow());
    }
    @GetMapping("/owner")

    public ResponseEntity<?> getAllByOwnerId(){
        return ResponseEntity.ok(deviceService.getAllByOwnerId());
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewDeviceDTO newDeviceDTO){
        return ResponseEntity.ok(deviceService.create(newDeviceDTO));
    }
    @PostMapping("/{deviceId}")
    public ResponseEntity<?> addUsersToDevice(@PathVariable Long deviceId, @RequestBody List<Long> userIds){
        deviceService.addUsersToDevice(deviceId, userIds);
        return ResponseEntity.ok("Users successfully added");
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
