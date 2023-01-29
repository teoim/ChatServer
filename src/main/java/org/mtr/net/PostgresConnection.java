package org.mtr.net;

import org.mtr.logger.ErrorLogger;
import org.mtr.logger.MessageLogger;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.sql.*;

public class PostgresConnection {

    private Connection connection;

    private MessageLogger msgLog;
    private ErrorLogger errLog;


    /**
     *
     * @param url the database url to connect to. Example: "jdbc:postgresql://localhost:5432/teodb"
     * @param user db username
     * @param password password
     * @throws SQLException
     */
    public PostgresConnection(String url, String user, String password) throws SQLException {
        connection = this.connect(url, user, password);
    }


    /**
     *
     * @param url
     * @param user
     * @param password
     * @return
     * @throws SQLException
     */
    private Connection connect(String url, String user, String password) throws SQLException{
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            msgLog.log("Connected to the PostgreSQL server successfully. " + conn.getCatalog());
        } catch (SQLException e) {
            errLog.log( e,"PostgresConnection", "connect()");
            throw e;
        }

        return conn;
    }

    public String login(String email, String password){
        String response = "";
        String bio = "", name = "", surname = "";

        try {
            // TODO: Extra input validation
            if( email == null || email.isBlank() || email.isEmpty()){ throw new InvalidParameterException("Email cannot be null or empty!"); }
            if( password == null || password.isBlank() || password.isEmpty()){ throw new InvalidParameterException("Password cannot be null or empty"); }

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM USERS WHERE email=?");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if ( resultSet.next()) {
                // Check password
                if ( password.equals( resultSet.getString("password"))) {
                    response = "200\t";
                    name = resultSet.getString("name");
                    response += name + "\t";
                    surname = resultSet.getString("surname");
                    response += surname;
                    bio = resultSet.getString("bio");
                } else {
                    // Invalid password
                    response = invalidCredentials();
                }
            } else {
                // Record not found (invalid email)
                response = invalidCredentials();
            }
        }
        catch( SQLException e){
            response = invalidCredentials();
            errLog.log(e, "PostgresConnection", "login()");
        }
        catch( InvalidParameterException e){
            response = invalidCredentials();
            errLog.log( e, "PostgresConnection", "login()");
        }

        return response;
    }

    private String invalidCredentials(){
        return "401 Unauthorized - Invalid email/password.";
    }
}
