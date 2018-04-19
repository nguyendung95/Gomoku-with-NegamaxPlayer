package com.example.hoshiko.gomoku.game.objects;

import java.util.Objects;

/**
 * Chuyển động trên bàn cờ hoặc vị trí trên bàn cờ
 */
public class Move {

    public final int row;
    public final int col;

    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.row, this.col);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Move) {
            Move move = (Move) obj;
            return move.row == this.row && move.col == this.col;
        }
        return false;
    }
}
