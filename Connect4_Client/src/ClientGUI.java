

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Document : XOGUI Created on : 24/11/2016, 14:02:09 Author : Daniel Abay
 * זוהי המחלקה של הממשק הגרפי של המשחק. היא יוצרת את חלון המשחק,
 * את הפאנלים השונים בחלון, 
 * והיא מבצעת פקודות שונות המבצעות שינויים בממשק,
 * בנוסף, היא גם מחליפה את שפת ממשק המשחק בעת קבלת פקודה של שינוי שפה מן המשתמש.
 */
public class ClientGUI {

    // ======[קבועים]======
    private static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 30);

    // ======[משתנים]======
    private JFrame win;           // GUIהחלון בו יוצג הממשק הגרפי 
    private JPanel pnlButtons;    // פאנל הכפתורים של לוח
    private JButton[][] buttons;  // מערך הכפתורים
    private JMenuBar menuBar;     // בר תפריטים
    private int numRows, numCols;        // גודל הלוח
    private JLabel lblMSg;           //תיבת הטקסט 
    private JLabel lblPlayerName;
    ImageIcon image;
    // ======[משתנים]======
    
    public ClientGUI() {
        this.numRows = Utils.NUM_ROWS; // מספר השורות
        this.numCols = Utils.NUM_COLS; // מספר העמודות
        createGUI();
    }

    /**
     * יצירת ממשק המשתמש
     */
    private void createGUI() {
        win = new JFrame();
        win.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        win.setResizable(false);
        createButtonsPanel();   // יצירת פאנל הכפתורים 
        creatMenueBar();       // יצירת בר התפריטים
        createLblPlayerName();
        createLblMSg();
        
        //הוספת תפריט ופקדים לחלון 
        win.setJMenuBar(menuBar); // קביעת בר התפריטים לחלון
        win.add(pnlButtons, BorderLayout.CENTER); // הוספת פאנל הכפתורים
        win.add(lblMSg, BorderLayout.SOUTH);
        win.add(lblPlayerName, BorderLayout.NORTH);
        win.setAlwaysOnTop(false);
        win.setIconImage(Utils.frameIcon.getImage());
        win.pack(); // התאמת גודל החלון לפקדים והצגתו על המסך
        win.setLocationRelativeTo(null);

    }

    /**
     * Method to set the title text on the window.
     * @param title 
     */
    public void setTitle(String title) {
        win.setTitle(title);
    }

    /**
     * יצירת פאנל הכפתורים
     */
    private void createButtonsPanel() {

        // יצירת מערך דו ממדי לשמירתכל הכפתורים
        buttons = new JButton[numRows][numCols];

        // יצירת פאנל לכפתורים שמסודר בפרישת גריד על פי גודל לוח המשחק
        pnlButtons = new JPanel(new GridLayout(numRows, numCols));
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                // יצירת כפתור
                JButton btn = new JButton();
                btn.setBackground(null);
                btn.setPreferredSize(new Dimension(Utils.BUTTON_SIZE, Utils.BUTTON_SIZE - 2)); //קובע גודל כפתור
                btn.setFont(BUTTON_FONT);
                btn.setFocusable(false);
                // הצמדת מידע חשוב לכפתור
                btn.putClientProperty("rowNum", row);//מספר השורה במערך הכפתורים
                btn.putClientProperty("colNum", col);//מספר העמודה במערך הכפתורים
                pnlButtons.add(btn);
                btn.setIcon(null);
                btn.setBorderPainted(false);
                btn.setEnabled(true);
                buttons[row][col] = btn;
                pnlButtons.setBackground(new Color(0, 111, 208));
            }
        }
    }

    /**
     * יצירת בר התפריטים שבראש החלון
     */
    private void creatMenueBar() {
        menuBar = new JMenuBar();
        JMenu menu1 = new JMenu("Menu");
        JMenuItem menu1_2 = new JMenuItem("Game Rules");
        JMenuItem menu1_3 = new JMenuItem("About Programmer");

        JMenu menu2 = new JMenu("Options");
        JMenuItem menu2_1 = new JMenuItem("Play Music");
        JMenuItem menu2_2 = new JMenuItem("Chat");

        //New Game בעת לחיצה על תת תפריט משחק חדש
        //הוספת תת התפריטים לתפריט הראשי והוספתו לבר התפריטים שבחלון המשחק
        menu1.add(menu1_2);
        menu1.add(menu1_3);
        menu2.add(menu2_1);
        menu2.add(menu2_2);
        menuBar.add(menu1);
        menuBar.add(menu2);
        win.setJMenuBar(menuBar);
    }

    /**
     * פעולה שמאפסת את הכפתורים ומאתחלת את המשחק
     * @param BG תמונת רקע
     */
    public void initGame(ImageIcon BG) {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                buttons[row][col].setBackground(Color.WHITE);
                buttons[row][col].setIcon(BG);
                buttons[row][col].setDisabledIcon(BG);
            }
        }
    }

    /**
     * פעולה שמזירה את מערך הכפתורים
     * @return מערך הכפתורים
     */
    public JButton[][] getButtons() {
        return buttons;
    }

    /**
     * פעולה שמזירה את בר התפריטים
     * @return בר התפריטים
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * פעולה שמחזירה את החלון
     * @return returns a win
     */
    public JFrame getWin() {
        return win;
    }

    /**
     * יצירת תיבת הטקסט של הודעות
     */
    public void createLblMSg() {
        lblMSg = new JLabel();
        lblMSg.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        lblMSg.setForeground(Color.WHITE);
        lblMSg.setBackground(new Color(0, 114, 219));
        lblMSg.setOpaque(true);
        lblPlayerName.setHorizontalAlignment(SwingConstants.CENTER);

    }

    /**
     * פעולה שמציגה את החלון על פי הערך שהוכנס בה
     *
     * @param status ערך הקובע האם החלון יוצג true-החלון יוצג false-החלון לא
     * יוצג
     */
    public void setVisible(boolean status) {
        win.setVisible(status);
    }

    /**
     * פעולה המחזירה את תיבת הטקסט של ההודעות
     * @return תווית הטקסט
     */
    public JLabel getLabelMsg() {
        return lblMSg;
    }

    /**
     * פעולה ששמה טקסט בתוך התווית הודעות
     * @param msg 
     */
    public void setLabelMsgText(String msg) {
        lblMSg.setText(msg + "\n");
    }

    /**
     * פעולה שנועלת את כל הכפתורים במסך בעת סיום המשחק.
     */
    public void endGame() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                buttons[row][col].setEnabled(false);
            }
        }
    }

    /**
     * פעולה ששמה את התמונה של השחקן בצבע שלו על המיקום הספיצי בלוח
     * @param row שורה
     * @param col עמודה
     * @param img תמונה של השחקן
     */
    public void setButtonOn(int row, int col, ImageIcon img) {
        buttons[row][col].setIcon(img);
        buttons[row][col].setDisabledIcon(img);
        buttons[row][col].setEnabled(false);
    }

    /**
     * פעולה שמאפשרת להפעיל אפשרות לחיצה על הכפתורים שבלוח
     * או שהיא יכולה לבטל אותם
     * @param status 
     */
    public void enableAllButtons(boolean status) {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                buttons[row][col].setEnabled(status);
            }
        }

    }

    /**
     * פעולה שמציגה את המסך פתיחה.
     */
    private void showSplashScreen() {
        JLabel lblSplash = new JLabel(Utils.splashIcon);
        lblSplash.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        JOptionPane.showMessageDialog(null, lblSplash, "Welcome", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * יוצר את התווית טקסט שמופיע באה את השם של השחקן
     * .
     */
    private void createLblPlayerName() {
        lblPlayerName = new JLabel();
        lblPlayerName.setFont(new Font(Font.DIALOG, Font.BOLD, 17));
        lblPlayerName.setBackground(new Color(0, 111, 208));
        lblPlayerName.setOpaque(true);
        lblPlayerName.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * מחזיר את התווית שרשום בתוכה את שם השחקן
     * @return 
     */
    public JLabel getLablPlayerName() {
        return lblPlayerName;
    }

}
