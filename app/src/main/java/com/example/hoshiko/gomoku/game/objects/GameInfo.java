package com.example.hoshiko.gomoku.game.objects;

public class GameInfo {
    private final GameSettings settings;
    private int playerIndex;
    private int opponentIndex;

    /**
     * Thông tin của ván Game cho  người chơi biết :v
     * @param playerIndex Chỉ số của người chơi  (1/2)
     * @param opponentIndex Chỉ số của dối thủ  (1/2)
     */
    protected GameInfo(GameSettings settings, int playerIndex, int
            opponentIndex) {
        this.settings = settings;
        this.playerIndex = playerIndex;
        this.opponentIndex = opponentIndex;
    }


    /**
     * Lấy thông tin size board
     * Để tính số giao điểm (size * size) có trên bàn cờ khi đưa cho Bot chơi
     */
    public int getSize() {
        return settings.getSize();
    }

}
