package iis.project.Reservation;

import iis.project.Device.Device;
import iis.project.Device.DeviceService;
import iis.project.Device.dto.NewDeviceDTO;
import iis.project.Exceptions.BadRequestException;
import iis.project.Exceptions.NotAuthorizedException;
import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.Studio.Studio;
import iis.project.User.Role;
import iis.project.User.User;
import iis.project.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private ReservationRepository reservationRepository;
    private DeviceService deviceService;
    private UserService userService;



    public Reservation create(NewReservationDTO newReservationDTO) {

        deviceService.checkIfExist(newReservationDTO.device_id());

        checkIfAvailable(newReservationDTO.startDateTime(), newReservationDTO.endDateTime(), newReservationDTO.device_id());




        if(newReservationDTO.endDateTime().isBefore(newReservationDTO.startDateTime())){
            throw new BadRequestException("End of the reservation cannot be before the start");
        }

        Reservation reservation = Reservation.builder()
                .device(deviceService.getById(newReservationDTO.device_id()))
                .user(userService.getCurrentUser())
                .startDateTime(newReservationDTO.startDateTime())
                .endDateTime(newReservationDTO.endDateTime())
                .status(ReservationStatus.RESERVED)
                .build();
        return save(reservation);
    }


    public void delete(Long reservation_id){
        Reservation reservation = getById(reservation_id);
        Device device = reservation.getDevice();
        User currentUser = userService.getCurrentUser();
        User creatorOfReservation = reservation.getUser();

        if (!(
                currentUser.getRole().equals(Role.ADMIN)
                || device.getOwner().equals(currentUser)
                || creatorOfReservation.equals(currentUser))
        ) {
            throw new NotAuthorizedException("You are not authorized to delete this reservation");
        }

        checkIfExist(reservation_id);
        reservationRepository.deleteById(reservation_id);
    }

    private Reservation getById(Long reservationId) {
        return reservationRepository.getReferenceById(reservationId);
    }

    public Reservation save(Reservation reservation){
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAll(){
        return reservationRepository.findAll();
    }

    public void checkIfExist(Long reservation_id){
        if(!reservationRepository.existsById(reservation_id)){
            throw new ResourceNotFoundException("Reservation with ID " + reservation_id + " does not exist");
        }
    }

    public void checkIfAvailable(LocalDateTime startDateTime, LocalDateTime endDateTime, Long device_id){
        Device device = deviceService.getById(device_id);
        List<Reservation> existedReservations = reservationRepository.findAllByStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqualAndDevice(startDateTime,endDateTime, device);
        if(existedReservations.isEmpty())
            throw new BadRequestException("Reservation overlaps with others");

    }
}
