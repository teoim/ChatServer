package org.mtr.web.api.repository.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mtr.web.api.repository.dao.compositeKey.TextMessageID;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Objects;

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

    @Override
    public String toString(){
        return "{" + this.getTimestamp() + ": " +
                this.getTxtFrom() + " " +
                this.getTxtTo() + " " +
                this.getContent() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TextMessageDAO that = (TextMessageDAO) o;
        return Objects.equals(getTimestamp(), that.getTimestamp()) && Objects.equals(getTxtFrom(), that.getTxtFrom()) && Objects.equals(getTxtTo(), that.getTxtTo()) && Objects.equals(getContent(), that.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimestamp(), getTxtFrom(), getTxtTo(), getContent());
    }

}
