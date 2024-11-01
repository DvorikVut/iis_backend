package iis.project.Studio.dto;

import iis.project.Device.Device;
import lombok.Builder;

import java.util.List;

@Builder
public record StudioInfo(
        String name,
        List<Device> devices
) {
}
