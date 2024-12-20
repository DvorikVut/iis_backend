package iis.project.Device.dto;

import java.time.LocalDate;

public record NewDeviceDTO(
        Long deviceType_id,
        String name,
        String description,
        Integer yearOfManufacture,
        LocalDate purchaseDate,
        Boolean disabledForBorrowing,
        Integer maximumLoanPeriodInHours,
        Boolean forAll,
        Long studio_id
) {
}
