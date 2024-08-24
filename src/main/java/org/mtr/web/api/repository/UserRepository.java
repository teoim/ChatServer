package org.mtr.web.api.repository;

import org.mtr.logger.MessageLogger;
import org.mtr.web.api.repository.dao.UserDAO;
import org.mtr.web.api.repository.rowmapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public UserDAO getUserByEmail(String email){
        MessageLogger.log( "UserRepository - getUserByEmail(String)");
        return this.jdbcTemplate.queryForObject( "SELECT * FROM users WHERE email = ?", new UserRowMapper(), email);
    }

    public int registerUser(UserDAO userDao){
        MessageLogger.log( "UserRepository - registerUser(UserDAO)");

        return this.jdbcTemplate.update(
                "INSERT INTO users(nick, name, surname, dob, phonenr, email, bio, password, profile_photo_link) " +
                        "VALUES ( ? , ? , ? , ? , ? , ? , ? , ?, ?)",
                //UUID.randomUUID(),
                userDao.getNick(),
                userDao.getName(),
                userDao.getSurname(),
                userDao.getDob(),
                userDao.getPhonenr(),
                userDao.getEmail(),
                userDao.getBio(),
                userDao.getPassword(),
                userDao.getProfilePhotoLink());
    }
}
