package org.mtr.web.api.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.Map;

@Component
@SessionScope
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSession {
    // Tinem minte date despre client in sesiunea curenta

    private String email;
    private String username;
    private String bio;

    private Map<String, String> messages = new HashMap<>();
}
