package org.mtr.web.api.repository.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
//@AllArgsConstructor
@Component
@Entity
@Table(name = "users")
public class UserDAO implements UserDetails, CredentialsContainer {
    @Id
    @Column(columnDefinition = "bigserial", nullable = false)
    private String id;

//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID uuid;
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
    private String profilePhotoLink;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_email", referencedColumnName = "email"),
        inverseJoinColumns = @JoinColumn(name = "role_name", referencedColumnName = "name"))
    private List<RoleDAO> roles = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_friends", joinColumns = @JoinColumn(name = "user_email", referencedColumnName = "email"),
        inverseJoinColumns = @JoinColumn(name = "friend_email", referencedColumnName = "email"))
    private List<UserDAO> friends = new ArrayList<>();


    public UserDAO( String id, String nick, String name, String surname, Date dob, String phonenr, String email, String bio, String password, String profilePhotoLink){
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
        return this.getRoles();
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
}
