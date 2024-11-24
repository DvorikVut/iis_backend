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
    @GetMapping("/user")
    public ResponseEntity<?> getAllByUser(){
        return ResponseEntity.ok(studioService.getAllByUser());
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewStudioDTO newStudioDTO){
        return ResponseEntity.ok(studioService.create(newStudioDTO));
    }
    @PostMapping("/add-manager/{userId}/{studioId}")
    public ResponseEntity<?> setManager(@PathVariable Long userId, @PathVariable Long studioId){
        studioService.setManager(userId, studioId);
        return ResponseEntity.ok("Manager was successfully added");
    }
    @PostMapping("/remove-manager")
    public ResponseEntity<?> removeManager(@RequestBody Long studio_id) {
        studioService.removeManager(studio_id);
        return ResponseEntity.ok("Manager was successfully removed");
    }
    @PostMapping("/add-teacher/{userId}/{studioId}")
    public ResponseEntity<?> addTeacher(@PathVariable Long userId, @PathVariable Long studioId){
        studioService.addTeacher(userId, studioId);
        return ResponseEntity.ok("Teacher was successfully added");
    }
    @PostMapping("/remove-teacher/{userId}/{studioId}")
    public ResponseEntity<?> removeTeacher(@PathVariable Long userId, @PathVariable Long studioId){
        studioService.removeTeacher(userId, studioId);
        return ResponseEntity.ok("Teacher was successfully removed");
    }
    @PostMapping("/add-user/{userId}/{studioId}")
    public ResponseEntity<?> addUser(@PathVariable Long userId, @PathVariable Long studioId){
        studioService.addUser(userId, studioId);
        return ResponseEntity.ok("User was successfully added");
    }
    @PostMapping("/remove-user/{userId}/{studioId}")
    public ResponseEntity<?> removeUser(@PathVariable Long userId, @PathVariable Long studioId){
        studioService.removeUser(userId, studioId);
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
