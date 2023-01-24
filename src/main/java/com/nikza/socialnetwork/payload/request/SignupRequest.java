package com.nikza.socialnetwork.payload.request;

import com.nikza.socialnetwork.annotations.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatches
public class SignupRequest {
    @Email(message = "wrong email")
    @NotBlank(message = "email is required")
    //@ValidEmail
    private String email;
    @NotEmpty(message = "enter firstname")
    private String firstname;
    @NotEmpty(message = "enter lastname")
    private String lastname;
    @NotEmpty(message = "enter username")
    private  String username;
    @NotEmpty(message = "enter password")
    @Size(min = 6)
    private String password;
    @NotEmpty(message = "confirm password")
    private String confirmPassword;
}
