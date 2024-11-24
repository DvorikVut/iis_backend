package iis.project.Room.dto;

import lombok.Builder;

@Builder
public record RoomInfo(
        Long id,
        Long studio_id,
        String name
) {
}
