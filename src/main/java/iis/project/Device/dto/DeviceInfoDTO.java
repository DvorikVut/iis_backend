package iis.project.Device.dto;

import iis.project.DeviceHours.dto.DeviceHoursInfoDTO;
import iis.project.DeviceType.DeviceType;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder

public record DeviceInfoDTO(
        Long id,
        String name,
        String description,
        Integer yearOfManufacture,
        LocalDate purchaseDate,
        DeviceType deviceType,
        Integer maximumLoanPeriodInHours,
        Boolean forAll,
        Boolean disabledForBorrowing,
        Long studioId,
        Long ownerId,
        String studioName,
        List<DeviceHoursInfoDTO> hours
) {
}
