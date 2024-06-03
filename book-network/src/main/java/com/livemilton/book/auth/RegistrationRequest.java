package src.main.java.com.livemilton.book.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message="Firstname is mandatory")
    @NotBlank(message="Firstname is mandatory")
    private String firstname;
    @NotEmpty(message="Lastname is mandatory")
    @NotBlank(message="Lastname is mandatory")
    private String lastname;
    @Email(message="Email is not formatted")
    @NotEmpty(message="Email is mandatory")
    @NotBlank(message="Email is mandatory")
    private String email;
    @NotEmpty(message="Password is mandatory")
    @NotBlank(message="Password is mandatory")
    @Size(min=8, message="Password should be 8 characters long minimum")
    private String password;
}
