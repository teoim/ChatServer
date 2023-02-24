package org.mtr.web.api.controller.dto;

import jakarta.persistence.Column;
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
    private Date dob;
    private String phonenr;
    private String email;
    private String bio;
    private String password;
}
