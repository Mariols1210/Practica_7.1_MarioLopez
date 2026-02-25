package org.example.mario_pr51.BD;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionBD {
    public static Connection getConnection() {
        try {
            String url = "jdbc:sqlite:C:\\Users\\Alumno\\Desktop\\Mario_PR51\\Practica5DB.db";
            return DriverManager.getConnection(url);
        } catch (Exception e) {
            System.out.println("Error cargado la base de datos");
            return null;
        }
    }
}
