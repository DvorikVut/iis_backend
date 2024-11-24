package iis.project.Room.dto;

import iis.project.Room.Room;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RoomInfoDTOMapper implements Function<Room,RoomInfo> {
    @Override
    public RoomInfo apply(Room room) {
        return RoomInfo.builder()
                .name(room.getName())
                .id(room.getId())
                .studio_id(room.getStudio().getId())
                .build();
    }
}
