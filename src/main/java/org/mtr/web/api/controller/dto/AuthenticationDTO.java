package org.mtr.web.api.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationDTO {
    private String username;
    private String password;
}
