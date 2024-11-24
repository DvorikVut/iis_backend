package iis.project.User;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

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

}
