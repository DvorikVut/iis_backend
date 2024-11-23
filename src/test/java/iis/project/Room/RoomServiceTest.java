package iis.project.Room;

import iis.project.Exceptions.NotAuthorizedException;
import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.Room.dto.NewRoomDTO;
import iis.project.Studio.Studio;
import iis.project.Studio.StudioService;
import iis.project.User.Role;
import iis.project.User.User;

import iis.project.User.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserService userService;

    @Mock
    private StudioService studioService;

    @InjectMocks
    private RoomService roomService;

    private User currentUser;
    private Studio studio;
    private Room room;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        currentUser = new User();
        currentUser.setId(1L);

        studio = new Studio();
        studio.setId(1L);

        room = Room.builder()
                .id(1L)
                .name("Test Room")
                .studio(studio)
                .build();

        // Настройка SecurityContextHolder
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(currentUser, null, List.of());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Настройка мока userService
        when(userService.getCurrentUser()).thenReturn(currentUser);
    }


    @Test
    void shouldCreateRoomWhenUserIsAuthorized() {
        NewRoomDTO newRoomDTO = new NewRoomDTO(1L, "New Room");

        when(userService.checkCurrentUserRole(Role.STUDIO_MANAGER)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(studioService.checkIfUserIsManager(currentUser.getId(), 1L)).thenReturn(true);
        when(studioService.getById(1L)).thenReturn(studio);

        // Настройка ответа roomRepository.save(), чтобы возвращать объект с данными из вызова
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room savedRoom = invocation.getArgument(0);
            savedRoom.setId(1L); // Устанавливаем ID для сохраненного объекта
            return savedRoom;
        });

        Room createdRoom = roomService.create(newRoomDTO);

        // Проверка
        assertNotNull(createdRoom);
        assertEquals("New Room", createdRoom.getName()); // Проверяем имя
        verify(roomRepository, times(1)).save(any(Room.class));
    }


    @Test
    void shouldThrowNotAuthorizedExceptionWhenUserNotAuthorizedToCreateRoom() {
        NewRoomDTO newRoomDTO = new NewRoomDTO(1L, "New Room");

        when(userService.checkCurrentUserRole(Role.STUDIO_MANAGER)).thenReturn(false);
        when(userService.checkCurrentUserRole(Role.ADMIN)).thenReturn(false);

        assertThrows(NotAuthorizedException.class, () -> roomService.create(newRoomDTO));
    }

    @Test
    void shouldDeleteRoomWhenUserIsAuthorized() {
        when(userService.checkCurrentUserRole(Role.STUDIO_MANAGER)).thenReturn(true);
        when(studioService.checkIfUserIsManager(currentUser.getId(), 1L)).thenReturn(true);

        roomService.delete(1L);

        verify(roomRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowNotAuthorizedExceptionWhenUserNotAuthorizedToDeleteRoom() {
        when(userService.checkCurrentUserRole(Role.STUDIO_MANAGER)).thenReturn(false);

        assertThrows(NotAuthorizedException.class, () -> roomService.delete(1L));
        verify(roomRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldCheckIfRoomExists() {
        when(roomRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> roomService.checkIfExist(1L));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenRoomDoesNotExist() {
        when(roomRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> roomService.checkIfExist(1L));
    }
}
