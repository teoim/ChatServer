package org.mtr.web.api.repository.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Subselect;

@Getter
@Entity
@Table(name = "roles_id_seq")
@Subselect("select * from roles_id_seq")
public class RolesIdSeqDAO {

    @Id
    @Column
    int last_value;
}
