package com.example.hoshiko.gomoku.ui;

/**
 * Created by DZUNG NGUYEN on 2018/4/12
 */


public class Line {

    private int xStart;
    private int yStart;
    private int xEnd;
    private int yEnd;

    public Line(double startX, double v, double v1, double v2){}

    public Line(int xStart, int yStart, int xEnd, int yEnd) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
    }

    public int getXStart() {return xStart;}

    public void setXStart(int xStart) {
        this.xStart = xStart;
    }

    public int getYStart() {
        return yStart;
    }

    public void setYStart(int yStart) {
        this.yStart = yStart;
    }

    public int getXEnd() {
        return xEnd;
    }

    public void setXEnd(int xEnd) {
        this.xEnd = xEnd;
    }

    public int getYEnd() {
        return yEnd;
    }

    public void setYEnd(int yEnd) {
        this.yEnd = yEnd;
    }
}

