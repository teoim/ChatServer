package org.mtr.web.api.repository;

import org.mtr.logger.ErrorLogger;
import org.mtr.logger.MessageLogger;
import org.mtr.web.api.repository.dao.UserDAO;
import org.mtr.web.api.repository.rowmapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationRepositoryImpl {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserDAO findUserByEmailAndPassword(String email, String password){
        MessageLogger.log( "AuthenticationRepositoryImpl - findUserByEmailAndPassword(String,String)");

        UserDAO user = null;

        try{
            user = this.jdbcTemplate.queryForObject("SELECT * FROM users WHERE email = ? AND password = ?", new UserRowMapper(), email, password);
        } catch (DataAccessException e){
            ErrorLogger.log(e, this.getClass().getSimpleName(), "findUserByEmailAndPassword(String,String)");
        }
        finally {
            return user;
        }
    }
}
