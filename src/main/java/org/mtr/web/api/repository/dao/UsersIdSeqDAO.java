package org.mtr.web.api.repository.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Subselect;

@Getter
@Entity
@Table(name = "users_id_seq")
@Subselect("select * from users_id_seq")        // Maps an immutable and read-only entity to a given SQL select expression.
public class UsersIdSeqDAO {

    @Id
//    @Column(columnDefinition = "bigserial")
    @Column
    long last_value;
}
