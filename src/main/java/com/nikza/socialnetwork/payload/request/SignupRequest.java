package com.nikza.socialnetwork.payload.request;

import com.nikza.socialnetwork.annotations.PasswordMatches;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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
    @NotEmpty(message = "enter usertname")
    private  String username;
    @NotEmpty(message = "enter password")
    @Size(min = 6)
    private String password;
    @NotEmpty(message = "confirm firstname")
    private String confirmPassword;
}
