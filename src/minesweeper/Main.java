package minesweeper;


import java.util.*;

public class Main {
  private static final Scanner sc = new Scanner(System.in);
  private static final int n = 9;
  private static final Printer p = new Printer(n);
  private static boolean isFirstExplore = true;
  private static boolean isUserLose = false;
  private static CellType[][] matrix;
  private static CellType[][] displayMatrix;
  private static int mineNumber;

  public static void main(String[] args) {
    System.out.println("How many mines do you want on the field?");
    mineNumber = sc.nextInt();
    mkMatrix();
    p.printMatrix(displayMatrix);

    while(!isFinish()) {
      battle();
      if(isUserLose) {
        p.printMatrix(displayMatrix, true, matrix);
      } else {
        p.printMatrix(displayMatrix);
      }
    }

    if(isUserLose) {
      System.out.println("You stepped on a mine and failed!");
    } else {
      System.out.println("Congratulations! You found all mines!");
    }
  }

  static boolean isFinish() {
    return isUserLose || isAllMarked() || isAllExplored();
  }

  static void mkMatrix() {
    Matrix m = new Matrix(n, mineNumber);
    updateMatrix(m);
  }

  /**
   * Make matrix with excluding point.
   * If there are some marked cell in the previous displayMatrix, copy the mark to new displayMatrix.
   * @param excludePoint this cell is not set mine
   */
  static void mkMatrix(Point excludePoint) {
    Matrix m = new Matrix(n, mineNumber, excludePoint);
    updateMatrix(m);
  }

  static void updateMatrix(Matrix m) {
    matrix = m.matrix;
    displayMatrix = m.displayMatrix;
  }

  static void battle() {
    // Input
    Input input = readInput();
    int col = input.p.x;
    int row = input.p.y;
    Point point = new Point(col, row);

    if(!input.isFree()) {
      mark(point);
    }

    if(input.isFree()) {
      if(isFirstExplore) {
        isFirstExplore = false;
        CellType[][] oldDisplayMatrix = displayMatrix.clone();
        mkMatrix(point);
        explore(point);
        displayMatrix = Matrix.copyMark(displayMatrix.clone(), oldDisplayMatrix);
      } else {
        if(matrix[row][col].equals(CellType.MINE)) {
          isUserLose = true;
        } else {
          explore(point);
        }
      }
    }
  }

  static void mark(Point p) {
    int col = p.x;
    int row = p.y;
    switch (displayMatrix[row][col]) {
      case UNKNOWN:
        displayMatrix[row][col] = CellType.MARK;
        break;
      case MARK:
        displayMatrix[row][col] = CellType.UNKNOWN;
        break;
      default:
        System.out.printf("We cannot mark the cell. (CellType is %s)", displayMatrix[row][col]);
    }
  }

  static void explore(Point p) {
    int col = p.x;
    int row = p.y;
    displayMatrix[row][col] = matrix[row][col];

    // If the cell is safe, explore around the cell
    if(matrix[row][col].equals(CellType.SAFE)) {
      for(int i = row-1; i <= row+1; i++) {
        for(int j = col-1; j <= col+1; j++) {
          if(0 <= i && i < n && 0 <= j && j < n && !displayMatrix[i][j].isExplored()) {
            explore(new Point(j, i));
          }
        }
      }
    }
  }

  static Input readInput() {
    System.out.println("Set/unset mines marks or claim a cell as free:");
    Input input = read();
    int x = input.p.x, y = input.p.y;

    // Validate input
    while(x < 0 || n-1 < x || y < 0 || n-1 < y) {
      System.out.printf("Range is between 1 to %d%n", n);
      input =  read();
      x = input.p.x;
      y = input.p.y;
    }

    if(!isFirstExplore) {
      while(displayMatrix[y][x].v.matches("[0-9]")) {
        System.out.println(
            "There is a number here!\n" +
                "Set/delete mines marks (x and y coordinates): ");
        input = read();
        x = input.p.x;
        y = input.p.y;
      }
    }
    return input;
  }

  static Input read() {
    int x = sc.nextInt()-1;
    int y = sc.nextInt()-1;
    String operation = sc.next();
    return new Input(new Point(x, y), operation);
  }

  static boolean isAllMarked() {
    int markCnt = 0;
    for(int i = 0; i < n; i++) {
      for(int j = 0; j < n; j++) {
        if(matrix[i][j].equals(CellType.MINE) && !displayMatrix[i][j].equals(CellType.MARK)) {
          return false;
        }
        if(displayMatrix[i][j].equals(CellType.MARK)) {
          markCnt++;
        }
      }
    }
    return markCnt == mineNumber;
  }

  static boolean isAllExplored() {
    for(int i = 0; i < n; i++) {
      for(int j = 0; j < n; j++) {
        // All cells must be SAFE or MINE
        if(!(displayMatrix[i][j].isExplored() || matrix[i][j].equals(CellType.MINE))) {
          return false;
        }
      }
    }
    return true;
  }

}


