

import java.util.ArrayList;

/**
 * Document : GameLogic Created on : 24/11/2016, 14:02:21 Author : Daniel Abay
 */
public class GameLogic {

    // ======[תכונות]======
    private int currentPlayer; // השחקן הנוכחי
    private String playerName; // שם של השחקן
    private boolean isWin; // if win = true / if lose = false
    private static final int NUM_ROWS = 6; // מספר שורות
    private static final int NUM_COLS = 7; // מספר עמודות

    public GameLogic() {
        initGame();
    }

    /**
     * Rest the game
     */
    public void initGame() {
        playerName = "Yellow";
        isWin = true;
    }

//============================================================================\\
    /**
     * פועלה שבודקת ניצחון לשחקן הנוכחי בפעולה של
     *
     * @param board הלוח
     * @param player השחקן
     * @return true if player win, false if not
     */
    public boolean isPlayerWin(int[][] board, int player) {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {

                // בדיקה האם יש ניצחון בשורה מצד ימין
                if (isPlayerInPlace(board, row, col, player)
                        && isPlayerInPlace(board, row, col + 1, player)
                        && isPlayerInPlace(board, row, col + 2, player) // Check win for row.
                        && isPlayerInPlace(board, row, col + 3, player)) // Check win for row.
                {
                    return true;
                }

                // בדיקה האם יש ניצחון בשורה מצד שמאל 
                if (isPlayerInPlace(board, row, col, player)
                        && isPlayerInPlace(board, row, col - 1, player)
                        && isPlayerInPlace(board, row, col - 2, player) // Check win for row.
                        && isPlayerInPlace(board, row, col - 3, player)) // Check win for row.
                {

                    return true;
                }

                // בדיקה האם יש ניצחון בעמודה כלפי מעלה
                if (isPlayerInPlace(board, row, col, player)
                        && isPlayerInPlace(board, row - 1, col, player)
                        && isPlayerInPlace(board, row - 2, col, player) // Check win for col.
                        && isPlayerInPlace(board, row - 3, col, player)) // Check win for col.
                {
                    return true;
                }

                // בדיקה האם יש ניצחון בעמודה כלפי מטה
                if (isPlayerInPlace(board, row, col, player)
                        && isPlayerInPlace(board, row + 1, col, player)
                        && isPlayerInPlace(board, row + 2, col, player) // Check win for col.
                        && isPlayerInPlace(board, row + 3, col, player)) // Check win for col.
                {
                    return true;
                }

                // =============== בדיקת ארבעה אלכסונים  ================
                // בדיקה האם יש ניצחון באלכסון ימני עליון
                if (isPlayerInPlace(board, row, col, player)
                        && isPlayerInPlace(board, row - 1, col + 1, player)
                        && isPlayerInPlace(board, row - 2, col + 2, player)
                        && isPlayerInPlace(board, row - 3, col + 3, player)) {
                    return true;
                }

                // בדיקה האם יש ניצחון באלכסון שמאלי עליון
                if (isPlayerInPlace(board, row, col, player)
                        && isPlayerInPlace(board, row - 1, col - 1, player)
                        && isPlayerInPlace(board, row - 2, col - 2, player)
                        && isPlayerInPlace(board, row - 3, col - 3, player)) {
                    return true;
                }

                // בדיקה האם יש ניצחון באלכסון שמאלי תחתון
                if (isPlayerInPlace(board, row, col, player)
                        && isPlayerInPlace(board, row + 1, col - 1, player)
                        && isPlayerInPlace(board, row + 2, col - 2, player)
                        && isPlayerInPlace(board, row + 3, col - 3, player)) {
                    return true;
                }

                // בדיקה האם יש ניצחון באלכסון ימיני תחתון
                if (isPlayerInPlace(board, row, col, player)
                        && isPlayerInPlace(board, row + 1, col + 1, player)
                        && isPlayerInPlace(board, row + 2, col + 2, player)
                        && isPlayerInPlace(board, row + 3, col + 3, player)) {
                    return true;
                }
            }
        }

        return false;
    }

//======================<<<<<<<<<<[]>>>>>>>>>>>========================\\
    /**
     * Method that checks if it's the game is tie.
     *
     * @param board לוח
     * @return true if bard is full if not so false
     */
    public boolean isTie(int[][] board) {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                if (board[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

//======================<<<<<<<<<<>>>>>>>>>>>========================\\
    /**
     * if player 1 is playing right now so the method will change the player to
     * 2;
     *
     * @param player the current player.
     * @return 1 for the current player 2 for second player
     */
    public int getOtherPlayer(int player) {
        if (player == 1) {
            return 2;
        }
        return 1;
    }

    /**
     * מחזיר את מספר השורה הפנויה בעמודה המתקבלת כפרמטר -1 במידה ואין שורה פנויה
     * בעמודה זו יוחזר הערך
     *
     * @param col מספר העמודה לבדיקה
     * @param board הלוח
     * @return returns row, if all board is full returns -1
     */
    public int getEmptyRow(int col, int[][] board) {
        for (int row = NUM_ROWS - 1; row >= 0; row--) {
            if (board[row][col] == 0) {
                return row;
            }
        }

        return -1;
    }


    /**
     * Returns if isWin
     *
     * @return isWin
     */
    public boolean getIsWin() {
        return isWin;
    }

    /**
     * Returns the current player.
     *
     * @return Current Player
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the player name.
     *
     * @return Player Name
     */
    public String getCurrentPlayerName() {
        return playerName;
    }

//==============================[]================================\\
    
    /**
     * checks if the row and col that he gets are on the board limits;
     *
     * @param row שורה
     * @param col עמודה
     * @return
     */
    public boolean isLegalPlace(int row, int col) {
        if (row >= 0 && col >= 0 && row < NUM_ROWS && col < NUM_COLS) {
            return true;
        }
        return false;
    }

    /**
     * הפעולה מקבלת שורה ובודקת את כל השורות בעמודה זו ואם היא מוצאת שמקום פנוי
     * מחזירה אמת ואם לא מחזירה שקר.
     *
     * @param col עמודה
     * @return return true if col is not full, if its full return false
     */
    public boolean isColNotFull(int col, int[][] board) {
        for (int row = 0; row < NUM_ROWS; row++) {
            if (board[row][col] == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * פעולה שבודקת אם השחקן הוא בתחום של הלוח ולא חורג ממנו And checks if the
     * player is in place of board[row][col].
     *
     * @param board לוח
     * @param row שורה
     * @param col עמודה
     * @param player שחקן
     * @return if the player legal place return true if not false
     */
    private boolean isPlayerInPlace(int[][] board, int row, int col, int player) {
        if (row >= 0 && col >= 0 && row < NUM_ROWS && col < NUM_COLS && board[row][col] == player) {
            return true;
        }
        return false;
    }

   public int getNUM_ROWS() {
        return NUM_ROWS;
    }

    public int getNUM_COLS() {
        return NUM_COLS;
    }
}
