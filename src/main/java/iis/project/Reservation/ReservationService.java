package iis.project.Reservation;

import com.google.api.client.util.DateTime;
import iis.project.Device.Device;
import iis.project.Device.DeviceService;
import iis.project.Exceptions.BadRequestException;
import iis.project.Exceptions.NotAuthorizedException;
import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.Reservation.dto.NewReservationDTO;
import iis.project.Reservation.dto.ReservationInfoDTO;
import iis.project.Reservation.dto.ReservationInfoDTOMapper;
import iis.project.User.Role;
import iis.project.User.User;
import iis.project.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final DeviceService deviceService;
    private final UserService userService;
    private final ReservationInfoDTOMapper reservationInfoDTOMapper;

    /**
     * Create new reservation
     * @param newReservationDTO DTO with reservation data
     * @return created reservation
     */
    public ReservationInfoDTO create(NewReservationDTO newReservationDTO) {
        User user = userService.getCurrentUser();

        deviceService.checkIfExist(newReservationDTO.device_id());
        if(!deviceService.checkIfCanBorrow(user.getId(), newReservationDTO.device_id()))
            throw new NotAuthorizedException("You cannot borrow this device");

        checkIfAvailable(newReservationDTO.startDateTime(), newReservationDTO.endDateTime(), newReservationDTO.device_id());

        if(newReservationDTO.endDateTime().isBefore(newReservationDTO.startDateTime()))
            throw new BadRequestException("End of the reservation cannot be before the start");

        Reservation reservation = Reservation.builder()
                .device(deviceService.getById(newReservationDTO.device_id()))
                .user(userService.getCurrentUser())
                .startDateTime(newReservationDTO.startDateTime())
                .endDateTime(newReservationDTO.endDateTime())
                .status(ReservationStatus.RESERVED)
                .build();
        return reservationInfoDTOMapper.apply(save(reservation));
    }
    /**
     * Delete reservation
     * @param reservation_id ID of reservation to delete
     */
    public void delete(Long reservation_id){
        Reservation reservation = getById(reservation_id);
        Device device = reservation.getDevice();
        User currentUser = userService.getCurrentUser();
        User creatorOfReservation = reservation.getUser();

        if(reservation.getStatus().equals(ReservationStatus.BORROWED))
            throw new RuntimeException("You cannot delete reservation if device was already taken");


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
    /**
     * Get reservation by ID
     * @param reservationId ID of reservation
     * @return Reservation
     */
    private Reservation getById(Long reservationId) {
        return reservationRepository.getReferenceById(reservationId);
    }
    /**
     * Save reservation
     * @param reservation Reservation to save
     * @return saved reservation
     */
    public Reservation save(Reservation reservation){
        return reservationRepository.save(reservation);
    }
    /**
     * Get all reservations
     * @return List of reservations
     */
    public List<ReservationInfoDTO> getAll(){
        return reservationRepository.findAll()
                .stream()
                .map(reservationInfoDTOMapper)
                .collect(Collectors.toList());
    }
    /**
     * Get all reservations by device ID
     * @param deviceId Device ID
     * @return List of reservations
     */
    public List<ReservationInfoDTO> getAllByDeviceId(Long deviceId){
        return reservationRepository.findAllByDeviceId(deviceId)
                .stream()
                .map(reservationInfoDTOMapper)
                .collect(Collectors.toList());
    }
    /**
     * Get all reservations by user ID
     * @param userId User ID
     * @return List of reservations
     */
    public List<ReservationInfoDTO> getAllByUserId(Long userId){
        return reservationRepository.findAllByUserId(userId)
                .stream()
                .map(reservationInfoDTOMapper)
                .collect(Collectors.toList());
    }
    /**
     * Get all future reservations by device ID
     * @param deviceId Device ID
     * @return List of reservations
     */
    public List<Reservation> getFutureReservations(Long deviceId){
        LocalDateTime now = LocalDateTime.now();
        return reservationRepository.findAllByStartDateTimeGreaterThanEqualAndDeviceId(now, deviceId);
    }

    /**
     * Check if reservation exists
     * @param reservation_id ID of reservation
     */
    public void checkIfExist(Long reservation_id){
        if(!reservationRepository.existsById(reservation_id)){
            throw new ResourceNotFoundException("Reservation with ID " + reservation_id + " does not exist");
        }
    }
    /**
     * Check if reservation overlaps with others
     * @param startDateTime Start of reservation
     * @param endDateTime End of reservation
     * @param device_id Device ID
     */
    public void checkIfAvailable(LocalDateTime startDateTime, LocalDateTime endDateTime, Long device_id){
        Device device = deviceService.getById(device_id);
        List<Reservation> existedReservations = reservationRepository.findAllByStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqualAndDeviceAndStatusIsNot(startDateTime,endDateTime, device, ReservationStatus.RETURNED);
        if(!existedReservations.isEmpty())
            throw new BadRequestException("Reservation overlaps with others");

    }
    /**
     * Change status of reservation
     * @param reservationId ID of reservation
     * @param newStatus New status
     */
    public void changeStatus(Long reservationId, ReservationStatus newStatus){
        Reservation reservation = getById(reservationId);

        if(newStatus == ReservationStatus.BORROWED && reservation.getStatus().equals(ReservationStatus.RESERVED))
            reservation.setActualStartDateTime(LocalDateTime.now());

        if(newStatus == ReservationStatus.RETURNED && reservation.getStatus().equals(ReservationStatus.BORROWED))
            reservation.setActualEndDateTime(LocalDateTime.now());

        reservation.setStatus(newStatus);
        save(reservation);
    }
    /**
     * Check if reservation can be deleted by current time
     * @param deviceId Device ID
     * @return boolean
     */
    public boolean canDeleteByCurrentTime(Long deviceId){
        LocalDateTime now = LocalDateTime.now();
        return !reservationRepository.existsByStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqualAndStatusAndDeviceId(now,now,ReservationStatus.BORROWED,deviceId);
    }

}