class Input {
  Point p;
  String operation;

  public Input(Point p, String operation) {
    this.p = p;
    this.operation = operation;
  }

  boolean isFree() {
    return this.operation.equals("free");
  }
}

class Point {
  int x;
  int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }
}

class Matrix {
  CellType[][] matrix;
  CellType[][] displayMatrix;

  public Matrix(int n, int mineNumber) {
    this(n, mineNumber, new Point(1, 1));
  }
  
  public Matrix(int n, int mineNumber, Point excludePoint) {
    // Make n * n size matrix
    CellType[][] matrix = new CellType[n][n];
    for(CellType[] row : matrix)
      Arrays.fill(row, CellType.SAFE);

    // Embed mines
    for(int i = 1; i <= mineNumber;) {
      int row = new Random().nextInt(9);
      int column = new Random().nextInt(9);
      if(matrix[row][column].equals(CellType.SAFE) && !(row == excludePoint.x && column == excludePoint.y)) {
        matrix[row][column] = CellType.MINE;
        i++;
      }
    }

    // Make hint
    for(int i = 0; i < n; i++) {
      for(int j = 0; j < n; j++) {
        int mineCount = 0;
        for(int k = i-1; k <= i+1; k++) {
          for(int l = j-1; l <= j+1; l++) {
            if(k >= 0 && k < n && l >= 0 && l < n && !(k == i && l == j)) {
              if(matrix[k][l].equals(CellType.MINE)) {
                mineCount++;
              }
            }
          }
        }
        if(matrix[i][j].equals(CellType.SAFE) && mineCount > 0) {
          matrix[i][j] = CellType.fromNum(mineCount);
        }
      }
    }
    this.matrix = matrix;
    this.displayMatrix = initDisplayMatrix(n);
  }

  public static String[][] toStrArray(CellType[][] matrix) {
    int row = matrix.length;
    int col = matrix[0].length;
    String[][] strArr = new String[row][col];
    for(int i = 0; i < row; i++) {
      for(int j = 0; j < col; j++) {
        strArr[i][j] = matrix[i][j].v;
      }
    }
    return strArr;
  }

  static CellType[][] initDisplayMatrix(int n) {
    CellType[][] displayMatrix = new CellType[n][n];
    for(CellType[] row : displayMatrix) {
      Arrays.fill(row, CellType.UNKNOWN);
    }
    return displayMatrix;
  }

  public static CellType[][] copyMark(CellType[][] newArr, CellType[][] oldArr) {
    int row = newArr.length;
    int col = newArr[0].length;
    for(int i = 0; i < row; i++) {
      for(int j = 0; j < col; j++) {
        if(oldArr[i][j].equals(CellType.MARK) && !isNextToSAFE(newArr, i, j)) {
          newArr[i][j] = CellType.MARK;
        }
      }
    }
    return newArr;
  }

  // Check if there are SAFE cells around the target cell
  private static boolean isNextToSAFE(CellType[][] arr, int row, int col) {
    int len = arr.length;
    for(int i = row-1; i <= row+1; i++) {
      for(int j = col-1; j <= col+1; j++) {
        if(i >= 0 && j >= 0 && i < len && j < len && !(i == row && j == col)
            && arr[i][j].equals(CellType.SAFE)) {
          return true;
        }
      }
    }
    return false;
  }
}

enum CellType {
  MINE("X"),
  UNKNOWN("."),
  MARK("*"),
  SAFE("/"),
  N1("1"),
  N2("2"),
  N3("3"),
  N4("4"),
  N5("5"),
  N6("6"),
  N7("7"),
  N8("8");

  String v;
  
  CellType(String v) {
    this.v = v;
  }

  public static CellType fromNum(int num) {
    return Arrays.stream(CellType.values())
        .filter(cellType -> cellType.v.equals(Integer.toString(num)))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

  public boolean isExplored() {
    return !(this.equals(CellType.UNKNOWN) || this.equals(CellType.MARK));
  }
}

class Printer {
  int n;

  public Printer(int n) {
    this.n = n;
  }

  void printMatrix(CellType[][] matrix) {
    printMatrix(matrix, false, null);
  }

  void printMatrix(CellType[][] matrix, boolean isShowMines, CellType[][] matrixWithMine) {
    String[] strArr = new String[n];
    String[][] strMatrix = Matrix.toStrArray(matrix.clone());
    for(int i = 0; i < n; i++) {
      String content = String.join("", strMatrix[i]);
      if(isShowMines) {
        for(int j = 0; j < n; j++) {
          if(matrixWithMine[i][j].equals(CellType.MINE)) {
            strMatrix[i][j] = CellType.MINE.v;
            content = String.join("", strMatrix[i]);
          }
        }
      }
      String row = String.format("%d|", i+1) + content + "|";
      strArr[i] = row;
    }
    String ans = String.join("\n", strArr);

    System.out.println(" │123456789│");
    System.out.println("—│—————————│");
    System.out.println(ans);
    System.out.println("—│—————————│");
  }
}
