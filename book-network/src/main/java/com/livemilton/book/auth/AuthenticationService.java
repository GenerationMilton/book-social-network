package com.livemilton.book.auth;
import com.livemilton.book.email.EmailTemplateName;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.livemilton.book.email.EmailService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final com.livemilton.book.role.RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.livemilton.book.user.UserRepository userRepository;
    private final com.livemilton.book.user.TokenRepository tokenRepository;
    private EmailService emailService;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                // todo - better exception handling
                .orElseThrow(()-> new IllegalStateException("ROLE USER was not initialized"));
        var user = com.livemilton.book.user.User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(com.livemilton.book.user.User user) throws MessagingException {

        var newToken= generateAndSaveActivationToken(user);
        //send email
        emailService.sendEmail(
                user.getEmail(),
                user.fullname(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );

    }

    private String generateAndSaveActivationToken(com.livemilton.book.user.User user) {
        //generate activation token
        String generatedToken=generateAndSaveCode(6);
        var token= com.livemilton.book.user.Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateAndSaveCode(int length) {
        String characters= "0123456789";
        StringBuilder codeBuilder= new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for(int i=0; i < length; i++){
            int randomIndex = secureRandom.nextInt(characters.length());//0...9
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}
