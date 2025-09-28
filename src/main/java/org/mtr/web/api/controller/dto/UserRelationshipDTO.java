package org.mtr.web.api.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class UserRelationshipDTO {
    private String thisUser;
    private String thatUser;
    private String relationship;
    private Timestamp inRelationshipSince;
}
