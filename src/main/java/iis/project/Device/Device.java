package iis.project.Device;

import iis.project.DeviceType.DeviceType;
import iis.project.Studio.Studio;
import iis.project.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Integer yearOfManufacture;
    private LocalDate purchaseDate;
    private Integer maximumLoanPeriodInHours;
    private Boolean forAll;
    private Boolean DisabledForBorrowing;

    @ManyToOne
    @JoinColumn(name = "deviceType_id", referencedColumnName = "id", nullable = false)
    private DeviceType deviceType;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "studio_id", referencedColumnName = "id", nullable = false)
    private Studio studio;


    @ManyToMany
    @JoinTable(
            name = "device_access",
            joinColumns = @JoinColumn(name = "device_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    List<User> users;
}
