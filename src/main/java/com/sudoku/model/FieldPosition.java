package com.sudoku.model;

public class FieldPosition {

    private int row;
    private int column;

    public FieldPosition(int row, int column) {
        super();
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }

}
