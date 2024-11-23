package iis.project.Auth;

import iis.project.User.Role;
import lombok.RequiredArgsConstructor;
import iis.project.Config.JwtService;
import iis.project.User.UserRepo;
import iis.project.User.User;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) throws BadRequestException {

        if(userRepo.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("User with this email already exists");
        }

        var user = User.builder()
                .name(request.name())
                .surname(request.surname())
                .email(request.email())
                .role(Role.USER)
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepo.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .surname(user.getSurname())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = userRepo.findByEmail(request.email()).orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .surname(user.getSurname())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

    }
}
