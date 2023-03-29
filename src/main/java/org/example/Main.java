package org.example;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;



public class Main {


    public static void main(String[] args) {

        Repository repository = RepositoryAccess.getRepository();

        FootagesAndReportersLoader loader = new FootagesAndReportersLoader();
        if(repository != null){
            System.out.println("kage");
        }

        }




    }
