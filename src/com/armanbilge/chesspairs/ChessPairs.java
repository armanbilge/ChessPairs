package com.armanbilge.chesspairs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * @author Arman Bilge
 */
public class ChessPairs extends Application {

    final MenuBar menuBar;

    {
        menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        final Menu fileMenu = new Menu("File");
        fileMenu.getItems().add(new MenuItem("Exit"));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chess Pairs");
        final TournamentViewer viewer = new TournamentViewer();
        primaryStage.setScene(new Scene(viewer));
        primaryStage.show();
    }

    public static void main(String... args) {
        launch(args);
    }

}
