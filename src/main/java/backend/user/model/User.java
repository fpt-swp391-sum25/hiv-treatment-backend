package backend.user.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String fullName;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String address;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String gender;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String accountStatus;

    @Column(unique = true, columnDefinition = "VARCHAR(100)")
    private String phoneNumber;

    @Column(unique = true, columnDefinition = "VARCHAR(100)")
    private String email;

    @Column(unique = true, columnDefinition = "VARCHAR(100)")
    private String username;

    @Column(columnDefinition = "VARCHAR(255)")
    private String password;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String avatar;

    private LocalDate dateOfBirth;

    private LocalDate createdAt;

    private boolean isVerified;

    private Role role;

    @JsonProperty("displayId")
    public String getDisplayId() {
        return role.name().substring(0, 3) + "-" + id;
    }
}
