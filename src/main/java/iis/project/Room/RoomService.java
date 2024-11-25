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

    /**
     * Save Room
     * @param room Room to save
     * @return saved Room
     */
    public Room save(Room room){
        return roomRepository.save(room);
    }

    /**
     * Create Room
     * @param newRoomDTO DTO with Room data
     * @return created Room
     */
    public Room create(NewRoomDTO newRoomDTO){
        if(!(userService.checkCurrentUserRole(Role.STUDIO_MANAGER) || (userService.checkCurrentUserRole(Role.ADMIN)))) {
            throw new NotAuthorizedException("You are not authorized to create room");
        }

        if(!studioService.checkIfUserIsManager(userService.getCurrentUser().getId(), newRoomDTO.studio_id()) && !userService.checkCurrentUserRole(Role.ADMIN))
            throw new NotAuthorizedException("You must be a manager for studio to create room in it");


        Room room = Room.builder()
                .name(newRoomDTO.name())
                .studio(studioService.getById(newRoomDTO.studio_id()))
                .build();
        return save(room);
    }

    /**
     * Delete room
     * @param id ID of room to delete
     */
    public void delete(Long id){
        if(!(userService.checkCurrentUserRole(Role.STUDIO_MANAGER) || (userService.checkCurrentUserRole(Role.ADMIN)))) {
            throw new NotAuthorizedException("You are not authorized to delete room");
        }

        if(!studioService.checkIfUserIsManager(userService.getCurrentUser().getId(), getById(id).getStudio().getId()))
            throw new NotAuthorizedException("You must be a manager for studio to delete room in it");

        roomRepository.deleteById(id);
    }

    /**
     * Change room
     * @param newRoomDTO DTO with new Room data
     * @param id ID of room to change
     * @return changed Room
     */
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
    /**
     * Get Room by ID
     * @param id ID of Room
     * @return Room
     */
    public Room getById(Long id){
        return roomRepository.getReferenceById(id);
    }

    /*
        * Get all Rooms
        * @return List of Rooms
     */
    public List<Room> getAll() {
        return roomRepository.findAll();
    }


    /**
     * Check if Room with ID exist
     * @param room_id ID of Room
     */
    public void checkIfExist(Long room_id) {
        if(!roomRepository.existsById(room_id))
            throw new ResourceNotFoundException("Room with ID " + room_id + " does not exist");
    }

    /**
     * Get all Rooms by Studio ID
     * @param studioId Studio ID
     * @return List of Rooms
     */
    public List<RoomInfo> getAllByStudioId(Long studioId) {
        return roomRepository.findAllByStudioId(studioId)
                .stream()
                .map(roomInfoDTOMapper)
                .collect(Collectors.toList());
    }
}
