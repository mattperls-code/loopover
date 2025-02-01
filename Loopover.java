import java.util.List;
import java.util.ArrayList;

// Initial State:
/*
    ? ? ? ? ? ?
    ? ? ? ? ? ?
    ? ? ? ? ? ?
    ? ? ? ? ? ?
    ? ? ? ? ? ?
    ? ? ? ? ? ?
*/

// Phase 1:
/*
    A B C D E ?
    G H I J K ?
    M N O P Q ?
    S T U V W ?
    Y Z 0 1 2 ?
    ? ? ? ? ? ?
*/

// Phase 2:
/*
    A B C D E F
    G H I J K L
    M N O P Q R
    S T U V W X
    Y Z 0 1 2 3
    ? ? ? ? ? ?
*/

// Phase 3:
/*
    A B C D E F
    G H I J K L
    M N O P Q R
    S T U V W X
    Y Z 0 1 2 3
    4 5 6 ? ? 9
*/

// After Parity Removed:
/*
    A B C D E F
    G H I J K L
    M N O P Q R
    S T U V W X
    Y Z 0 1 2 3
    4 5 6 7 8 9
*/

public class Loopover {
  public static class Position {
    public int x;
    public int y;
    
    public Position(int x, int y){
      this.x = x;
      this.y = y;
    }
  }
  
  public static String primitiveHash(char[][] board){
    String hash = "";
    for(int i = 0;i<board.length;i++){
      for(int j = 0;j<board[0].length;j++){
        hash += board[i][j];
      }
    }
    return hash;
  }
  
  public static boolean boardsAreEqual(char[][] board1, char[][] board2){
    String hash1 = primitiveHash(board1);
    String hash2 = primitiveHash(board2);
    
    return hash1.equals(hash2);
  }
  
  public static Position getPosition(char c, char[][] board){
    for(int i = 0;i<board.length;i++){
      for(int j = 0;j<board[0].length;j++){
        if(board[i][j] == c){
          return new Position(j, i);
        }
      }
    }
    return new Position(-1, -1);
  }
  
  public static void rotateU(int n, char[][] board, List<String> moves){
    char temp = board[0][n];
    for(int i = 1;i<board.length;i++){
      board[i - 1][n] = board[i][n];
    }
    board[board.length - 1][n] = temp;
    
    moves.add("U" + n);
  }
  
  public static void rotateD(int n, char[][] board, List<String> moves){
    char temp = board[board.length - 1][n];
    for(int i = board.length - 1;i>0;i--){
      board[i][n] = board[i - 1][n];
    }
    board[0][n] = temp;
    
    moves.add("D" + n);
  }
  
  public static void rotateL(int n, char[][] board, List<String> moves){
    char temp = board[n][0];
    for(int i = 1;i<board[0].length;i++){
      board[n][i - 1] = board[n][i];
    }
    board[n][board[0].length - 1] = temp;
    
    moves.add("L" + n);
  }
  
  public static void rotateR(int n, char[][] board, List<String> moves){
    char temp = board[n][board[0].length - 1];
    for(int i = board[0].length - 1;i>0;i--){
      board[n][i] = board[n][i - 1];
    }
    board[n][0] = temp;
    
    moves.add("R" + n);
  }
  
