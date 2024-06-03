package src.main.java.com.livemilton.book.auth;

import lombok.RequiredArgsConstructor;
import src.main.java.com.livemilton.book.email.EmailService;

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
    private final EmailService emailService;

    public void register(RegistrationRequest request) {
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

    private void sendValidationEmail(com.livemilton.book.user.User user) {

        var newToken= generateAndSaveActivationToken(user);
        //send email
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
        StringRandom sercureRandom = new SecureRandom();

        for(int i=0; i < length; i++){
            int randomIndex = sercureRandom.nextInt(characters.length());//0...9
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}
