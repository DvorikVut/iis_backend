package iis.project.DeviceHours;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DayOfWeek {
    @JsonProperty("1")
    MONDAY,
    @JsonProperty("2")
    TUESDAY,
    @JsonProperty("3")
    WEDNESDAY,
    @JsonProperty("4")
    THURSDAY,
    @JsonProperty("5")
    FRIDAY,
    @JsonProperty("6")
    SATURDAY,
    @JsonProperty("7")
    SUNDAY
}
