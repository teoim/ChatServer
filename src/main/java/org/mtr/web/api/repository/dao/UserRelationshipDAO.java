package org.mtr.web.api.repository.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mtr.web.api.controller.dto.UserRelationshipDTO;

import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "user_relationship")
public class UserRelationshipDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "UserRelationshipDAO.userId cannot be empty.")
    private String userId;

    @NotEmpty(message = "UserRelationshipDAO.friendId cannot be empty.")
    private String friendId;

    private String status;

    @CreationTimestamp
    private Timestamp inRelationshipSince;

    @UpdateTimestamp
    private Timestamp lastUpdated;

    @Override
    public String toString() {
        return "UserRelationshipDAO{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", friendId='" + friendId + '\'' +
                ", status='" + status + '\'' +
                ", inRelationshipSince=" + inRelationshipSince +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserRelationshipDAO that = (UserRelationshipDAO) o;
        // Ignore the 'nano' piece on equals and hashcode
        Timestamp thatTimestamp1 = that.inRelationshipSince;
        thatTimestamp1.setNanos(0);
        Timestamp thisTimestamp1 = this.inRelationshipSince;
        thisTimestamp1.setNanos(0);
        Timestamp thatTimestamp2 = that.lastUpdated;
        thatTimestamp1.setNanos(0);
        Timestamp thisTimestamp2 = null;
        if(this.lastUpdated!=null) {
            thisTimestamp2 = this.lastUpdated;
            thisTimestamp1.setNanos(0);
        }
        return id == that.id
                && Objects.equals(userId, that.userId)
                && Objects.equals(friendId, that.friendId)
                && Objects.equals(status, that.status)
                && Math.abs(thisTimestamp1.getTime() - thatTimestamp1.getTime()) < 1_000
                && (this.lastUpdated == null) || (Math.abs(thisTimestamp2.getTime() - thatTimestamp2.getTime()) < 1_000);
    }

    @Override
    public int hashCode() {
        // Ignore the 'nano' piece on equals and hashcode
        Timestamp hashTimestamp1 = this.inRelationshipSince;
        hashTimestamp1.setNanos(0);
        Timestamp hashTimestamp2 = null;
        if(this.lastUpdated!=null) {
            hashTimestamp2 = this.lastUpdated;
            hashTimestamp2.setNanos(0);
        }
        return Objects.hash(id, userId, friendId, status, hashTimestamp1, hashTimestamp2);
    }

    public UserRelationshipDTO toDto(){
        return new UserRelationshipDTO(this.userId, this.friendId, this.status, this.inRelationshipSince, this.lastUpdated);
    }
}
