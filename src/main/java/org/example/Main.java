package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Main {
    /**
     * This is the Starting point of the program.
     * It starts out with Creating a repository which also contains a connector object
     * Then a connection is made using the Connector.getConnection method
     * It then uses the droptable PrepareStatement to drops the tables Journalist and Footage
     * This is mainly done for the purposes of demonstrating the program as you would not want to delete the existing table
     * everytime you load and new file, but in this case where we only have one file to load it prevents the program from
     * throwing  SQLIntegrityConstraintViolationException if you run the program more than once the alternative would be to Drop both
     * Tables in the console before rerunning the program
     * After that it Uses two PrepareStatement to create the tables Journalist and Footage
     * After that it calls the FootagesAndReporter method which returns a List of the object FootageAndReporter
     * Then the program will run a for loop for each FootageAndReporter where it takes the Reporter and Footage object out and gets the
     * values from them
     * It then uses a PrepareStatement to create a Tuple in each table using the parameters from the Reporter and Footage objects
     */


    public static void main(String[] args) throws FileNotFoundException {

        Repository repository = RepositoryAccess.getRepository();


        FootagesAndReportersLoader loader = new FootagesAndReportersLoader();
        if (repository != null) {

            try {
                Connection connection = Connector.getConnection();



                String createjournalistsql = "CREATE TABLE IF NOT EXISTS " +
                        " (\n" +
                        "                                                 CPRNumber numeric(10,0),   \n" +
                        "                                                 FirstName varchar(35) ,\n" +
                        "                                                 LastName varchar(70),\n" +
                        "                                                 Streetname varchar(35),\n" +
                        "                                                 Civicnr numeric(8,0),\n" +
                        "                                                 Zipcode numeric(4,0),\n" +
                        "                                                 Country varchar(35),\n" +
                        "                                              primary key (CPRNumber) );";


                PreparedStatement createjournalist = connection.prepareStatement(createjournalistsql);

                String createfootagessql = "CREATE TABLE IF NOT EXISTS Footage(\n" +
                        "\n" +
                        "                           Footagetitle Varchar(70),\n" +
                        "                        DateOfShooting timestamp,\n" +
                        "\n" +
                        "                       Duration numeric(4,0),\n" +
                        "                            primary key (Footagetitle)\n" +
                        "\n" +
                        "                        );;";


                PreparedStatement createfootage = connection.prepareStatement(createfootagessql);
                createfootage.executeUpdate();
                createjournalist.executeUpdate();


                List<FootageAndReporter> footagesAndReporters = loader.loadFootagesAndReporters(args[0]);

                for (FootageAndReporter footageAndReporter : footagesAndReporters) {
                    Reporter journalist = footageAndReporter.getReporter();
                    Integer cprNr = journalist.getCPR();
                    String firstname = journalist.getFirstName();
                    String lastname = journalist.getLastName();
                    String streetname = journalist.getStreetName();
                    Integer civicnr = journalist.getCivicNumber();
                    Integer zipcode = journalist.getZIPCode();
                    String country = journalist.getCountry();

                    Footage footage = footageAndReporter.getFootage();
                    java.util.Date dateUtil = footage.getDate();
                    java.sql.Date dateOnfootage = new java.sql.Date(dateUtil.getTime());
                    String footageTitle = footage.getTitle();
                    int footageDuration = footage.getDuration();
                /*
                    String sqlFootage = "insert into Footage (DateOfShooting, Footagetitle, Duration) VALUE (?,?,?);";
                    PreparedStatement insertFootage = connection.prepareStatement(sqlFootage);
                    insertFootage.setDate(1, dateOnfootage);
                    insertFootage.setString(2, footageTitle);
                    insertFootage.setInt(3, footageDuration);
                    insertFootage.executeUpdate();

                 */

                    String sqlJournalist = "insert into Journalist (CPRNumber,FirstName,LastName,Streetname,Civicnr,Zipcode,Country) " +
                            "SELECT ?,?,?,?,?,?,?,? FROM dual where not EXISTS(SELECT CPRNumber FROM Journalist WHERE CPRNumber = ?)";
                    PreparedStatement insertJournalist = connection.prepareStatement(sqlJournalist);
                    insertJournalist.setInt(1, cprNr);
                    insertJournalist.setString(2, firstname);
                    insertJournalist.setString(3, lastname);
                    insertJournalist.setString(4, streetname);
                    insertJournalist.setInt(5, civicnr);
                    insertJournalist.setInt(6, zipcode);
                    insertJournalist.setString(7, country);
                    insertJournalist.setInt(8,journalist.getCPR());
                    insertJournalist.executeUpdate();




                }
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                String[] types = {"TABLE"};
                ResultSet resultSetTables = databaseMetaData.getTables(null, null, null, types);
                while (resultSetTables.next()) {
                    String tableName = resultSetTables.getString(3);
                    System.out.println("\n=== TABLE" + tableName);
                    ResultSet resultSetColumns = databaseMetaData.getColumns(null, null, tableName, null);
                    ResultSet resultSetPrimaryKeys = databaseMetaData.getPrimaryKeys(null, null, tableName);
                    while (resultSetColumns.next()) {
                        String columnName = resultSetColumns.getString("COLUMN_NAME");
                        String columnType = resultSetColumns.getString("TYPE_NAME");
                        int columnSize = resultSetColumns.getInt("COLUMN_SIZE");
                        System.out.println("\t" + columnName + " - " + columnType + "(" + columnSize + ")");
                    }
                    while (resultSetPrimaryKeys.next()) {
                        String primayKeyColumn = resultSetPrimaryKeys.getString("COLUMN_NAME");
                        System.out.println("\tPrimary Key Column: " + primayKeyColumn);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e){
                System.out.println("File not found");
            }


        }
    }
}







