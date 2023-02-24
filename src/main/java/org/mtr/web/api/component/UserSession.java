package org.mtr.web.api.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSession {
    // Tinem minte date despre client in sesiunea curenta

    private String email;
}
