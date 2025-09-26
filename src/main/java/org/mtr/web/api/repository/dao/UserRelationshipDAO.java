package org.mtr.web.api.repository.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "UserRelationshipDAO{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", friendId='" + friendId + '\'' +
                ", status='" + status + '\'' +
                ", inRelationshipSince=" + inRelationshipSince +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserRelationshipDAO that = (UserRelationshipDAO) o;
        // Ignore the 'nano' piece on equals and hashcode
        Timestamp thatTimestamp = that.inRelationshipSince;
        thatTimestamp.setNanos(0);
        Timestamp thisTimestamp = this.inRelationshipSince;
        thisTimestamp.setNanos(0);
        return id == that.id
                && Objects.equals(userId, that.userId)
                && Objects.equals(friendId, that.friendId)
                && Objects.equals(status, that.status)
//                && Objects.equals(inRelationshipSince, that.inRelationshipSince);
                && Math.abs(thisTimestamp.getTime() - thatTimestamp.getTime()) < 1_000;
    }

    @Override
    public int hashCode() {
        // Ignore the 'nano' piece on equals and hashcode
        Timestamp hashTimestamp = this.inRelationshipSince;
        hashTimestamp.setNanos(0);
        return Objects.hash(id, userId, friendId, status, hashTimestamp);
    }
}
