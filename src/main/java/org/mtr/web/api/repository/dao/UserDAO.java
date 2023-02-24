package org.mtr.web.api.repository.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@Entity
public class UserDAO {
    @Id
    @Column(nullable = false)
    private String id;
    private String nick;
    private String name;
    private String surname;
    private Date dob;
    @Column(unique = true)
    private String phonenr;
    @Column(unique = true, nullable = false)
    private String email;
    private String bio;
    private String password;

    public UserDAO( String id, String nick, String name, String surname, Date dob, String phonenr, String email, String bio, String password){
        this.id = id;
        this.nick = nick;
        this.name = name;
        this.surname = surname;
        this.dob = dob;
        this.phonenr = phonenr;
        this.email = email;
        this.bio = bio;
        this.password = password;
    }
}
