package org.mtr.web.api.repository.dao;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.sql.Date;
import java.util.*;

@Data
@NoArgsConstructor
//@AllArgsConstructor
@Component
@Entity
@Table(name = "users")
public class UserDAO implements UserDetails, CredentialsContainer {
    @Id
    @Column(columnDefinition = "bigserial", nullable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID uuid;
    private String nick;
    private String name;
    private String surname;
    private Date dob;
    @Column(unique = true)
    private String phonenr;
    @PrimaryKeyJoinColumn
    @Column(unique = true, nullable = false)
    private String email;
    private String bio;
    private String password;
    private String profilePhotoLink;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_email", referencedColumnName = "email"),
        inverseJoinColumns = @JoinColumn(name = "role_name", referencedColumnName = "name"))
    private List<RoleDAO> userRoles = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinTable(name = "user_friends", joinColumns = @JoinColumn(name = "user_email", referencedColumnName = "email"),
        inverseJoinColumns = @JoinColumn(name = "friend_email", referencedColumnName = "email"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_email", "friend_email" }))
    private List<UserDAO> friends = new ArrayList<>();
//    private Set<UserDAO> friends = new HashSet<>();

/*    @ManyToMany     //(fetch = FetchType.EAGER)
    @JoinTable(name = "user_friends", joinColumns = @JoinColumn(name = "friend_email", referencedColumnName = "email"),
            inverseJoinColumns = @JoinColumn(name = "user_email", referencedColumnName = "email"))*/
    @ToString.Exclude
    @ManyToMany(mappedBy = "friends")
    private List<UserDAO> friendsOf = new ArrayList<>();
//    private Set<UserDAO> friendsOf = new HashSet<>();


    public UserDAO( Long id, String nick, String name, String surname, Date dob, String phonenr, String email, String bio, String password, String profilePhotoLink){
        this.id = id;
        this.nick = nick;
        this.name = name;
        this.surname = surname;
        this.dob = dob;
        this.phonenr = phonenr;
        this.email = email;
        this.bio = bio;
        this.password = password;
        this.profilePhotoLink = profilePhotoLink;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getUserRoles();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void eraseCredentials() {
        this.setPassword(null);
        this.setEmail(null);
        this.setPhonenr(null);
    }

//    @Override
//    public String toString(){
//        String separator = ", ";
//        return "UserDAO { id : " + this.getId() + separator +
//                " nick : " + this.getNick() + separator +
//                " name : " + this.getName() + separator +
//                " surname : " + this.getSurname() + separator +
//                " dob : " + this.getDob() + separator +
//                " phonenr : " + this.getPhonenr() + separator +
//                " email : " + this.getEmail() + separator +
//                " bio : " + this.getBio() + " };";
//    }
}
