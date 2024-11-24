package iis.project.User;

import iis.project.Auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.query.Jpa21Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserInfoById(@PathVariable Long userId){
        return ResponseEntity.ok(userService.getInfoById(userId));
    }
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(){
        return ResponseEntity.ok(userService.getCurrentUserInfo());
    }

    @GetMapping("/registered")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(userService.getAllByRole(Role.USER));
    }

    @GetMapping("/teachers")
    public ResponseEntity<?> getAllTeachers(){
        return ResponseEntity.ok(userService.getAllByRole(Role.TEACHER));
    }

    @GetMapping("/managers")
    public ResponseEntity<?> getAllManagers(){ return ResponseEntity.ok(userService.getAllByRole(Role.STUDIO_MANAGER));}

    @PutMapping
    public ResponseEntity<?> update(@RequestBody UpdateProfileDTO updateProfileDTO){
        userService.update(updateProfileDTO);
        return ResponseEntity.ok("Profile was updated successfully");
    }
}
