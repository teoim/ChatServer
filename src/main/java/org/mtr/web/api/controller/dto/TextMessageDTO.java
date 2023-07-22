package org.mtr.web.api.controller.dto;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class TextMessageDTO implements Comparable<TextMessageDTO>{

    private Timestamp timestamp;
    private String from;
    private String to;
    private String content;

    @Override
    public int compareTo(TextMessageDTO o) {
        return this.getTimestamp().compareTo(o.getTimestamp());
    }
}
