package com.armanbilge.chesspairs;

/**
 * @author Arman Bilge
 */
public interface Undoable {
    void apply();
    void undo();
}
