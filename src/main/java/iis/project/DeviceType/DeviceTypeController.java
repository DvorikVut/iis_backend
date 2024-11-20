package iis.project.DeviceType;

import iis.project.DeviceType.DeviceTypeService;
import iis.project.DeviceType.dto.NewDeviceTypeDTO;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/device-type")
public class DeviceTypeController {

    //controller

    private final DeviceTypeService deviceTypeService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(deviceTypeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return ResponseEntity.ok(deviceTypeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewDeviceTypeDTO newDeviceTypeDTO){
        return ResponseEntity.ok(deviceTypeService.create(newDeviceTypeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        deviceTypeService.delete(id);
        return ResponseEntity.ok("Device type was deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> change(@RequestBody NewDeviceTypeDTO newDeviceTypeDTO, @PathVariable Long id){
        return ResponseEntity.ok(deviceTypeService.change(newDeviceTypeDTO, id));
    }
}
