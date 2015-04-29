package com.armanbilge.chesspairs;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * @author Arman Bilge
 */
public class UndoManager {

    private final ObservableList<Undoable> undo = new ObservableListWrapper<>(new ArrayList<>());
    private final ObservableList<Undoable> redo = new ObservableListWrapper<>(new ArrayList<>());

    private final BooleanProperty undoProperty = new SimpleBooleanProperty();
    private final BooleanProperty redoProperty = new SimpleBooleanProperty();

    private final StringProperty undoTextProperty = new SimpleStringProperty("Undo");
    private final StringProperty redoTextProperty = new SimpleStringProperty("Redo");

    {
        undoProperty.bind(new SimpleListProperty<>(undo).emptyProperty());
        redoProperty.bind(new SimpleListProperty<>(redo).emptyProperty());
    }

    public void push(final Undoable undoable) {
        undo.add(undoable);
        setNameProperties();
    }

    public void undo() {
        final Undoable undoable = undo.remove(undo.size() - 1);
        undoable.undo();
        redo.add(undoable);
        setNameProperties();
    }

    public void redo() {
        final Undoable undoable = redo.remove(redo.size() - 1);
        undoable.redo();
        undo.add(undoable);
        setNameProperties();
    }

    private void setNameProperties() {
        undoTextProperty.set("Undo" + (!undo.isEmpty() ? " " + undo.get(undo.size() - 1).toString() : ""));
        redoTextProperty.set("Redo" + (!redo.isEmpty() ? " " + redo.get(redo.size() - 1).toString() : ""));
    }

    public void clear() {
        undo.clear();
        redo.clear();
    }

    public BooleanProperty undoProperty() {
        return undoProperty;
    }

    public StringProperty undoTextProperty() {
        return undoTextProperty;
    }

    public BooleanProperty redoProperty() {
        return redoProperty;
    }

    public StringProperty redoTextProperty() {
        return redoTextProperty;
    }

}
