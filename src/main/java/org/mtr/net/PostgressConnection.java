package org.mtr.net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgressConnection {

    private Connection connection;

    /**
     *
     * @param url the database url to connect to. Example: "jdbc:postgresql://localhost:5432/teodb"
     * @param user db username
     * @param password password
     * @throws SQLException
     */
    public PostgressConnection(String url, String user, String password) throws SQLException {
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
            System.out.println("Connected to the PostgreSQL server successfully. " + conn.getCatalog());
        } catch (SQLException e) {
            System.out.println("PostgressConnection - connect() - Error: " + e.getMessage());
            throw e;
        }

        return conn;
    }
}
