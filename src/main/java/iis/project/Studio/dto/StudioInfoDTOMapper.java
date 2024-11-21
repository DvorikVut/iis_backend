package iis.project.Studio.dto;

import iis.project.Device.DeviceService;
import iis.project.Studio.Studio;
import iis.project.Studio.StudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class StudioInfoDTOMapper implements Function<Studio, StudioInfo> {
    private final DeviceService deviceService;
    private final StudioService studioService;
    @Override
    public StudioInfo apply(Studio studio) {
        return StudioInfo.builder()
                .name(studio.getName())
                .devicesInfo(deviceService.getAllByStudioId(studio.getId()))
                .users(studioService.getAllUsersInfoByStudioId(studio.getId()))
                .teachers(studioService.getAllTeachersInfoByStudioId(studio.getId()))
                .managerId(studio.getManager().getId())
                .build();
    }
}
