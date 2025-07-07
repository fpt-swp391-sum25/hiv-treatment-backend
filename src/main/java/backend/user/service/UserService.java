package backend.user.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.authentication.dto.AuthenticationResponse;
import backend.authentication.model.MailVerification;
import backend.authentication.reposiotry.MailVerificationRepository;
import backend.user.dto.CreateUserRequest;
import backend.user.dto.UpdateUserRequest;
import backend.user.model.Role;
import backend.user.model.User;
import backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailVerificationRepository mailVerificationRepository;

    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    // Create account with required mail verification
    public AuthenticationResponse create(CreateUserRequest request) {
        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .accountStatus("ACTIVE")
                .role(Role.valueOf(request.role().toUpperCase()))
                .createdAt(LocalDate.now())
                .isVerified(false)
                .build();
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        MailVerification verificationToken = MailVerification.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();
        mailVerificationRepository.save(verificationToken);

        String subject = "Verify your email";
        String verificationUrl = "http://localhost:8080/api/verify?token=" + token;
        String body = "Click the link to verify your email: " + verificationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);

        return new AuthenticationResponse(token, user.getUsername(), user.getFullName(), user.getRole().name());
    }

    // Read user detail
    public User get(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO USER FOUND WITH ID: " + id));
    }

    // Update user
    public String update(long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO USER FOUND WITH ID: " + id));

        Optional.ofNullable(request.phoneNumber()).ifPresent(user::setPhoneNumber);
        Optional.ofNullable(request.fullName()).ifPresent(user::setFullName);
        Optional.ofNullable(request.gender()).ifPresent(user::setGender);
        Optional.ofNullable(request.email()).ifPresent(user::setEmail);
        Optional.ofNullable(request.username()).ifPresent(user::setUsername);
        Optional.ofNullable(request.password())
                .map(passwordEncoder::encode)
                .ifPresent(user::setPassword);
        Optional.ofNullable(request.address()).ifPresent(user::setAddress);
        Optional.ofNullable(request.avatar()).ifPresent(user::setAvatar);
        Optional.ofNullable(request.dateOfBirth()).ifPresent(user::setDateOfBirth);
        userRepository.save(user);

        return "USER UPDATED SUCCESSFULLY WITH ID: " + id;
    }

    // Delete user
    public String delete(long id) {
        userRepository.delete(userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO USER FOUND WITH ID: " + id)));

        return "USER DELETED SUCCESSFULLY WITH ID: " + id;
    }

    // List users by role
    public List<User> listByRole(String role) {

        List<User> users = userRepository.findUsersByRole(Role.valueOf(role.toUpperCase()));
        if (users.isEmpty())

            return users;

        return userRepository.findUsersByRole(Role.valueOf(role.toUpperCase()));

    }

    // Search user by role and full name
    public List<User> search(Role role, String searchString) {
        return userRepository.findByFullNameContaining(searchString);
    }

    // Search user by phone number
    public User getByPhoneNumber(Role role, String searchString) {
        return userRepository.findByPhoneNumberContaining(searchString)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "NO USER FOUND WITH PHONE NUMBER: " + searchString));
    }

    // Search user by mail
    public User getByEmail(Role role, String searchString) {
        return userRepository.findByEmailContaining(searchString)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "NO USER FOUND WITH EMAIL: " + searchString));
    }

    // List users by mail verification status
    public List<User> getByIsVerified(Role role, boolean isVerified) {
        return userRepository.findByIsVerified(isVerified);
    }

    // List users by account status
    public List<User> getByAccountStatus(Role role, String status) {
        return userRepository.findByAccountStatus(status);
    }
}
