package backend.authentication.reposiotry;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.authentication.model.MailVerification;

public interface MailVerificationRepository extends JpaRepository<MailVerification, Long> {
    Optional<MailVerification> findByToken(String token);

    void deleteByExpiryDateBefore(LocalDateTime dateTime);

    MailVerification findByUserId(long id);
}