  public static void insertPhase1(char c, int targetX, int targetY, char[][] board, ArrayList<String> moves){
    Position pos = getPosition(c, board);
    
    if(pos.x == targetX && pos.y == targetY){
      return;
    }
    
    // if it is not in the correct column, move it all the way down
    if(pos.x != targetX){
      while(pos.y != board.length - 1){
        rotateD(pos.x, board, moves);
        pos.y++;
      }
    }
    // move it to the correct column
    while(pos.x != targetX){
      rotateL(pos.y, board, moves);
      pos = getPosition(c, board);
    }
    // if it is not all the way down, move it all the way down, shift the bottom row, reverse the initial vertical movement, then reverse the shift
    if(pos.y != board.length - 1){
      int verticalMoves = 0;
      while(pos.y != board.length - 1){
        rotateD(pos.x, board, moves);
        verticalMoves++;
        pos.y++;
      }
      rotateL(pos.y, board, moves);
      for(int i = 0;i<verticalMoves;i++){
        rotateU(targetX, board, moves);
      }
      rotateR(pos.y, board, moves);
      pos = getPosition(c, board);
    }
    // shift all the existing pieces in the target column, push it down to the target y, shift all the existing pieces back
    for(int i = 0;i<targetY;i++){
      rotateL(i, board, moves);
    }
    while(pos.y != targetY){
      rotateU(pos.x, board, moves);
      pos.y--;
    }
    for(int i = 0;i<targetY;i++){
      rotateR(i, board, moves);
    }
  }
  
  public static void insertPhase2(char c, int targetY, char[][] board, ArrayList<String> moves){
    Position pos = getPosition(c, board);
    // if it is in the wrong column, move it right until it is
    while(pos.x != board[0].length - 1){
      rotateR(pos.y, board, moves);
      pos.x++;
    }
    // if it is in the slot already, move it back to the top, shift, move the rest of the slot back down, then reverse the shift
    if(pos.y != board.length - 1){
      int verticalMoves = 0;
      while(pos.y != board.length - 1){
        rotateD(pos.x, board, moves);
        verticalMoves++;
        pos.y++;
      }
      rotateL(pos.y, board, moves);
      for(int i = 0;i<verticalMoves;i++){
        rotateU(pos.x, board, moves);
      }
      rotateR(pos.y, board, moves);
    }
    pos = getPosition(c, board);
    
    // shift, push down target column until target y is at the bottom, reverse shift, reverse the vertical push
    rotateL(pos.y, board, moves);
    for(int i = 0;i<board.length - targetY - 1;i++){
      rotateD(pos.x, board, moves);
    }
    rotateR(pos.y, board, moves);
    for(int i = 0;i<board.length - targetY - 1;i++){
      rotateU(pos.x, board, moves);
    }
  }
  
  public static void shiftRight(char[][] board, ArrayList<String> moves){
    int x = board[0].length - 1;
    int y = board.length - 1;

    rotateD(x, board, moves);
    rotateR(y, board, moves);
    rotateR(y, board, moves);
    rotateU(x, board, moves);
    rotateL(y, board, moves);
    rotateD(x, board, moves);
    rotateL(y, board, moves);
    rotateU(x, board, moves);
  }
  
  public static void shiftLeft(char[][] board, ArrayList<String> moves){
    shiftRight(board, moves);
    shiftRight(board, moves);
  }
  
  public static void insertPhase3(char c, int targetX, char[][] board, ArrayList<String> moves){
    Position pos = getPosition(c, board);
    
    if(pos.x == targetX){
      return;
    }
    
    if(pos.x > board[0].length - 3){
      while(pos.x != board[0].length - 3){
        shiftLeft(board, moves);
        pos.x--;
      }
    } else if(pos.x < board[0].length - 3){
      int shifts = 0;
      while(pos.x != board[0].length - 3){
        rotateR(pos.y, board, moves);
        shifts++;
        pos.x++;
      }
      for(int i = 0;i<shifts;i++){
        shiftRight(board, moves);
        rotateL(pos.y, board, moves);
        pos.x--;
      }
    }
    
    for(int i = 0;i<(board[0].length - 3 - targetX);i++){
      rotateR(pos.y, board, moves);
      shiftLeft(board, moves);
    }
    pos = getPosition(c, board);
    
    while(pos.x != targetX){
      rotateL(pos.y, board, moves);
      pos.x--;
    }
  }
  
