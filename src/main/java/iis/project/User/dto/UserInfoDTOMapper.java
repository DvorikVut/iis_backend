package iis.project.User.dto;

import iis.project.Studio.StudioService;
import iis.project.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserInfoDTOMapper implements Function<User, UserInfo> {
    private final StudioService studioService;

    @Override
    public UserInfo apply(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .surname(user.getSurname())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}
