package com.armanbilge.chesspairs;

import com.itextpdf.text.DocumentException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Optional;

/**
 * @author Arman Bilge
 */
public class PairingViewer extends BorderPane {

    private static final ExtensionFilter pdf = new ExtensionFilter("Portable Document Format (PDF)", "*.pdf");

    private final TableView<Pair> pairing;
    private final FileChooser chooser = new FileChooser();
    private Tournament tournament = null;
    private PairingOptimizer optimizer = null;

    {
        chooser.getExtensionFilters().addAll(pdf);
        chooser.setSelectedExtensionFilter(pdf);

        pairing = new TableView<>();
        final TableColumn<Pair,Player> white = new TableColumn<>("White");
        white.setCellValueFactory(new PropertyValueFactory<>("white"));
        final TableColumn<Pair,Player> black = new TableColumn<>("Black");
        black.setCellValueFactory(new PropertyValueFactory<>("black"));
        pairing.getColumns().addAll(white, black);
        setCenter(pairing);
        final Button regenerate = new Button("Regenerate");
        regenerate.setOnAction(a -> randomize());
        final Button save = new Button("Save as PDF");
        save.setOnAction(a -> Optional.ofNullable(chooser.showSaveDialog(getScene().getWindow())).ifPresent(file -> {
            try {
                new PDFGenerator(tournament, new HashSet<>(getPairing())).write(file);
            } catch (final FileNotFoundException|DocumentException ex) {
                ChessPairs.handleException(ex);
            }
        }));
        setBottom(new HBox(regenerate, save));
    }

    public void setTournament(final Tournament tournament) {
        this.tournament = tournament;
        optimizer = PairingOptimizer.create(tournament);
        randomize();
    }

    public void randomize() {
        pairing.setItems(FXCollections.observableArrayList(optimizer.getRandomPairing()));
    }

    public ObservableList<Pair> getPairing() {
        return pairing.getItems();
    }

}
