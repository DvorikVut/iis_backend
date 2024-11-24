package iis.project.Device.dto;

import iis.project.Device.Device;
import iis.project.DeviceHours.DeviceHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;


@Service
@RequiredArgsConstructor
public class DeviceInfoDTOMapper implements Function<Device,DeviceInfoDTO>{
    private final DeviceHoursService deviceHoursService;

    @Override
    public DeviceInfoDTO apply(Device device) {
        return DeviceInfoDTO.builder()
                .id(device.getId())
                .name(device.getName())
                .description(device.getDescription())
                .purchaseDate(device.getPurchaseDate())
                .yearOfManufacture(device.getYearOfManufacture())
                .maximumLoanPeriodInHours(device.getMaximumLoanPeriodInHours())
                .forAll(device.getForAll())
                .disabledForBorrowing(device.getDisabledForBorrowing())
                .studioId(device.getStudio().getId())
                .deviceType(device.getDeviceType())
                .ownerId(device.getOwner().getId())
                .studioName(device.getStudio().getName())
                .hours(deviceHoursService.getAllByDeviceId(device.getId()))
                .build();
    }
}
