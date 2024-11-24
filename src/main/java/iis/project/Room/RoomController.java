package iis.project.Room;

import iis.project.Room.dto.NewRoomDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(roomService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return ResponseEntity.ok(roomService.getById(id));
    }

    @GetMapping("/by-studio/{studioId}")
    public ResponseEntity<?> getAllByStudioId(@PathVariable Long studioId){
        return ResponseEntity.ok(roomService.getAllByStudioId(studioId));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewRoomDTO newRoomDTO){
        return ResponseEntity.ok(roomService.create(newRoomDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> change(@RequestBody NewRoomDTO newRoomDTO, @PathVariable Long id){
        return ResponseEntity.ok(roomService.change(newRoomDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        roomService.delete(id);
        return ResponseEntity.ok("Room was deleted successfully");
    }
}
