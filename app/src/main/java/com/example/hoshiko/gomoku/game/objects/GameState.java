package com.example.hoshiko.gomoku.game.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameState {

    private int size;
    private int[][] board;
    private Stack<Move> moves;
    private int currentIndex = 1;

    /**
     * Khởi tạo game state
     * @param size  = Board size
     */
    public GameState(int size) {
        this.size = size;
        this.board = new int[size][size];
        this.moves = new Stack<>();
    }

    /**
     * Trả về trạng thái cuối cùng của Game
     * @return 0 nếu chưa kết thúc ,
     * chỉ số của mỗi người chơi nếu họ thắng,
     * trả về 3 nếu hết ô để có thể vẽ
     */
    public int terminal() {
        if(isWinner(1)) return 1;
        if(isWinner(2)) return 2;
        if(moves.size() == size * size) return 3;
        return 0;
    }

    /**
     * Trả về chỉ số của người chơi hiện tại đang ở trạng thái này
     */
    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex;  }
    /**
     * Get an ordered list of moves that were made on this state.
     */
    public List<Move> getMoves() {
        return new ArrayList(moves);
    }

    /**
     * Trả về move cuối cùng cho trạng thái này
     * @return Move trước vừa mới làm
     */
    public Move getLastMove() {
        return !moves.isEmpty() ? moves.peek() : null;
    }


    /**
     * Make a move on this state.
     * @param move Move to make
     */
    public void makeMove(Move move) {
        this.moves.push(move);
        this.board[move.row][move.col] = currentIndex;
        this.currentIndex = currentIndex == 1 ? 2 : 1;
    }

    public boolean isWinner(int playerIndex) {
        if(moves.size() < 5) return false;
        Move lastMove = getLastMove();
        int row = lastMove.row;
        int col = lastMove.col;
        if(board[row][col] == playerIndex) {
            // Diagonal from the bottom left to the top right
            if(countConsecutiveStones(row, col, 1, -1) +
                    countConsecutiveStones(row, col, -1, 1) == 4) {
                return true;
            }
            // Diagonal from the top left to the bottom right
            if(countConsecutiveStones(row, col, -1, -1) +
                    countConsecutiveStones(row, col, 1, 1) == 4) {
                return true;
            }
            // Horizontal
            if(countConsecutiveStones(row, col, 0, 1) +
                    countConsecutiveStones(row, col, 0, -1) == 4) {
                return true;
            }
            // Vertical
            if(countConsecutiveStones(row, col, 1, 0) +
                    countConsecutiveStones(row, col, -1, 0) == 4) {
                return true;
            }
        }
        return false;

    }

    /**
     * Kiểm tra xem có cò trong phạm vi của cái board không ?
     */
    public boolean inBounds(int index) {
        return index >= 0 && index < size;
    }


    /*Đếm các viên đá trong trường hợp chúng liên tục với nhau */

    private int countConsecutiveStones(int row, int col, int rowIncrement, int colIncrement) {

        int count = 0;
        int index = board[row][col];
        for(int i = 1; i <= 4; i++) {
            if(inBounds(row + (rowIncrement*i)) && inBounds(col +
                    (colIncrement*i))) {
                if(board[row + (rowIncrement*i)][col + (colIncrement*i)] == index) {
                    count++;
                } else {
                    break;
                }
            }
        }
        return count;
    }
}

