

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

/**
 * Document : Utils Created on : 24/05/2017, 22:35:11 Author : Daniel Abay
 */
public class Utils {

    public static final int NUM_ROWS = 6; // מספר שורות
    public static final int NUM_COLS = 7; // מספר עמודות
    public static final int YELLOW = 1; // מספר השחקן הראשון
    public static final int BLUE = 2; // מספר השחקן השני
    public static final long MAX_TIME = 14000; // הזמן המקסימילי של המינימקס לרוץ
    public static int BUTTON_SIZE;  // הגודל של הכפתור
    public static ImageIcon yellowIcon;        // אייקון הצהוב
    public static ImageIcon redIcon;        //  אייקון הכחול 
    public static ImageIcon backgroundIcon;       // האיקון של הרקע הכפתורים
    public static ImageIcon yellowWinIcon;        // האייקון של הניצחון של הכפתור הצהוב
    public static ImageIcon redWinIcon;        // האייקון של הניצחון של הכפתור הכחול
    public static ImageIcon splashIcon;   //שומר את הודעת הפתיחה
    public static ImageIcon frameIcon;    //האיקון של החלון
    public static ImageIcon loginIcon;
    public static ImageIcon chatPicture;
    public static Sound clickSound; // צליל של קליק
    public static Sound BGMusic; // מוזיקת רקע
    public static Sound gameOverSound; // צליל של המשחק נגמר
    public static Sound winSound; // צליל של ניצחון
    public static Sound MassageReceived; // צליל קבלת הודעה משחקן בצאט
   
    /**
     * טוען את קבצי המדיה
     */
    public static void loadMedia() {
        setScreenSize();
        setSoundEffects();

        splashIcon = new ImageIcon(Utils.class.getResource("media/splash.png"));
        splashIcon = new ImageIcon(splashIcon.getImage().getScaledInstance(653, 489, Image.SCALE_SMOOTH));

        yellowIcon = new ImageIcon(Utils.class.getResource("media/x.png"));
        yellowIcon = new ImageIcon(yellowIcon.getImage().getScaledInstance((int) (BUTTON_SIZE * 1.3), (int) (BUTTON_SIZE * 0.9), Image.SCALE_SMOOTH));

        redIcon = new ImageIcon(Utils.class.getResource("media/o.png"));
        redIcon = new ImageIcon(redIcon.getImage().getScaledInstance((int) (BUTTON_SIZE * 1.3), (int) (BUTTON_SIZE * 0.9), Image.SCALE_SMOOTH));

        backgroundIcon = new ImageIcon(Utils.class.getResource("media/BG.png"));
        backgroundIcon = new ImageIcon(backgroundIcon.getImage().getScaledInstance((int) (BUTTON_SIZE * 1.3), (int) (BUTTON_SIZE * 0.9), Image.SCALE_SMOOTH));

        yellowWinIcon = new ImageIcon(Utils.class.getResource("media/YellowWinButton.png"));
        yellowWinIcon = new ImageIcon(yellowWinIcon.getImage().getScaledInstance((int) (BUTTON_SIZE * 1.3), (int) (BUTTON_SIZE * 0.9), Image.SCALE_SMOOTH));

        redWinIcon = new ImageIcon(Utils.class.getResource("media/redWinButton.png"));
        redWinIcon = new ImageIcon(redWinIcon.getImage().getScaledInstance((int) (BUTTON_SIZE * 1.3), (int) (BUTTON_SIZE * 0.9), Image.SCALE_SMOOTH));
        
        frameIcon = new ImageIcon(Utils.class.getResource("media/frameIcon.png"));
        
        loginIcon  = new ImageIcon(Utils.class.getResource("media/login.png"));
        
        chatPicture = new ImageIcon(Utils.class.getResource("media/login.png"));
        
    }

    /**
     * פעולה שקובעת את גודל המסך
     */
    public static void setScreenSize() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        BUTTON_SIZE = (int) (size.getHeight() / (NUM_COLS * 1.60));
    }

    /**
     * פעולה שטוענת את קבצי הסאונד
     */
    public static void setSoundEffects() {
        clickSound = new Sound(Utils.class.getResource("media/click.wav"));
        BGMusic = new Sound(Utils.class.getResource("media/BG Music.wav"));
        gameOverSound = new Sound(Utils.class.getResource("media/gameover.wav"));
        winSound = new Sound(Utils.class.getResource("media/win.wav"));
        MassageReceived = new Sound(Utils.class.getResource("media/MassageReceived.wav"));
    }

}
