package com.example.hoshiko.gomoku.game.objects;

import com.example.hoshiko.gomoku.player.HumanPlayer;
import com.example.hoshiko.gomoku.player.negamax.player.NegamaxPlayer;
import com.example.hoshiko.gomoku.player.Player;

public class GameSettings {

    public enum PlayerType { HUMAN, COMPUTER }

    private PlayerType player1;
    private PlayerType player2;
    private int size;

    // Khởi tạo vói các giá trị mặc định
    public GameSettings() {
        this.player1 = PlayerType.HUMAN;
        this.player2 = PlayerType.COMPUTER;
        this.size = 10;

    }

     // Xác định  PlayerType cho từng người chơi (human/computer),
     // Đưa chỉ số cho từng người chơi

    private Player getPlayer(PlayerType type, int playerIndex) {
        int opponentIndex = playerIndex == 2 ? 1 : 2;
        switch(type) {
            case HUMAN:
                return new HumanPlayer(new GameInfo(this, playerIndex,
                        opponentIndex));
            case COMPUTER:
                return new NegamaxPlayer(new GameInfo(this, playerIndex,
                        opponentIndex));
            default:
                return null;
        }
    }



    // Lấy thông tin dành cho người chơi thứ nhất
    public Player getPlayer1() {
        return getPlayer(player1, 1);
    }


    // Lấy thông tin dành cho người chơi thứ hai
    public Player getPlayer2() {
        return getPlayer(player2, 2);
    }

    // Lấy Size của bàn cờ
    public int getSize() {
        return this.size;
    }

    // Thiết lập size cho bàn cờ
    public void setSize(int size) {
        this.size = size;
    }


    // Lấy tên của người chơi (HUMAN/COMPUTER)
    public int getName(PlayerType playerType){

        switch (playerType){
            case HUMAN:
                return 0;
            case COMPUTER:
                return 1;
             default: return -1;
        }
    }

}

