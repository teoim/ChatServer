package org.mtr.web.api.repository;

import org.mtr.web.api.repository.dao.UserDAO;
import org.mtr.web.api.repository.rowmapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationRepositoryImpl {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserDAO findUserByEmailAndPassword(String email, String password){
        return this.jdbcTemplate.queryForObject("SELECT * FROM users WHERE email = ? AND password = ?", new UserRowMapper(), email, password);
    }
}
