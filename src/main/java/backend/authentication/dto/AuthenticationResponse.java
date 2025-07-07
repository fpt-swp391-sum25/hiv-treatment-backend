package backend.authentication.dto;

import io.micrometer.common.lang.NonNull;

public record AuthenticationResponse(
                @NonNull String token,

                @NonNull String userName,

                @NonNull String fullName,

                @NonNull String role) {
}
