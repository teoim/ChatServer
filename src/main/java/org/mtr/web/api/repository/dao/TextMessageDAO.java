package org.mtr.web.api.repository.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mtr.web.api.repository.dao.compositeKey.TextMessageID;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Component
@Entity
@Table(name = "messages")
@IdClass(TextMessageID.class)
public class TextMessageDAO implements Comparable<TextMessageDAO>{

    @Id
    private Timestamp timestamp;
    @Id
    private String txtFrom;
    private String txtTo;
    private String content;

    @Override
    public int compareTo(TextMessageDAO o) {
        return this.getTimestamp().compareTo(o.getTimestamp());
    }
}
