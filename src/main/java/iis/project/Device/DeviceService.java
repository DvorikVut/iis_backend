package iis.project.Device;

import iis.project.Device.dto.DeviceInfoDTO;
import iis.project.Device.dto.DeviceInfoDTOMapper;
import iis.project.Device.dto.NewDeviceDTO;
import iis.project.DeviceHours.DeviceHoursService;
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
import iis.project.User.dto.UserInfo;
import iis.project.User.dto.UserInfoDTOMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
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
    private final UserInfoDTOMapper userInfoDTOMapper;
    private final DeviceService deviceService;
    private final DeviceHoursService deviceHoursService;

    private final MyLogger myLogger;

    public DeviceService(@Lazy DeviceRepository deviceRepository, @Lazy UserService userService, @Lazy DeviceTypeService deviceTypeService, @Lazy StudioService studioService, @Lazy ReservationService reservationService, @Lazy EmailSenderService emailSenderService, @Lazy DeviceInfoDTOMapper deviceInfoDTOMapper, @Lazy UserInfoDTOMapper userInfoDTOMapper, @Lazy DeviceService deviceService, @Lazy DeviceHoursService deviceHoursService, MyLogger myLogger) {
        this.deviceRepository = deviceRepository;
        this.userService = userService;
        this.deviceTypeService = deviceTypeService;
        this.studioService = studioService;
        this.reservationService = reservationService;
        this.emailSenderService = emailSenderService;
        this.deviceInfoDTOMapper = deviceInfoDTOMapper;
        this.userInfoDTOMapper = userInfoDTOMapper;
        this.deviceService = deviceService;
        this.deviceHoursService = deviceHoursService;
        this.myLogger = myLogger;
    }
    /**
     * Creates a new device based on the provided DTO.
     * This method checks if the current user has the required role (ADMIN or TEACHER)
     * and builds a new device object with the provided data.
     * If the device is available for everyone (`forAll` is true), it grants access to all users in the studio.
     *
     * @param newDeviceDTO The data transfer object containing device information.
     * @return A success message if the device was created successfully.
     */

    public String create(NewDeviceDTO newDeviceDTO) {
        if (!((userService.checkCurrentUserRole(Role.ADMIN)) || (userService.checkCurrentUserRole(Role.TEACHER)))) {
            throw new NotAuthorizedException("You are not authorized to create device");
        }

        Device device = Device.builder()
                .name(newDeviceDTO.name())
                .deviceType(deviceTypeService.getById(newDeviceDTO.deviceType_id()))
                .description(newDeviceDTO.description())
                .yearOfManufacture(newDeviceDTO.yearOfManufacture())
                .purchaseDate(newDeviceDTO.purchaseDate())
                .maximumLoanPeriodInHours(newDeviceDTO.maximumLoanPeriodInHours())
                .studio(studioService.getById(newDeviceDTO.studio_id()))
                .disabledForBorrowing(newDeviceDTO.disabledForBorrowing())
                .forAll(newDeviceDTO.forAll())
                .owner(userService.getCurrentUser())
                .build();

        save(device);

        if (device.getForAll())
            allowDeviceToAllUsersInStudio(device.getId());

        return "Success";
    }

    /**
     * Deletes a device with the provided ID.
     * This method checks if the device is currently borrowed by a user.
     * If the device is borrowed, it cancels all future reservations and sends an email to the users.
     * Finally, it deletes the device from the database.
     *
     * @param device_id The ID of the device to delete.
     */

    public void delete(Long device_id) {

        if(!reservationService.canDeleteByCurrentTime(device_id))
            throw new RuntimeException("You cannot delete device when it is borrowed");

        List<Reservation> futureReservations = reservationService.getFutureReservations(device_id);
        for(Reservation reservation : futureReservations){
            emailSenderService.sendSimpleMessage(reservation.getUser().getEmail(), "Cancel reservation", "Your reservation with " + reservation.getDevice().getName() + " was cancelled, this device is not more available");
            reservationService.delete(reservation.getId());
        }
        deviceRepository.deleteById(device_id);
    }

    /**
     * Changes the device information based on the provided DTO.
     * This method checks if the current user has the required role (ADMIN or TEACHER)
     * and if the user is the owner of the device.
     * If the user is the owner, it updates the device information with the provided data.
     *
     * @param device_id The ID of the device to change.
     * @param newDeviceDTO The data transfer object containing the new device information.
     */

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
        device.setForAll(newDeviceDTO.forAll());
        device.setName(newDeviceDTO.name());
        device.setDisabledForBorrowing(newDeviceDTO.disabledForBorrowing());
        device.setMaximumLoanPeriodInHours(newDeviceDTO.maximumLoanPeriodInHours());
        device.setDescription(newDeviceDTO.description());
        device.setPurchaseDate(newDeviceDTO.purchaseDate());
        device.setYearOfManufacture(newDeviceDTO.yearOfManufacture());
        save(device);
    }

    /**
     * Gets a device by its ID.
     *
     * @param deviceId The ID of the device to get.
     * @return The device with the provided ID.
     */

    public Device getById(Long deviceId) {
        return deviceRepository.getReferenceById(deviceId);
    }


    /**
     * Gets the device information by its ID.
     *
     * @param deviceId The ID of the device to get.
     * @return The device information with the provided ID.
     */

    public DeviceInfoDTO getInfoById(Long deviceId){
        return deviceInfoDTOMapper.apply(getById(deviceId));
    }
    public void checkIfExist(Long device_id) {
        if (!deviceRepository.existsById(device_id)) {
            throw new ResourceNotFoundException("Device with ID " + device_id + " does not exist");
        }
    }

    /**
     * Saves a device to the database.
     *
     * @param device The device to save.
     */
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

    /**
     * Grants access to a device to all users in the studio.
     *
     * @param device_id The ID of the device to grant access to.
     */

    public void allowDeviceToAllUsersInStudio(Long device_id) {
        Device device = getById(device_id);
        Hibernate.initialize(device.getUsers());
        Hibernate.initialize(device.getStudio().getUsers());
        List<User> usersInDevice = device.getUsers();
        List<User> usersInStudio = device.getStudio().getUsers();
        usersInDevice.addAll(usersInStudio);
        device.setUsers(usersInDevice);
        save(device);
    }

    /**
     * Removes a user from a device.
     *
     * @param userId The ID of the user to remove.
     * @param studioId The ID of the studio
     */

    public void removeUserFromUserAccess(Long userId, Long studioId){
        Studio studio = studioService.getById(studioId);
        User user = userService.getById(userId);
        List<Device> devicesInStudio = deviceRepository.findAllByStudio(studio);
        for (Device device : devicesInStudio) {
                device.getUsers().remove(user);
                save(device);
        }
    }


    /**
     * @param studio_id
     * @return List of devices in the studio
     */
    public List<DeviceInfoDTO> getAllByStudioId(Long studio_id) {
        return deviceRepository.findAllByStudio(studioService.getById(studio_id))
                .stream()
                .map(deviceInfoDTOMapper)
                .collect(Collectors.toList());
    }


    /**
     *
     * @return
     */

    public List<DeviceInfoDTO> getAllByUserCanBorrow(){
        if(userService.checkCurrentUserRole(Role.ADMIN))
            return getAll();

        return deviceRepository.findAllByUsersContaining(userService.getById(userService.getCurrentUser().getId()))
                .stream()
                .filter(device -> !device.getDisabledForBorrowing())
                .map(deviceInfoDTOMapper)
                .collect(Collectors.toList());
    }
    public boolean checkIfCanBorrow(Long userId, Long deviceId){
        Device device = deviceService.getById(deviceId);
        return (deviceRepository.existsByIdAndUsersContaining(deviceId, userService.getById(userId)) || userService.checkCurrentUserRole(Role.ADMIN)) && !device.getDisabledForBorrowing();
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

    public void addUsersToDevice(Long deviceId, List<Long> userIds){
        Device device = getById(deviceId);
        List<User> usersInDevice = device.getUsers();
        List<User> usersToAdd = userIds.stream()
                .map(userService::getById) // Получаем объект User для каждого ID
                .toList();

        usersInDevice.addAll(usersToAdd);
        device.setUsers(usersInDevice);
        save(device);
    }

    public List<UserInfo> getAllUsersThatCanBorrowDevice(Long deviceId) {
        Device device = getById(deviceId);
        return device.getUsers()
                .stream()
                .map(userInfoDTOMapper)
                .collect(Collectors.toList());
    }
}
