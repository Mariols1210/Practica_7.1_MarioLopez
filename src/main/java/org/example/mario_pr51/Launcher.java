package org.example.mario_pr51;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        try {
            Application.launch(HelloApplication.class, args);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
}
