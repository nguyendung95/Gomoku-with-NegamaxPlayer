package com.example.hoshiko.gomoku.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.example.hoshiko.gomoku.R;
import com.example.hoshiko.gomoku.game.objects.GameState;

import java.util.ArrayList;
import java.util.List;

public class BoardPane {

    // Cạnh của hình vuông
    private int sideLength;
    private int size;
    private int cellSize;
    // Lưu lại nước đi của White Stone & Black Stone
    private int[][] board;
    private Context context;
    private List<Line> listLine;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    private Bitmap bmWhite;
    private Bitmap bmBlack;

    private GameState state;


    /**
     * Tạo mới bàn cờ Gomoku với số lượng size đã được chỉ định sẵn.
     */
    public BoardPane(Context context, int size, int sideLength, int strokeWidth) {

        this.context = context;
        this.sideLength = sideLength;       // Cạnh của hình vuông (chiều dài = chiều rộng)
        this.size = size;                   // Kích cỡ bàn cờ (vd: 8x8, 9x9 ...)
        this.cellSize = sideLength / size;   // Khoảng cách giữa mỗi hàng/cột

        this.board = new int[size][size];

        this.bitmap = Bitmap.createBitmap(sideLength, sideLength, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(bitmap);
        this.paint = new Paint();

        state = new GameState(size);


        //gan gia tri (-1)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = -1;
            }
        }

        bmWhite = BitmapFactory.decodeResource(context.getResources(), R.drawable.white_stone);
        bmBlack = BitmapFactory.decodeResource(context.getResources(), R.drawable.black_stone);


        //Khởi tạo bàn cờ
        setGrid(size);
        drawBoard(strokeWidth);
    }

    public void setGrid(int size) {

        //Khởi tạo List line chứa các line tạo nên board
        listLine = new ArrayList<Line>();

        // Khởi tạo các Line thuộc hàng
        for (int i = 0; i <= size; i++) {
            listLine.add(new Line(0, i * cellSize, sideLength, i * cellSize));
        }

        // Khởi tạo các Line thuộc cột
        for (int i = 0; i <= size; i++) {
            listLine.add(new Line(i * cellSize, 0, i * cellSize, sideLength));
        }
    }


    public Bitmap drawBoard(float strokeWidth) {
        paint.setStrokeWidth(strokeWidth);
        for (Line line : listLine) {
            canvas.drawLine(line.getXStart(), line.getYStart(), line.getXEnd(), line.getYEnd(), paint);
        }
        return bitmap;

    }


    /**
     * Vẽ quân cờ trắng/đen lên bàn cờ
     *
     * @param rowIndex Vị trí hàng của quân cờ
     * @param colIndex Vị trí cột của quân cờ
     * @param index    Chỉ số của quân cờ (quân Đen là 1, quân Trắng là 2)
     */
    public void paintStone(int rowIndex, int colIndex, int index) {
        int padding = 5;

        int x = (int) (colIndex * cellSize + padding);
        int y = (int) (rowIndex * cellSize + padding);

        switch (index) {
            case 0:
                canvas.drawBitmap(bmBlack, new Rect(0, 0, bmBlack.getWidth(), bmBlack.getHeight()),
                        new Rect(x, y, (colIndex + 1) * cellSize - padding,
                                (rowIndex + 1) * cellSize - padding), paint);

                break;

            case 1:
                canvas.drawBitmap(bmWhite, new Rect(0, 0, bmWhite.getWidth(), bmWhite.getHeight()),
                        new Rect(x, y, (colIndex + 1) * cellSize - padding,
                                (rowIndex + 1) * cellSize - padding), paint);

                break;
        }
    }

    // Thêm các viên đá lên bàn cờ theo chỉ số cột & hàng
    public void addStone(int rowIndex, int colIndex, int player) {

        if (board[rowIndex][colIndex] == -1) {
            if (player == 0) {
                paintStone(rowIndex, colIndex, 0);
                board[rowIndex][colIndex] = player;

            }
            if (player == 1) {
                paintStone(rowIndex, colIndex, 1);
                board[rowIndex][colIndex] = player;
            }
        }
    }

    ;


    // Lấy tọa độ trục y từ event, trả về chỉ số hàng (row index) trên bàn cờ
    public int getClosestRow(View view, MotionEvent event) {

        int celHeight = view.getHeight() / size;
        int rowIndex = (int) event.getY() / celHeight;

        if (rowIndex < 0) return 0;
        if (rowIndex > size - 1) return size - 1;
        return rowIndex;
    }


    // Lấy tọa độ trục x từ event, trả về chỉ số cột (colmn index) trên bàn cờ
    public int getClosestCol(View view, MotionEvent event) {
        int celWidth = view.getWidth() / size;
        int colIndex = (int) event.getX() / celWidth;

        if (colIndex < 0) return 0;
        if (colIndex > size - 1) return size - 1;
        return colIndex;
    }

}




