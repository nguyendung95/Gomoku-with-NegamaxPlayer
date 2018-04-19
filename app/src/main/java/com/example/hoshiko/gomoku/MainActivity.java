package com.example.hoshiko.gomoku;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoshiko.gomoku.game.objects.GameInfo;
import com.example.hoshiko.gomoku.game.objects.GameSettings;
import com.example.hoshiko.gomoku.game.objects.GameState;
import com.example.hoshiko.gomoku.game.objects.Move;
import com.example.hoshiko.gomoku.player.HumanPlayer;
import com.example.hoshiko.gomoku.player.Player;
import com.example.hoshiko.gomoku.player.negamax.player.NegamaxPlayer;
import com.example.hoshiko.gomoku.ui.BoardPane;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btnStart;
    private Button btnStop;
    private TextView txtResult;

    // Hiển thị bàn cờ và các viên đá lên Main Thread
    private BoardPane boardView;

    // Khai báo các đối tượng thuộc package Game Objects
    private GameState state;
    private GameSettings settings;
    private final Player[] players = new Player[2];


    // Chờ requestMove từ người chơi và lưu giữ nước đi này
    private Future<Move> futureMove;

    // Tạo thread và quản lý Thread
    private Thread gameThread;
    private ExecutorService executor;

    // Biến để trao đổi thông tin giữa UI & GameThread
    Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các đối tượng liên quan đến UI & Game Thread
        initialize();
        btnStop.setVisibility(View.GONE);


        // Đặt các Listener trong Button
        imageView.setOnTouchListener(userTouch);
        btnStart.setOnClickListener(startAction);
        btnStop.setOnClickListener(stopAction);
    }


    // Khởi tạo và vẽ bàn cờ mới
    public void setupBoard() {
        boardView = new BoardPane(getBaseContext(), 10, 700, 2);
        imageView.setImageBitmap(boardView.drawBoard(2));

    }

    public void initialize() {

        // Tham chiếu đến các đối tượng ở content_view
        imageView = findViewById(R.id.imgView);
        btnStart = findViewById(R.id.btn_play);
        btnStop = findViewById(R.id.btn_stop);
        txtResult = findViewById(R.id.txt_Result);

        // Tạo mới các đối thượng thuộc Object Game
        this.settings = new GameSettings();
        this.gameThread = new Thread(getRunnable());
        this.executor = Executors.newSingleThreadExecutor();
        this.state = new GameState(settings.getSize());

        // Định nghĩa  đối tượng Handler, đối tượng này nằm trên UI Thread
        // Nhận các thông tin từ Game Thread để vẽ lên lên boardView
        mainHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message inputMessage) {
                super.handleMessage(inputMessage);

                // Nhận thông tin từ Message object
                Move move = (Move) inputMessage.obj;
                String winnerName = null;

                // Vẽ nước đi của quân cờ hiện tại lên boardView
                txtResult.setText("Nước vừa mới đi ở hàng: " + (move.row+1) + " và cột " + (move.col+1) + "");
                boardView.addStone(move.row, move.col, (state.getCurrentIndex() - 1));

                // Update lại view nà :v
                imageView.invalidate();
            }
        };

    }


    private View.OnTouchListener userTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (state.terminal() == 0 && gameThread.isAlive()) {
                int row = boardView.getClosestRow(v, event);
                int col = boardView.getClosestCol(v, event);

                // Kiểm tra xem Move vừa nhập có hợp lệ hay không ?
                if (setUserMove(new Move(row, col))) {
                    return true;
                }
            }
            return true;

        }
    };


    private View.OnClickListener startAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            // Bắt đầu chạy vòng lặp for game
            start();
            btnStart.setVisibility(View.GONE);
            btnStop.setVisibility(View.VISIBLE);
        }
    };


    private View.OnClickListener stopAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            stop();
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.GONE);
        }
    };


    // Bắt đầu ván game. Đọc thông tin ván game từ Settings và xem bắt đầu cho chạy Game Thread
    // Nếu game thread còn đang chạy thì không có gì xảy ra.

    public void start() {
        if (!this.gameThread.isAlive()) {
            // Set up bàn cờ
            setupBoard();
            this.state = new GameState(settings.getSize());
            // Lấy thông tin từ người chơi (là HUMAN hay COMPUTER)
            players[0] = settings.getPlayer1();
            players[1] = settings.getPlayer2();
            ;
            this.gameThread = new Thread(getRunnable());
            this.gameThread.start();
        }
    }


    // Dừng Game. Các thread đang chạy sẽ bị chặn lại,
    // Gọi phương thức join() để chờ thread chạy xong là hủy.
    // FutureMove chưa kịp nhận request từ user cũng hủy

    public void stop() {
        if (this.gameThread.isAlive()) {
            this.gameThread.interrupt();
            try {
                this.gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!futureMove.isDone()) {
                futureMove.cancel(true);
            }
        }
    }


    // Chờ nước đi từ người chơi

    private Move requestMove(int playerIndex) throws
            InterruptedException, ExecutionException {
        Player player = players[playerIndex - 1];
        this.futureMove = executor.submit(() -> player.getMove(state));
        return futureMove.get();
    }


    // Kiểm tra xem nước đi của player có hợp lệ hay không
    // Nếu hợp lệ thì trả về true.
    // Và đánh thức thread đang trong trạng thái đang chờ

    public boolean setUserMove(Move move) {
        Player currentPlayer = players[state.getCurrentIndex() - 1];
        if (currentPlayer instanceof HumanPlayer) {
            if (!state.getMoves().contains(move)) {
                synchronized (currentPlayer) {
                    ((HumanPlayer) currentPlayer).setMove(move);
                    players[state.getCurrentIndex() - 1].notify();
                }
                return true;
            }
        }
        return false;
    }

    // Tạo vòng lặp cho Game nà :v

    private Runnable getRunnable() {
        return () -> {

            while (state.terminal() == 0) {
                try {
                    Log.i("MAIN", "BEGIN GAME LOOP AT STATE TERMINAL = 0 ");

                    // Bước 1: Ngồi chờ người chơi đánh để nhận Move
                    Move move = requestMove(state.getCurrentIndex());

                    // Bước 2: Gửi Move vừa nhận lên lại UI THREAD để vẽ stone
                    // Lấy message từ Main Thread
                    Message msg = mainHandler.obtainMessage();
                    // Gán dữ liệu cho msg Mainthread, lưu vào biến obj
                    msg.obj = move;
                    // Gửi trả lại message cho Mainthread
                    mainHandler.sendMessage(msg);

                    // Bước 3: Cho Game Thread ngủ 1s để UI Thread kịp vẽ các quân cờ
                    Thread.sleep(1000);

                    // Bước 4: Lưu nước đi trong state & thay turn
                    state.makeMove(move);

                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    break;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    break;
                }
            }

            if (state.terminal() != 0) {
                switch (state.terminal()){
                    case 1:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getBaseContext(), "Game over.  Người chơi 1 thắng!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case 2:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getBaseContext(), "Game over. Người chơi 2 thắng!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    case 3:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getBaseContext(), "Game over. Hòa !", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        };

    }

}

