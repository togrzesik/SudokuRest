package com.sudoku.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SudokuField {


    public static final int MAXSOLUTIONS = 10;
    int recursioncounter=0;
    private static int size=0;
    private SudokuCell[][] cells;
    private ArrayList<SudokuCell[][]> solutions = new ArrayList<>();

    public SudokuField(int size) {
        super();
        SudokuField.size = size;
        this.cells = new SudokuCell[size][size];
        initializeFieldAndBorderSettings();
    }

    public SudokuField() {
        if (SudokuField.size == 0 )
            throw new RuntimeException("Rozmiar pola nie został ustawiony przed pierwszą instancją obiektu!");
        this.cells = new SudokuCell[SudokuField.size][SudokuField.size];
        initializeFieldAndBorderSettings();
    }

    private void initializeFieldAndBorderSettings() {

        int quadrantSize = (int) Math.sqrt(this.getCells().length);

        for (int row = 0; row < this.cells.length; row++)
            for (int col = 0; col < cells[row].length; col++) {


                this.cells[row][col] = new SudokuCell(0, false);

                SudokuCell currentCell = this.cells[row][col];


                if ((row % quadrantSize) == 0) {
                    currentCell.setBorderClasses(currentCell.getBorderClasses() + " bordertop");
                } else if (row == (this.cells.length-1)) {
                    currentCell.setBorderClasses(currentCell.getBorderClasses() + " borderbottom");
                }

                if ((col %  quadrantSize) == 0) {
                    currentCell.setBorderClasses(currentCell.getBorderClasses() + " borderleft");
                } else if (col == (this.cells[row].length-1)) {
                    currentCell.setBorderClasses(currentCell.getBorderClasses() + " borderright");
                }
            }
    }

    public static void setSize(int size) {
        SudokuField.size = size;
    }

    public static int getSize() {
        return size;
    }

    public boolean fieldIsValid() {
        return columnsAreValid()
                && rowsAreValid()
                && quadrantsAreValid();
    }

    private boolean columnsAreValid() {

        for (int col = 0; col < SudokuField.getSize(); col++) {
            List<Integer> vorkommendeWerte = new ArrayList<>(SudokuField.getSize());
            for (int row = 0; row < SudokuField.getSize(); row++) {
                Integer currentCellValue = (Integer) this.getCells()[row][col].getValue();
                if (currentCellValue < 0
                        || currentCellValue > SudokuField.getSize()
                        || vorkommendeWerte.contains(currentCellValue)) {
                    return false;
                } else if (currentCellValue > 0 && !vorkommendeWerte.contains(currentCellValue)) {
                    vorkommendeWerte.add(currentCellValue);
                }
            }
        }
        return true;
    }

    private boolean rowsAreValid() {

        for (int row = 0; row < SudokuField.getSize(); row++) {
            List<Integer> vorkommendeWerte = new ArrayList<>(SudokuField.getSize());
            for (int col = 0; col < SudokuField.getSize(); col++) {
                Integer currentCellValue = (Integer) this.getCells()[row][col].getValue();
                if (currentCellValue < 0
                        || currentCellValue > SudokuField.getSize()
                        || vorkommendeWerte.contains(currentCellValue)) {
                    return false;
                } else if (currentCellValue > 0 && !vorkommendeWerte.contains(currentCellValue)) {
                    vorkommendeWerte.add(currentCellValue);
                }
            }
        }
        return true;
    }

    private boolean quadrantsAreValid() {

        int noOfSegments = (int) Math.sqrt(SudokuField.getSize());
        int elPerSegment = noOfSegments;

        for (int vSegment = 0; vSegment < noOfSegments; vSegment++) {

            for (int hSegment = 0; hSegment < noOfSegments; hSegment++)  {

                List<Integer> vorkommendeWerte = new ArrayList<>(SudokuField.getSize());

                for (int row = vSegment*elPerSegment; row < (vSegment+1)*elPerSegment; row++) {
                    for (int col = hSegment*elPerSegment; col < (hSegment+1)*elPerSegment; col++) {
                        Integer currentCellValue = (Integer) this.getCells()[row][col].getValue();
                        if (currentCellValue < 0
                                || currentCellValue > SudokuField.getSize()
                                || vorkommendeWerte.contains(currentCellValue)) {
                            return false;
                        } else if (currentCellValue > 0 && !vorkommendeWerte.contains(currentCellValue)) {
                            vorkommendeWerte.add(currentCellValue);
                        }
                    }
                }
            }
        }

        return true;

    }

    public SudokuCell[][] findSolutions(SudokuCell[][] arbeitsFeld) {

        recursioncounter++;
        FieldPosition betrachtetePos = SudokuField.searchNextFieldVacant(arbeitsFeld);

        if (betrachtetePos == null) {
            System.out.println("W " + (recursioncounter-1) + ". polu zostało anulowane!\n");
            printFieldToConsole();
            this.getSolutions().add(this.copyCells(arbeitsFeld));
            return arbeitsFeld;
        }

        List<Integer> optionenFuerPos = SudokuField.findDigitsPossibleForPosition(betrachtetePos, arbeitsFeld);
        if (optionenFuerPos != null ) {

            for (Integer option : optionenFuerPos) {
                arbeitsFeld[betrachtetePos.getRow()][betrachtetePos.getColumn()].setValue(option);
                SudokuCell[][] lsg = findSolutions(arbeitsFeld);
                if (this.getSolutions().size() == SudokuField.MAXSOLUTIONS) {
                    return lsg;
                }
                arbeitsFeld[betrachtetePos.getRow()][betrachtetePos.getColumn()].setValue(0);
            }
        }

        return null;

    }

    private static List<Integer> findDigitsPossibleForPosition(FieldPosition betrachtetePos, SudokuCell[][] arbeitsFeld) {

        List <Integer> moeglicheWerte = new ArrayList<Integer>();

        for (int i=1; i < arbeitsFeld.length+1; i++) {
            moeglicheWerte.add(i);
        }

        moeglicheWerte = removeRowValuesInConflict(betrachtetePos, moeglicheWerte, arbeitsFeld);
        if (moeglicheWerte != null && !moeglicheWerte.isEmpty()) {

            moeglicheWerte = removeColumnValuesInConflict(betrachtetePos, moeglicheWerte, arbeitsFeld);

            if (moeglicheWerte != null && !moeglicheWerte.isEmpty()) {
                moeglicheWerte = removeQuandrantValuesInConflict(betrachtetePos, moeglicheWerte, arbeitsFeld);
            }
        }

        return moeglicheWerte != null && !moeglicheWerte.isEmpty() ? moeglicheWerte : null;

    }

    private static List<Integer> removeQuandrantValuesInConflict(FieldPosition betrachtetePos,
                                                                 List<Integer> moeglicheWerte, SudokuCell[][] arbeitsFeld) {

        List<Integer> verbleibendeMoeglicheWerte = new ArrayList<Integer>(moeglicheWerte);

        int quadrantSize = (int) Math.sqrt(arbeitsFeld.length);
        int startRow = (betrachtetePos.getRow() / quadrantSize) * quadrantSize;
        int startColumn = (betrachtetePos.getColumn() / quadrantSize) * quadrantSize;

        for (int row = startRow; row < startRow + quadrantSize; row++) {
            for (int column = startColumn; column < startColumn + quadrantSize; column++) {
                verbleibendeMoeglicheWerte.remove((Integer) arbeitsFeld[row][column].getValue());
            }
        }
        return verbleibendeMoeglicheWerte;
    }

    private static List<Integer> removeColumnValuesInConflict(FieldPosition betrachtetePos,
                                                              List<Integer> moeglicheWerte, SudokuCell[][] arbeitsFeld) {

        List<Integer> verbleibendeMoeglicheWerte = new ArrayList<Integer>(moeglicheWerte);
        for (int row = 0; row < arbeitsFeld.length; row++) {
            verbleibendeMoeglicheWerte.remove((Integer) arbeitsFeld[row][betrachtetePos.getColumn()].getValue());
        }

        return verbleibendeMoeglicheWerte;
    }

    private static List<Integer> removeRowValuesInConflict(FieldPosition betrachtetePos, List<Integer> moeglicheWerte,
                                                           SudokuCell[][] arbeitsFeld) {

        List<Integer> verbleibendeMoeglicheWerte = new ArrayList<Integer>(moeglicheWerte);
        int maxColumn = arbeitsFeld[betrachtetePos.getRow()].length;
        for (int column = 0; column < maxColumn; column++) {
            verbleibendeMoeglicheWerte.remove((Integer) arbeitsFeld[betrachtetePos.getRow()][column].getValue());
        }

        return verbleibendeMoeglicheWerte;
    }

    private static FieldPosition searchNextFieldVacant(SudokuCell[][] arbeitsFeld) {

        for (int row=0; row < arbeitsFeld.length; row++) {
            for (int column=0; column < arbeitsFeld[row].length; column++) {
                if (arbeitsFeld[row][column].getValue() == 0) {
                    return new FieldPosition(row,column);
                }
            }
        }
        return null;
    }


    public SudokuCell[][] copyCells (SudokuCell[][] quelle) {

        SudokuCell[][] kopie = new SudokuCell[SudokuField.getSize()][SudokuField.getSize()];

        for (int i=0; i < quelle.length; i++) {
            for (int j=0; j < quelle[i].length; j++) {
                kopie[i][j] = new SudokuCell(quelle[i][j].getValue(), quelle[i][j].isStartValue());
                kopie[i][j].setBorderClasses(quelle[i][j].getBorderClasses());
            }
        }
        return kopie;

    }

    private void printFieldToConsole() {

        System.out.println();

        int quadrantSize = (int) Math.sqrt(this.getCells().length);

        for (int row=0; row < this.getCells().length; row++) {
            for (int column=0; column < this.getCells()[row].length; column++) {
                System.out.printf("%2d", this.getCells()[row][column].getValue());
                if ((column+1) %  quadrantSize == 0) {
                    System.out.print("  ");
                }
            }
            if ((row % quadrantSize) == (quadrantSize - 1))
                System.out.println("\n");
            System.out.println("\n");
        }

    }

}
