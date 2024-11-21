package iis.project.Device.dto;

import iis.project.Device.Device;

import java.util.function.Function;

public class DeviceInfoDTOMapper implements Function<Device,DeviceInfoDTO>{

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
                .DisabledForBorrowing(device.getDisabledForBorrowing())
                .build();
    }
}
