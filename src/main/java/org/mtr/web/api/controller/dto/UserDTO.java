package org.mtr.web.api.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String nick;
    private String name;
    private String surname;
    @Past(message = "Date of birth must be in the past.")
    private Date dob;
    private String phonenr;
    @Email(message = "Invalid email.")
    private String email;
    private String bio;
    private String password;
    private String profilePhotoLink;
}
