package org.mtr.web.api.controller.dto;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class TextMessageDTO {

    private Timestamp timestamp;
    private String from;
    private String to;
    private String content;

}
