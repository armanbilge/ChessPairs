package com.armanbilge.chesspairs;

import javafx.beans.value.ObservableValueBase;
import javafx.event.EventType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
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

    private final TableView<Player> players;
    private final TableView<Game> games;
    private final FileChooser chooser;

    private Tournament tournament = new Tournament();
    private File tournamentFile = null;

    {
        players = new TableView<>(tournament.getPlayers());
        games = new TableView<>();

        chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(new ExtensionFilter("Chess Tournament", "cht"));

        final MenuBar menuBar = new MenuBar();
        setTop(menuBar);
        menuBar.setUseSystemMenuBar(false);

        final Menu fileMenu = new Menu("File");
        menuBar.getMenus().addAll(fileMenu);

        final MenuItem open = new MenuItem("Open...");
        open.setOnAction(event -> open());
        final MenuItem save = new MenuItem("Save...");
        save.setOnAction(event -> save(false));
        final MenuItem saveAs = new MenuItem("Save As...");
        saveAs.setOnAction(event -> save(true));
        fileMenu.getItems().addAll(open, save, saveAs);

        final Menu tournamentMenu = new Menu("Tournament");
        menuBar.getMenus().addAll(tournamentMenu);

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
        removeButton.setDisable(players.getFocusModel().getFocusedCell().getRow() == -1);
        players.addEventHandler(EventType.ROOT,
                event -> removeButton.setDisable(players.getFocusModel().getFocusedCell().getRow() == -1));
        removeButton.setOnAction(ae -> tournament.removePlayer(players.getFocusModel().getFocusedItem()));

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

        players.getFocusModel().focusedItemProperty().addListener((o, ov, nv) -> {if (nv != null) games.setItems(nv.getGames());});

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
        players.setItems(tournament.getPlayers());
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
        } catch (final IOException ex) {
            new Alert(AlertType.ERROR, ex.getMessage()).show();
        }
    }

    private void open() {
        final File file = chooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            tournamentFile = file;
            try {
                setTournament(Tournament.read(tournamentFile));
            } catch (final IOException|ClassNotFoundException ex) {
                new Alert(AlertType.ERROR, ex.getMessage()).show();
            }
        }
    }

}
