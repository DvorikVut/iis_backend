package iis.project.Studio;

import iis.project.EmailSender.EmailSenderService;
import iis.project.Studio.dto.NewStudioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studio")
public class StudioController {
    private final StudioService studioService;
    private final EmailSenderService emailSenderService;


    @GetMapping
    public ResponseEntity<?> getAll(){
        emailSenderService.sendSimpleMessage("tima.kininbaev@gmail.com", "Penis", "Timur Penis");
        return ResponseEntity.ok(studioService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return ResponseEntity.ok(studioService.getInfoById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewStudioDTO newStudioDTO){
        return ResponseEntity.ok(studioService.create(newStudioDTO));
    }

    @PostMapping("/add-manager")
    public ResponseEntity<?> setManager(@RequestBody Long user_id, @RequestBody Long studio_id){
        studioService.setManager(user_id, studio_id);
        return ResponseEntity.ok("Manager was successfully added");
    }

    @PostMapping("/remove-manager")
    public ResponseEntity<?> removeManager(@RequestBody Long studio_id) {
        studioService.removeManager(studio_id);
        return ResponseEntity.ok("Manager was successfully removed");
    }

    @PostMapping("/add-teacher")
    public ResponseEntity<?> addTeacher(@RequestBody Long user_id, @RequestBody Long studio_id){
        studioService.addTeacher(user_id, studio_id);
        return ResponseEntity.ok("Manager was successfully added");
    }

    @PostMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestBody Long user_id, @RequestBody Long studio_id){
        studioService.addUser(user_id, studio_id);
        return ResponseEntity.ok("User was successfully added");
    }


    @PostMapping("/remove-user")
    public ResponseEntity<?> removeUser(@RequestBody Long user_id, @RequestBody Long studio_id){
        studioService.removeUser(user_id, studio_id);
        return ResponseEntity.ok("User was successfully removed");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        studioService.delete(id);
        return ResponseEntity.ok("Studio was deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> change(@RequestBody NewStudioDTO newStudioDTO, @PathVariable Long id){
        return ResponseEntity.ok(studioService.change(id, newStudioDTO));
    }
}
