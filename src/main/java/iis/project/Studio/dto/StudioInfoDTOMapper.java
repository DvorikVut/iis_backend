package iis.project.Studio.dto;

import iis.project.Device.DeviceService;
import iis.project.Studio.Studio;
import iis.project.Studio.StudioService;
import iis.project.User.dto.UserInfoDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class StudioInfoDTOMapper implements Function<Studio, StudioInfo> {
    private final DeviceService deviceService;
    private final StudioService studioService;
    private final UserInfoDTOMapper userInfoDTOMapper;

    @Override
    public StudioInfo apply(Studio studio) {
        return StudioInfo.builder()
                .id(studio.getId())
                .name(studio.getName())
                .devicesInfo(deviceService.getAllByStudioId(studio.getId()))
                .users(studioService.getAllUsersInfoByStudioId(studio.getId()))
                .teachers(studioService.getAllTeachersInfoByStudioId(studio.getId()))
                .managerInfo(userInfoDTOMapper.apply(studio.getManager()))
                .build();
    }
}
