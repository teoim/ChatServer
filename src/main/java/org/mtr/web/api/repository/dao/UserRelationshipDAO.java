package org.mtr.web.api.repository.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user_relationship")
public class UserRelationshipDAO {

    @Id
    @Column(columnDefinition = "bigserial", nullable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message = "UserRelationshipDAO.userId cannot be empty.")
    private String userId;

    @NotEmpty(message = "UserRelationshipDAO.friendId cannot be empty.")
    private String friendId;
    private String status;

    private Timestamp inRelationshipSince;
}
