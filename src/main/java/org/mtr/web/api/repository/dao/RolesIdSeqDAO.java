package org.mtr.web.api.repository.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Subselect;

@Entity
@Table(name = "roles_id_seq")
@Subselect("select * from roles_id_seq")
public class RolesIdSeqDAO {

    @Id
    @Column
    @Getter @Setter
    int last_value;
}
