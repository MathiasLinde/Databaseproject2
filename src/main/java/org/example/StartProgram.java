package org.example;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.List;


public class StartProgram {
    /**
     * This is the Starting point of the program.
     * It starts out with Creating a repository which also contains a connector object
     * Then a connection is made using the Connector.getConnection method
     * After that it Uses two PrepareStatement to create the tables Journalist and Footage
     * After that it calls the FootagesAndReporter method which returns a List of the object FootageAndReporter
     * Then the program will run a for loop for each FootageAndReporter where it takes the Reporter and Footage object out and gets
     * the different values using the get Method to extract from the objects
     * It then uses a PrepareStatement to create a Tuple in each table using the parameters from the Reporter and Footage objects
     *
     * Note that since the LoaderClass used to load the files uses the String[] args
     * It is important to insert the following args
     * src/main/resources/uploads.csv
     * into the run configuration settings
     */


    public static void main(String[] args) throws FileNotFoundException {

        Repository repository = RepositoryAccess.getRepository();


        FootagesAndReportersLoader loader = new FootagesAndReportersLoader();
        if (repository != null) {

            try {
                Connection connection = Connector.getConnection();



                String createjournalistsql = "CREATE TABLE IF NOT EXISTS Journalist(" +
                        " \n" +
                        "                                                 CPRNumber numeric(10,0),   \n" +
                        "                                                 FirstName varchar(35) ,\n" +
                        "                                                 LastName varchar(70),\n" +
                        "                                                 Streetname varchar(35),\n" +
                        "                                                 Civicnr numeric(8,0),\n" +
                        "                                                 Zipcode numeric(4,0),\n" +
                        "                                                 Country varchar(35),\n" +
                        "                                              primary key (CPRNumber) );";


                PreparedStatement createjournalist = connection.prepareStatement(createjournalistsql);

                String createfootagessql = " CREATE TABLE IF NOT EXISTS Footage (\n" +
                        "                                                  Footagetitle Varchar(70),\n" +
                        "                                                DateOfShooting timestamp,\n" +
                        "\n" +
                        "                                               Duration numeric(4,0),\n" +
                        "                                                    primary key (Footagetitle)\n" +
                        "\n" +
                        "                                                );" ;


                PreparedStatement createfootage = connection.prepareStatement(createfootagessql);
                createfootage.executeUpdate();
                createjournalist.executeUpdate();


                List<FootageAndReporter> footagesAndReporters = loader.loadFootagesAndReporters(args[0]);

                for (FootageAndReporter footageAndReporter : footagesAndReporters) {


                    Footage footage = footageAndReporter.getFootage();
                    Date dateUtil = footage.getDate();
                    java.sql.Date dateOnfootage = new java.sql.Date(dateUtil.getTime());
                    String footageTitle = footage.getTitle();
                    int footageDuration = footage.getDuration();
                    String sqlFootage = "insert into Footage (DateOfShooting, Footagetitle, Duration) " +
                            "SELECT ?,?,? from dual Where not exists(SELECT Footagetitle from Footage where Footagetitle = ? );";
                    PreparedStatement insertFootage = connection.prepareStatement(sqlFootage);
                    insertFootage.setDate(1, dateOnfootage);
                    insertFootage.setString(2, footageTitle);
                    insertFootage.setInt(3, footageDuration);
                    insertFootage.setString(4,footage.getTitle());
                    insertFootage.executeUpdate();


                    Reporter journalist = footageAndReporter.getReporter();
                    Integer cprNr = journalist.getCPR();
                    String firstname = journalist.getFirstName();
                    String lastname = journalist.getLastName();
                    String streetname = journalist.getStreetName();
                    Integer civicnr = journalist.getCivicNumber();
                    Integer zipcode = journalist.getZIPCode();
                    String country = journalist.getCountry();
                    String sqlJournalist = "insert into Journalist (CPRNumber,FirstName,LastName,Streetname,Civicnr,Zipcode,Country) " +
                            "SELECT ?,?,?,?,?,?,? FROM dual where not EXISTS(SELECT CPRNumber FROM Journalist WHERE CPRNumber = ?)";
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

                String sqlQueryJournalist = "SELECT * from journalist";

                String sqlQueryFootage = "Select * from Footage";


                Statement statementJournalist = connection.createStatement();
                ResultSet resultSet1 = statementJournalist.executeQuery(sqlQueryJournalist);
                ResultSetMetaData resultSetMetaData1 = resultSet1.getMetaData();
                int columnCount1 = resultSetMetaData1.getColumnCount();
                for (int i = 1; i <= columnCount1; i++) {
                    System.out.print(resultSetMetaData1.getColumnName(i) + ";");
                }
                System.out.println();
                System.out.println("------");

                resultSet1.beforeFirst();
                while (resultSet1.next()){
                    for (int i = 1; i <= columnCount1 ; i++) {
                        if (resultSet1.getString(i) == null){
                            System.out.print("null; ");
                        } else {
                            System.out.print(resultSet1.getString(i) + "; ");
                        };
                    }
                    System.out.println();
                }

                System.out.println();
                System.out.println();

                Statement statementFootage = connection.createStatement();
                ResultSet resultSet2 = statementFootage.executeQuery(sqlQueryFootage);
                ResultSetMetaData resultSetMetaData = resultSet2.getMetaData();
                int columnCount2 = resultSetMetaData.getColumnCount();
                for (int i = 1; i <= columnCount2; i++) {
                    System.out.print(resultSetMetaData.getColumnName(i) + ";");
                }
                System.out.println();
                System.out.println("------");

                resultSet2.beforeFirst();
                while (resultSet2.next()){
                    for (int i = 1; i <= columnCount2 ; i++) {
                        if (resultSet2.getString(i) == null){
                            System.out.print("null; ");
                        } else {
                            System.out.print(resultSet2.getString(i) + "; ");
                        };
                    }
                    System.out.println();
                }
                connection.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e){
                throw new FileNotFoundException();
            }


        }
    }

}







