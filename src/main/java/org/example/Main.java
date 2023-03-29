package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Main {


    public static void main(String[] args) throws FileNotFoundException {

        Repository repository = RepositoryAccess.getRepository();


        FootagesAndReportersLoader loader = new FootagesAndReportersLoader();
        if(repository != null) {

            try {
                Connection connection = Connector.getConnection();
                PreparedStatement createjournalist = connection.prepareStatement("" +
                        "CREATE TABLE IF NOT EXISTS Journalist (\n" +
                        "\n" +
                        "    CPRNumber numeric(10,0),\n" +
                        "    FirstName varchar(35),\n" +
                        "    LastName varchar(70),\n" +
                        "   Streetname varchar(35),\n"+
                        "    Civicnr numeric(8,0),\n" +
                        "    Zipcode numeric(4,0),\n" +
                        " Country varchar(35),\n"+
                        "    primary key (CPRNumber)\n" +
                        "\n" +
                        ");;");
                PreparedStatement createfootage = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Footage(\n" +
                        "\n" +
                                "   Footagetitle Varchar(70),\n" +
                        "      DateOfShooting timestamp,\n\n" +
                        "    Duration time,\n" +
                        "    primary key (Footagetitle)\n" +
                        "\n" +
                        ");;");
                createfootage.executeUpdate();
                createjournalist.executeUpdate();
                List<FootageAndReporter> farList = new ArrayList<FootageAndReporter>();

                BufferedReader in = null;



                List<FootageAndReporter> footagesAndReporters = loader.loadFootagesAndReporters(args[0]);

                for (FootageAndReporter footageAndReporter : footagesAndReporters) {
                  Reporter journalist =   footageAndReporter.getReporter();
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
                  Integer footageDuration = footage.getDuration();

                  PreparedStatement insertFootage = connection.prepareStatement("insert into 'Footage'(DateOfShooting, Footagetitle, Duration) VALUE (?,?,?)");
                  insertFootage.setDate(1,  dateOnfootage );
                  insertFootage.setString(2,footageTitle);
                  insertFootage.setInt(3,footageDuration);
                  insertFootage.executeUpdate();

                 // PreparedStatement insertJournalist = connection.prepareStatement("insert into 'Journalist'(C)");












                }


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }catch (IOException e1 ){
              throw new FileNotFoundException("File not found");
            }


        }




        }
    }
