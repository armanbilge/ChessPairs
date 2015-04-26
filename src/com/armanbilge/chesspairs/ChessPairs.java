package com.armanbilge.chesspairs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

/**
 * @author Arman Bilge
 */
public class ChessPairs extends Application {

    public static final Random RANDOM = new Random();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chess Pairs");
        final TournamentViewer viewer = new TournamentViewer();
        primaryStage.setScene(new Scene(viewer));
        primaryStage.setOnCloseRequest(event -> {
            if (viewer.saveOpportunity()) event.consume();
        });
        primaryStage.show();
    }

    public static void main(String... args) {
        launch(args);
    }

    public static void handleException(final Exception ex) {
        final Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("There was an error!");
        alert.setContentText(ex.getMessage());
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        final VBox expandable = new VBox();
        alert.getDialogPane().setExpandableContent(expandable);
        expandable.getChildren().add(new Label("The exception stacktrace was:"));
        final TextArea textArea = new TextArea(sw.toString());
        expandable.getChildren().add(textArea);
        textArea.setEditable(false);
        alert.showAndWait();
    }

}
