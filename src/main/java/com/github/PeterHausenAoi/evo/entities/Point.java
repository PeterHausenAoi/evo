package com.github.PeterHausenAoi.evo.entities;

public class Point {
    private static final String TAG = Point.class.getSimpleName();

    private Number mX;
    private Number mY;

    public Point(Number x, Number y) {
        this.mX = x;
        this.mY = y;
    }

    public Number getX() {
        return mX;
    }

    public Number getY() {
        return mY;
    }

    public void setX(int mX) {
        this.mX = mX;
    }

    public void setY(int mY) {
        this.mY = mY;
    }
}