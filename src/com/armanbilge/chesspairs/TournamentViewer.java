package com.armanbilge.chesspairs;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Arman Bilge
 */
public class TournamentViewer extends BorderPane {

    private static final ExtensionFilter cht = new ExtensionFilter("Chess Tournament", "*.cht");

    private final TableView<Player> players = new TableView<>();
    private final TableView<GameView> games = new TableView<>();
    private final FileChooser chooser = new FileChooser();

    private Tournament tournament;
    private File tournamentFile = null;
    private boolean saved = true;

//    private final UndoManager undoManager;
//    private final

    private abstract class UndoablePlayer implements Undoable {
        private final Player player;
        public UndoablePlayer(final Player player) {
            this.player = player;
        }
        public Player getPlayer() {
            return player;
        }
    }

    private final class UndoableAddPlayer extends UndoablePlayer {
        public UndoableAddPlayer(Player player) {
            super(player);
        }
        @Override
        public void apply() {
            tournament.addPlayer(getPlayer());
        }
        @Override
        public void undo() {
            tournament.removePlayer(getPlayer());
        }
    }

    private final class UndoableRemovePlayer extends UndoablePlayer {
        public UndoableRemovePlayer(Player player) {
            super(player);
        }
        @Override
        public void apply() {
            tournament.removePlayer(getPlayer());
        }
        @Override
        public void undo() {
            tournament.addPlayer(getPlayer());
        }
    }

    public final class UndoableAddGame implements Undoable {
        private final Game game;
        public UndoableAddGame(final Game game) {
            this.game = game;
        }
        @Override
        public void apply() {
            tournament.addGame(game);
        }
        @Override
        public void undo() {
            tournament.removeGame(game);
        }
    }

    {
        chooser.getExtensionFilters().add(cht);
        chooser.setSelectedExtensionFilter(cht);

//        );
//        undoManager = UndoManagerFactory.unlimitedHistoryUndoManager(events, Undoable::apply, Undoable::undo)

        setTournament(new Tournament());

        final MenuBar menuBar = new MenuBar();
        setTop(menuBar);
        menuBar.setUseSystemMenuBar(true);

        final Menu fileMenu = new Menu("File");
        menuBar.getMenus().addAll(fileMenu);

        final MenuItem newTournament = new MenuItem("New Tournament");
        newTournament.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.META_DOWN));
        newTournament.setOnAction(event -> {
            if (!saveOpportunity()) {
                setTournament(new Tournament());
                tournamentFile = null;
                saved = false;
            }
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
        fileMenu.getItems().addAll(newTournament, open, save, saveAs);

        final Menu tournamentMenu = new Menu("Tournament");
        menuBar.getMenus().addAll(tournamentMenu);
        final MenuItem addGame = new MenuItem("Add Game...");
        addGame.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.META_DOWN));
        addGame.setOnAction(event -> {
            final Dialog<Game> dialog = new Dialog<>();
            dialog.setTitle("Add Game");
            final ButtonType addType = new ButtonType("Add", ButtonData.APPLY);
            dialog.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);
            final Node addButton = dialog.getDialogPane().lookupButton(addType);
            addButton.setDisable(true);
            final GridPane pane = new GridPane();
            pane.setHgap(10);
            pane.setVgap(10);
            dialog.getDialogPane().setContent(pane);
            pane.add(new Label("White"), 0, 0);
            pane.add(new Label("Outcome"), 1, 0);
            pane.add(new Label("Black"), 2, 0);
            final ComboBox<Player> white = new ComboBox<>(getTournament().getPlayers());
            pane.add(white, 0, 1);
            final ComboBox<Outcome> outcome = new ComboBox<>(new ObservableListWrapper<>(Arrays.asList(Outcome.values())));
            pane.add(outcome, 1, 1);
            final ComboBox<Player> black = new ComboBox<>(getTournament().getPlayers());
            pane.add(black, 2, 1);
            final CheckBox whiteNoCount = new CheckBox("No Count");
            pane.add(whiteNoCount, 0, 2);
            final CheckBox blackNoCount = new CheckBox("No Count");
            pane.add(blackNoCount, 2, 2);
            final ChangeListener<Object> listener = (o, ov, nv) -> {
                addButton.setDisable(white.getValue() == black.getValue() || white.getValue() == null || black.getValue() == null || outcome.getValue() == null);
                if (Unpaired.INSTANCE.equals(white.getValue()) || Unpaired.INSTANCE.equals(black.getValue())) {
                    whiteNoCount.setSelected(true);
                    whiteNoCount.setDisable(true);
                    blackNoCount.setSelected(true);
                    blackNoCount.setDisable(true);
                    outcome.setValue(null);
                    outcome.setDisable(true);
                    addButton.setDisable(white.getValue() == black.getValue() || white.getValue() == null || black.getValue() == null);
                } else {
                    whiteNoCount.setDisable(false);
                    blackNoCount.setDisable(false);
                    outcome.setDisable(false);
                }
            };
            white.getSelectionModel().selectedItemProperty().addListener(listener);
            black.getSelectionModel().selectedItemProperty().addListener(listener);
            outcome.getSelectionModel().selectedItemProperty().addListener(listener);
            dialog.setResultConverter(cb -> {
                if (!cb.getButtonData().isCancelButton())
                    return new Game(white.getValue(), whiteNoCount.isSelected(),
                            black.getValue(), blackNoCount.isSelected(), outcome.getValue());
                else
                    return null;
            });
            dialog.showAndWait().ifPresent(getTournament()::addGame);
        });
        final MenuItem generatePairing = new MenuItem("Generate Pairing");
        generatePairing.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.META_DOWN));
        generatePairing.setOnAction(a -> {
            if (tournament.getPlayers().size() % 2 == 1)
                tournament.addPlayer(Unpaired.INSTANCE);
            final PairingViewer viewer = new PairingViewer();
            viewer.setTournament(tournament);
            final Stage stage = new Stage();
            stage.setTitle("Pairing Generator");
            stage.setScene(new Scene(viewer));
            stage.show();
        });
        tournamentMenu.getItems().addAll(addGame, generatePairing);

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
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        final TableColumn<Player,Double> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        players.getColumns().addAll(nameColumn, scoreColumn);

        final HBox addRemove = new HBox(addButton, removeButton);
        final VBox left = new VBox(players, addRemove);
        setLeft(left);

        final TableColumn<GameView,Player> opponentColumn = new TableColumn<>("Opponent");
        opponentColumn.setCellValueFactory(new PropertyValueFactory<>("opponent"));
        final TableColumn<GameView,String> outcomeColumn = new TableColumn<>("Outcome");
        outcomeColumn.setCellValueFactory(new PropertyValueFactory<>("outcome"));
        final TableColumn<GameView,String> colorColumn = new TableColumn<>("Color");
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        final TableColumn<GameView,CheckBox> countedColumn = new TableColumn<>("Counted");
        countedColumn.setCellValueFactory(new PropertyValueFactory<>("counted"));
        games.getColumns().addAll(opponentColumn, colorColumn, outcomeColumn, countedColumn);
        setCenter(games);

        players.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                final ObservableList<GameView> view = new ObservableListWrapper<>(nv.getGames().stream().map(g -> new GameView(g, nv)).collect(Collectors.toList()));
                nv.getGames().addListener((ListChangeListener<? super Game>) c -> {
                    view.clear();
                    view.addAll(nv.getGames().stream().map(g -> new GameView(g, nv)).collect(Collectors.toList()));
                });
                games.setItems(view);
             }
        });

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
            alert.setTitle("Save Tournament");
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
        if (!saveOpportunity()) {
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

}
