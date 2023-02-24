package org.mtr.web.api.repository.rowmapper;

import org.mtr.web.api.repository.dao.UserDAO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<UserDAO> {

    @Override
    public UserDAO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserDAO(rs.getString("id"),
                rs.getString("nick"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getDate("dob"),
                rs.getString("phonenr"),
                rs.getString("email"),
                rs.getString("bio"),
                rs.getString("password"));
    }
}
