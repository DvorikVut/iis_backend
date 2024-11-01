package iis.project.Reservation;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReservationStatus {
    @JsonProperty("RESERVED")
    RESERVED,
    @JsonProperty("BORROWED")
    BORROWED,
    @JsonProperty("RETURNED")
    RETURNED
}
