package backend.user.dto;

import java.time.LocalDate;

public record UpdateUserRequest(
        String phoneNumber,

        String fullName,

        String gender,

        String email,

        String username,

        String password,

        String address,

        String avatar,

        LocalDate dateOfBirth,

        String accountStatus) {
}
