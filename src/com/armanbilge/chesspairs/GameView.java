package com.armanbilge.chesspairs;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

/**
 * @author Arman Bilge
 */
public class GameView {

    final ObjectProperty<Player> opponent;
    final StringProperty outcome;
    final StringProperty color;
    final ObjectProperty<CheckBox> counted;

    public GameView(final Game game, final Player player) {
        opponent = new SimpleObjectProperty<>(game.getOpponent(player));
        outcome = new SimpleStringProperty(game.getOutcome(player));
        final Color color = game.getNullableColor(player);
        this.color = new SimpleStringProperty(color != null ? color.toString().toLowerCase() : "NA");
        final CheckBox cb = new CheckBox();
        cb.setDisable(true);
        cb.setSelected(game.counted(player));
        counted = new SimpleObjectProperty<>(cb);
    }

    public ObjectProperty<Player> opponentProperty() {
        return opponent;
    }

    public StringProperty outcomeProperty() {
        return outcome;
    }

    public StringProperty colorProperty() {
        return color;
    }

    public ObjectProperty<CheckBox> countedProperty() {
        return counted;
    }

}
