package iis.project.Reservation;


import iis.project.Reservation.dto.NewReservationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(reservationService.getAll());
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<?> getAllByDeviceId(@PathVariable Long deviceId){
        return ResponseEntity.ok(reservationService.getAllByDeviceId(deviceId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(reservationService.getAllByUserId(userId));
    }

    @PostMapping("/changeStatus/{reservationId}")
    public ResponseEntity<?> changeStatus(@PathVariable Long reservationId, @RequestBody ReservationStatus newStatus){
        reservationService.changeStatus(reservationId, newStatus);
        return ResponseEntity.ok("Status was changed successfully");
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> delete(@PathVariable Long reservationId){
        reservationService.delete(reservationId);
        return ResponseEntity.ok("Reservation was deleted successfully");
    }
}
