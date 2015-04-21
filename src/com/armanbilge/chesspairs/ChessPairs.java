package com.armanbilge.chesspairs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Arman Bilge
 */
public class ChessPairs extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chess Pairs");
        primaryStage.setScene(new Scene(new TournamentViewer()));
        primaryStage.show();
    }

    public static void main(String... args) {
        launch(args);
    }

}
