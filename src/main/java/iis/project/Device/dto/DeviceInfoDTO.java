package iis.project.Device.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder

public record DeviceInfoDTO(

        Long id,
        String name,
        String description,
        Integer yearOfManufacture,
        LocalDate purchaseDate,
        Integer maximumLoanPeriodInHours,
        Boolean forAll,
        Boolean DisabledForBorrowing
) {
}
