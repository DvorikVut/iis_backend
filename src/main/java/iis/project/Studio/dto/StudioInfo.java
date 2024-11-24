package iis.project.Studio.dto;

import iis.project.Device.Device;
import iis.project.Device.dto.DeviceInfoDTO;
import iis.project.User.dto.UserInfo;
import lombok.Builder;

import java.util.List;

@Builder
public record StudioInfo(
        Long id,
        String name,
        List<DeviceInfoDTO> devicesInfo,
        List<UserInfo> users,
        List<UserInfo> teachers,
        UserInfo managerInfo
) {
}
