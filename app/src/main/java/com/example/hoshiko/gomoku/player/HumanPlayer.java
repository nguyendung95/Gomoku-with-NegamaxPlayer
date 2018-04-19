package com.example.hoshiko.gomoku.player;

import com.example.hoshiko.gomoku.game.objects.GameInfo;
import com.example.hoshiko.gomoku.game.objects.GameState;
import com.example.hoshiko.gomoku.game.objects.Move;

public class HumanPlayer extends Player {


        private Move move;

        public HumanPlayer(GameInfo info) {
            super(info);
        }

        public void setMove(Move move) {
            this.move = move;
        }

        @Override
        public Move getMove(GameState state) {
            // Đứng chờ cho đến khi player HUMAN nhập vào Move hợp lý
            try {
                synchronized(this) {
                    this.wait();
                }
            } catch(InterruptedException e) {
                return null;
            }
            return move;
        }


}
