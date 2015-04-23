package com.armanbilge.chesspairs;

import javafx.beans.value.ObservableValueBase;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.IOException;

/**
 * @author Arman Bilge
 */
public class TournamentViewer extends BorderPane {

    private final TableView<Player> players = new TableView<>();
    private final TableView<Game> games = new TableView<>();
    private final FileChooser chooser = new FileChooser();

    private Tournament tournament;
    private File tournamentFile = null;
    private boolean saved = false;

    {
        chooser.setSelectedExtensionFilter(new ExtensionFilter("Chess Tournament", "cht"));

        setTournament(new Tournament());

        final MenuBar menuBar = new MenuBar();
        setTop(menuBar);
        menuBar.setUseSystemMenuBar(true);

        final Menu fileMenu = new Menu("File");
        menuBar.getMenus().addAll(fileMenu);

        final MenuItem newTournament = new MenuItem("New Tournament");
        newTournament.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.META_DOWN));
        newTournament.setOnAction(event -> {
            if (!saveOpportunity())
                setTournament(new Tournament());
        });
        final MenuItem open = new MenuItem("Open...");
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN));
        open.setOnAction(event -> open());
        final MenuItem save = new MenuItem("Save...");
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN));
        save.setOnAction(event -> save(false));
        final MenuItem saveAs = new MenuItem("Save As...");
        saveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN, KeyCombination.SHIFT_DOWN));
        saveAs.setOnAction(event -> save(true));
        fileMenu.getItems().addAll(open, save, saveAs);

        final Menu tournamentMenu = new Menu("Tournament");
        menuBar.getMenus().addAll(tournamentMenu);
        final MenuItem addGame = new MenuItem("Add game...");
        addGame.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.META_DOWN));
        addGame.setOnAction(event -> {

        });

        menuBar.setVisible(true);

        final Button addButton = new Button("+");
        addButton.setOnAction(ae -> {
            final TextInputDialog playerDialog = new TextInputDialog();
            playerDialog.setTitle("New Player");
            playerDialog.setHeaderText(null);
            playerDialog.setContentText("Please enter the player name:");
            playerDialog.showAndWait().ifPresent(name -> tournament.addPlayer(new Player(name)));
        });

        final Button removeButton = new Button("-");
        removeButton.setDisable(true);
        players.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> removeButton.setDisable(nv == null));
        removeButton.setOnAction(ae -> tournament.removePlayer(players.getSelectionModel().getSelectedItem()));

        final TableColumn<Player,String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cdf -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return cdf.getValue().getName();
            }
        });
        final TableColumn<Player,String> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(cdf -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return Double.toString(cdf.getValue().getScore());
            }
        });
        players.getColumns().addAll(nameColumn, scoreColumn);

        final HBox addRemove = new HBox(addButton, removeButton);
        final VBox left = new VBox(players, addRemove);
        setLeft(left);

        players.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {if (nv != null) games.setItems(nv.getGames());});

        final TableColumn<Game,String> opponentColumn = new TableColumn<>("Opponent");
        opponentColumn.setCellValueFactory(cdf -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return cdf.getValue().getOpponent(players.getFocusModel().getFocusedItem()).toString();
            }
        });

        final TableColumn<Game,String> colorColumn = new TableColumn<>("Color");
        colorColumn.setCellValueFactory(cdf -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return cdf.getValue().getColor(players.getFocusModel().getFocusedItem()).toString().toLowerCase();
            }
        });

        final TableColumn<Game,String> outcomeColumn = new TableColumn<>("Outcome");
        outcomeColumn.setCellValueFactory(cdf -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return cdf.getValue().getOutcome(players.getFocusModel().getFocusedItem());
            }
        });

        final TableColumn<Game,CheckBox> countedColumn = new TableColumn<>("Counted");
        countedColumn.setCellValueFactory(cdf -> new ObservableValueBase<CheckBox>() {
            @Override
            public CheckBox getValue() {
                final CheckBox cb = new CheckBox();
                cb.setSelected(cdf.getValue().counted(players.getFocusModel().getFocusedItem()));
                cb.setDisable(true);
                return cb;
            }
        });

        games.getColumns().addAll(opponentColumn, colorColumn, outcomeColumn, countedColumn);

        setCenter(games);
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(final Tournament tournament) {
        this.tournament = tournament;
        tournament.getPlayers().addListener((ListChangeListener<? super Player>) c -> saved = false);
        tournament.getGames().addListener((ListChangeListener<? super Game>) c -> saved = false);
        players.setItems(tournament.getPlayers());
    }

    public boolean saveOpportunity() {
        if (!saved) {
            final Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setHeaderText("Save Tournament");
            alert.setHeaderText("Do you want to save the changes you made to the tournament?");
            alert.setContentText("Your changes will be lost if you don't save them.");
            final ButtonType dont = new ButtonType("Don't Save", ButtonData.NO);
            final ButtonType save = new ButtonType("Save", ButtonData.YES);
            final ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(dont, save, cancel);
            switch (alert.showAndWait().get().getButtonData()) {
                case NO:
                    return false;
                case YES:
                    save(false);
                    return true;
                case CANCEL_CLOSE:
                    return true;
            }
        }
        return false;
    }

    private void save(final boolean showDialog) {
        if (showDialog || tournamentFile == null) {
            final File file = chooser.showSaveDialog(getScene().getWindow());
            if (file != null)
                tournamentFile = file;
            else
                return;
        }
        try {
            getTournament().write(tournamentFile);
            saved = true;
        } catch (final IOException ex) {
            ChessPairs.handleException(ex);
        }
    }

    private void open() {
        final File file = chooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            tournamentFile = file;
            try {
                setTournament(Tournament.read(tournamentFile));
                saved = true;
            } catch (final IOException|ClassNotFoundException ex) {
                ChessPairs.handleException(ex);
            }
        }
    }

}
