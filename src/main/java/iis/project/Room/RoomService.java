package iis.project.Room;

import iis.project.Exceptions.NotAuthorizedException;
import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.Room.dto.NewRoomDTO;
import iis.project.Room.dto.RoomInfo;
import iis.project.Room.dto.RoomInfoDTOMapper;
import iis.project.Studio.StudioService;
import iis.project.User.Role;
import iis.project.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserService userService;
    private final StudioService studioService;
    private final RoomInfoDTOMapper roomInfoDTOMapper;

    public Room save(Room room){
        return roomRepository.save(room);
    }

    public Room create(NewRoomDTO newRoomDTO){
        if(!(userService.checkCurrentUserRole(Role.STUDIO_MANAGER) || (userService.checkCurrentUserRole(Role.ADMIN)))) {
            throw new NotAuthorizedException("You are not authorized to create room");
        }

        if(!studioService.checkIfUserIsManager(userService.getCurrentUser().getId(), newRoomDTO.studio_id()))
            throw new NotAuthorizedException("You must be a manager for studio to create room in it");


        Room room = Room.builder()
                .name(newRoomDTO.name())
                .studio(studioService.getById(newRoomDTO.studio_id()))
                .build();
        return save(room);
    }


    public void delete(Long id){
        if(!(userService.checkCurrentUserRole(Role.STUDIO_MANAGER) || (userService.checkCurrentUserRole(Role.ADMIN)))) {
            throw new NotAuthorizedException("You are not authorized to delete room");
        }

        if(!studioService.checkIfUserIsManager(userService.getCurrentUser().getId(), id))
            throw new NotAuthorizedException("You must be a manager for studio to delete room in it");

        roomRepository.deleteById(id);
    }

    public Room change(NewRoomDTO newRoomDTO, Long id){

        if(!(userService.checkCurrentUserRole(Role.STUDIO_MANAGER) || (userService.checkCurrentUserRole(Role.ADMIN)))) {
            throw new NotAuthorizedException("You are not authorized to change room");
        }

        if(!studioService.checkIfUserIsManager(userService.getCurrentUser().getId(), newRoomDTO.studio_id()))
            throw new NotAuthorizedException("You must be a manager for studio to change room in it");

        Room room = getById(id);
        room.setName(newRoomDTO.name());

        return room;
    }
    public RoomInfo RoomToRoomInfo(Room room){
        return RoomInfo.builder()
                .name(room.getName())
                .build();
    }

    public Room getById(Long id){
        return roomRepository.getReferenceById(id);
    }


    public RoomInfo getInfoById(Long id){
        return RoomToRoomInfo(getById(id));
    }

    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    public void checkIfExist(Long room_id) {
        if(!roomRepository.existsById(room_id))
            throw new ResourceNotFoundException("Room with ID " + room_id + " does not exist");
    }
    public List<RoomInfo> getAllByStudioId(Long studioId) {
        return roomRepository.findAllByStudioId(studioId)
                .stream()
                .map(roomInfoDTOMapper)
                .collect(Collectors.toList());
    }
}