  public static void removeBottomParity(char[][] board, ArrayList<String> moves){
    int x = board[0].length - 1;
    int y = board.length - 1;
    
    rotateD(x, board, moves);
    rotateL(y, board, moves);
    rotateU(x, board, moves);
    
    rotateL(y, board, moves);
    rotateD(x, board, moves);
    rotateR(y, board, moves);
    
    rotateR(y, board, moves);
    rotateU(x, board, moves);
    rotateL(y, board, moves);
  }
  
  public static void removeSideParity(char[][] board, ArrayList<String> moves){
    int x = board[0].length - 1;
    int y = board.length - 1;
    
    rotateR(y, board, moves);
    rotateU(x, board, moves);
    rotateL(y, board, moves);
    
    rotateU(x, board, moves);
    rotateR(y, board, moves);
    rotateD(x, board, moves);
    
    rotateD(x, board, moves);
    rotateL(y, board, moves);
    rotateU(x, board, moves);
  }
  
  public static void solvePhase1(char[][] mixedUpBoard, char[][] solvedBoard, ArrayList<String> moves){
    for(int j = 0;j<solvedBoard[0].length - 1;j++){
      for(int i = 0;i<solvedBoard.length - 1;i++){
        insertPhase1(solvedBoard[i][j], j, i, mixedUpBoard, moves);
      }
    }
  }
  
  public static void solvePhase2(char[][] mixedUpBoard, char[][] solvedBoard, ArrayList<String> moves){
    for(int i = 0;i<solvedBoard.length - 1;i++){
      insertPhase2(solvedBoard[i][solvedBoard[0].length - 1], i, mixedUpBoard, moves);
    }
  }
  
  public static void solvePhase3(char[][] mixedUpBoard, char[][] solvedBoard, ArrayList<String> moves){
    if(solvedBoard[0].length > 3){
      for(int i = 0;i<solvedBoard[0].length - 3;i++){
        insertPhase3(solvedBoard[solvedBoard.length - 1][i], i, mixedUpBoard, moves);
      }
      Position lastPiece = getPosition(solvedBoard[solvedBoard.length - 1][solvedBoard[0].length - 1], mixedUpBoard);
      if(lastPiece.x == solvedBoard[0].length - 2){
        shiftRight(mixedUpBoard, moves);
      } else if(lastPiece.x == solvedBoard[0].length - 3){
        shiftLeft(mixedUpBoard, moves);
      }
    } else {
      Position pos = getPosition(solvedBoard[solvedBoard.length - 1][0], mixedUpBoard);
      while(pos.x != 0){
        rotateL(pos.y, mixedUpBoard, moves);
        pos.x--;
      }
    }
  }
  
  public static void solveParity(char[][] mixedUpBoard, char[][] solvedBoard, ArrayList<String> moves){
    if((mixedUpBoard.length * mixedUpBoard[0].length) % 2 == 1){
      return;
    } else if(mixedUpBoard[0].length % 2 == 0) {
      removeBottomParity(mixedUpBoard, moves);
      solvePhase3(mixedUpBoard, solvedBoard, moves);
    } else {
      removeSideParity(mixedUpBoard, moves);
      solvePhase2(mixedUpBoard, solvedBoard, moves);
      solvePhase3(mixedUpBoard, solvedBoard, moves);
    }
  }
  
  public static List<String> solve(char[][] mixedUpBoard, char[][] solvedBoard) {
    ArrayList<String> moves = new ArrayList<String>();
    
    if(boardsAreEqual(solvedBoard, mixedUpBoard)){
      return moves;
    }
    
    solvePhase1(mixedUpBoard, solvedBoard, moves);
    solvePhase2(mixedUpBoard, solvedBoard, moves);
    solvePhase3(mixedUpBoard, solvedBoard, moves);
    
    if(!boardsAreEqual(solvedBoard, mixedUpBoard)){
      solveParity(mixedUpBoard, solvedBoard, moves);
    }
    
    if(boardsAreEqual(solvedBoard, mixedUpBoard)){
      return moves;
    }
    
    return null;   
  }
}
