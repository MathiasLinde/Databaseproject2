package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {


    private static final String HOST = "localhost";

    private static final int    PORT    = 3306;

    private static final String DATABASE = "tv3db";

    private static final String USERNAME = "root"; // insert you username for your MariaDB

    private static final String PASSWORD = ""; // Insert your password
    private static Connection connection;

    public static Connection getConnection() {
        return connection;
    }

    Connector (){

        try {
            String url = "jdbc:mariadb://" + HOST + ":" + PORT + "/" + DATABASE;

            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);

            createDatabaseSchema();

        }catch (SQLException e ){
            e.printStackTrace();
        }

    }
    private void createDatabaseSchema(){

    }

}
