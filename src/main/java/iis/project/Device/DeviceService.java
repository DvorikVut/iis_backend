package iis.project.Device;

import iis.project.Device.dto.DeviceInfoDTO;
import iis.project.Device.dto.DeviceInfoDTOMapper;
import iis.project.Device.dto.NewDeviceDTO;
import iis.project.DeviceType.DeviceTypeService;
import iis.project.EmailSender.EmailSenderService;
import iis.project.Exceptions.NotAuthorizedException;
import iis.project.Exceptions.ResourceNotFoundException;
import iis.project.Logger.MyLogger;
import iis.project.Reservation.Reservation;
import iis.project.Reservation.ReservationService;
import iis.project.Studio.Studio;
import iis.project.Studio.StudioService;
import iis.project.User.Role;
import iis.project.User.User;
import iis.project.User.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserService userService;
    private final DeviceTypeService deviceTypeService;
    private final StudioService studioService;
    private final ReservationService reservationService;
    private final EmailSenderService emailSenderService;
    private final DeviceInfoDTOMapper deviceInfoDTOMapper;
    private final MyLogger myLogger;

    public DeviceService(@Lazy DeviceRepository deviceRepository, @Lazy UserService userService, @Lazy DeviceTypeService deviceTypeService, @Lazy StudioService studioService, @Lazy ReservationService reservationService, @Lazy EmailSenderService emailSenderService, @Lazy DeviceInfoDTOMapper deviceInfoDTOMapper, MyLogger myLogger) {
        this.deviceRepository = deviceRepository;
        this.userService = userService;
        this.deviceTypeService = deviceTypeService;
        this.studioService = studioService;
        this.reservationService = reservationService;
        this.emailSenderService = emailSenderService;
        this.deviceInfoDTOMapper = deviceInfoDTOMapper;
        this.myLogger = myLogger;
    }

    public DeviceInfoDTO create(NewDeviceDTO newDeviceDTO) {
        if (!((userService.checkCurrentUserRole(Role.ADMIN)) || (userService.checkCurrentUserRole(Role.TEACHER)))) {
            throw new NotAuthorizedException("You are not authorized to create device");
        }

        Device device = Device.builder()
                .name(newDeviceDTO.name())
                .deviceType(deviceTypeService.getById(newDeviceDTO.deviceType_id()))
                .description(newDeviceDTO.description())
                .yearOfManufacture(newDeviceDTO.yearOfManufacture())
                .purchaseDate(newDeviceDTO.purchaseDate())
                .users(new ArrayList<User>())
                .maximumLoanPeriodInHours(newDeviceDTO.maximumLoanPeriodInHours())
                .studio(studioService.getById(newDeviceDTO.studio_id()))
                .DisabledForBorrowing(false)
                .forAll(newDeviceDTO.forAll())
                .owner(userService.getCurrentUser())
                .build();
        save(device);

        if (device.getForAll())
            allowDeviceToAllUsersInStudio(device.getId());

        return deviceInfoDTOMapper.apply(device);
    }

    public void delete(Long device_id) {

        //1 - check if it is borrowed ( Find reservations, where start - currentTime - end)
        if(!reservationService.canDeleteByCurrentTime(device_id))
            throw new RuntimeException("You cannot delete device when it is borrowed");

        List<Reservation> futureReservations = reservationService.getFutureReservations(device_id);
        for(Reservation reservation : futureReservations){
            reservationService.delete(reservation.getId());
            emailSenderService.sendSimpleMessage(reservation.getUser().getEmail(), "Cancel reservation", "Your reservation with " + reservation.getDevice().getName() + " was cancelled, this device is not more available");
        }
    }

    public void change(Long device_id, NewDeviceDTO newDeviceDTO) {
        if (!((userService.checkCurrentUserRole(Role.ADMIN)) || (userService.checkCurrentUserRole(Role.TEACHER)))) {
            throw new NotAuthorizedException("You are not authorized to change device");
        }

        checkIfExist(device_id);

        Long owner_id = getById(device_id).getOwner().getId();

        if (!(Objects.equals(userService.getCurrentUser().getId(), owner_id))) {
            throw new NotAuthorizedException("You must be the owner to change the device");
        }

        Device device = getById(device_id);

        device.setDeviceType(deviceTypeService.getById(newDeviceDTO.deviceType_id()));
        device.setName(newDeviceDTO.name());
        device.setDescription(newDeviceDTO.description());
        device.setPurchaseDate(newDeviceDTO.purchaseDate());
        device.setYearOfManufacture(newDeviceDTO.yearOfManufacture());

        save(device);
    }

    public Device getById(Long deviceId) {
        return deviceRepository.getReferenceById(deviceId);
    }

    public DeviceInfoDTO getInfoById(Long deviceId){
        return deviceInfoDTOMapper.apply(getById(deviceId));
    }
    public void checkIfExist(Long device_id) {
        if (!deviceRepository.existsById(device_id)) {
            throw new ResourceNotFoundException("Device with ID " + device_id + " does not exist");
        }
    }
    public void save(Device device) {
        deviceRepository.save(device);
    }
    public void allowUserToAllDevicesInStudio(Long userId, Long studio_id) {
        Studio studio = studioService.getById(studio_id);
        User user = userService.getById(userId);
        List<Device> devicesInStudio = deviceRepository.findAllByStudio(studio);
        for (Device device : devicesInStudio) {
            if (device.getForAll()) {
                device.getUsers().add(user);
                save(device);
            }
        }
    }

    public void allowDeviceToAllUsersInStudio(Long device_id) {
        Device device = getById(device_id);
        List<User> usersInDevice = device.getUsers();
        List<User> usersInStudio = device.getStudio().getUsers();
        usersInDevice.addAll(usersInStudio);
        device.setUsers(usersInDevice);
        save(device);
    }
    public List<DeviceInfoDTO> getAllByStudioId(Long studio_id) {
        return deviceRepository.findAllByStudio(studioService.getById(studio_id))
                .stream()
                .map(deviceInfoDTOMapper)
                .collect(Collectors.toList());
    }

    //Get all the devices that current user can borrow
    public List<DeviceInfoDTO> getAllByUserCanBorrow(){
        return deviceRepository.findAllByUsersContaining(userService.getById(userService.getCurrentUser().getId()))
                .stream()
                .map(deviceInfoDTOMapper)
                .collect(Collectors.toList());
    }
    public boolean checkIfCanBorrow(Long userId, Long deviceId){
        return deviceRepository.existsByIdAndUsersContaining(deviceId, userService.getById(userId));
    }
    public List<DeviceInfoDTO> getAll() {
        return deviceRepository.findAll()
                .stream()
                .map(deviceInfoDTOMapper)
                .collect(Collectors.toList());
    }
    public List<DeviceInfoDTO> getAllByOwnerId(){
        User owner = userService.getCurrentUser();
        return deviceRepository.findAllByOwnerId(owner.getId())
                .stream()
                .map(deviceInfoDTOMapper)
                .collect(Collectors.toList());
    }
    public List<Device> getAllByDeviceTypeId(Long deviceTypeId) {
        return deviceRepository.findAllByDeviceTypeId(deviceTypeId);
    }
}
