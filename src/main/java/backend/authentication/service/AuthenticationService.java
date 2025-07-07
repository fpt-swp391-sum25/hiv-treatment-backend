package backend.authentication.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.authentication.dto.AccountResponse;
import backend.authentication.dto.AuthenticationResponse;
import backend.authentication.dto.LoginRequest;
import backend.authentication.dto.RegisterRequest;
import backend.authentication.model.MailVerification;
import backend.authentication.reposiotry.MailVerificationRepository;
import backend.security.CustomUserDetails;
import backend.security.JwtService;
import backend.user.model.Role;
import backend.user.model.User;
import backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
        @Autowired
        private final UserRepository userRepository;

        @Autowired
        private final MailVerificationRepository mailVerificationRepository;

        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final JavaMailSender mailSender;

        // Register account with required mail verification
        public AuthenticationResponse register(RegisterRequest request) {
                if (userRepository.findByUsername(request.username()).isPresent())
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "USERNAME ALREADY REGISTERED");

                var user = User.builder()
                                .fullName(request.fullName())
                                .gender(request.gender())
                                .dateOfBirth(request.dateOfBirth())
                                .email(request.email())
                                .address(request.address())
                                .username(request.username())
                                .password(passwordEncoder.encode(request.password()))
                                .accountStatus("ACTIVE")
                                .role(Role.PATIENT)
                                .createdAt(LocalDate.now())
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
                String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + token;
                String body = "Click the link to verify your email: " + verificationUrl;

                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);

                UserDetails userDetails = new CustomUserDetails(user);
                String jwtToken = jwtService.generateToken(userDetails);

                return new AuthenticationResponse(jwtToken, user.getUsername(), user.getFullName(),
                                user.getRole().name());
        }

        // Send mail verification status
        public String verify(String token) {
                Optional<MailVerification> mailVerification = mailVerificationRepository
                                .findByToken(token);

                if (mailVerification.isEmpty()
                                || mailVerification.get().getExpiryDate().isBefore(LocalDateTime.now())) {
                        return "INVALID OR EXPIRED TOKEN";
                }

                User user = mailVerification.get().getUser();
                user.setVerified(true);
                userRepository.save(user);

                return "MAIL VERIFIED SUCCESSFULLY";
        }

        // Log in with user name and password
        public AuthenticationResponse login(LoginRequest request) {
                User user = userRepository.findByUsername(request.username())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "NO USER FOUND WITH USERNAME: " + request.username()));

                if (user.getAccountStatus().equals("UNACTIVE")
                                || !user.isVerified())
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                        "ACCOUNT IS UNACTIVE OR NOT VERIFIED YET");

                UserDetails userDetails = new CustomUserDetails(user);
                String jwtToken = jwtService.generateToken(userDetails);

                return new AuthenticationResponse(jwtToken, user.getUsername(), user.getFullName(),
                                user.getRole().name());
        }

        public AuthenticationResponse loginOrRegisterGoogleUser(String email, String fullName) {
                User user = userRepository.findByEmail(email).orElseGet(() -> {
                        User newUser = User.builder()
                                        .username(email)
                                        .email(email)
                                        .fullName(fullName)
                                        .password(passwordEncoder.encode("google-auth-" + email))
                                        .role(Role.PATIENT)
                                        .accountStatus("ACTIVE")
                                        .isVerified(true)
                                        .createdAt(LocalDate.now())
                                        .build();
                        return userRepository.save(newUser);
                });

                String jwt = jwtService.generateToken(new CustomUserDetails(user));
                return new AuthenticationResponse(jwt, user.getUsername(), user.getFullName(), user.getRole().name());
        }

        // Retrieve user account information
        public AccountResponse getUserInfo(String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "NO USER FOUND WITH USERNAME: " + username));
                return new AccountResponse(user.getId(), user.getUsername(),
                                user.getEmail(), user.getFullName(), user.getAccountStatus(),
                                user.getPhoneNumber(), user.getAddress(), user.getGender(),
                                user.getDateOfBirth(), user.getAvatar(), user.isVerified(), user.getRole());
        }
}