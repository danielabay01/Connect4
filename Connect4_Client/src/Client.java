
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Document : Client Created on : 16/04/2018, 14:02:21 Author : Daniel Abay
 */
public class Client implements Serializable {

//==============[משתנים]==============//
    private String serverHost; // האייפי של השרת שמתחברים אליו
    private int serverPort; // הפורט שאנו מאזינים לו
    private ClientGUI gui; // עצם מסוג -Gui בשיל הגרפיקה
    private String clientID; // מזהה של השחקן
    private String clientName; // השם של השחקן
    private Socket clientSocket; // הסאקט שמקשר בין הלקוח לבין השרת ודרכו עוברת או נשלח הנתונים
    private ObjectOutputStream os; // שולח לשרת מידע
    private ObjectInputStream is; // מקבל מהשרת מידע
    private ImageIcon playerImage; // תמונת השחקן
    private boolean canOpenChat; // מאפשר להראות את הצאט

    private JTextArea chatArea; // האזור הדפסה של ההודעות של הצאט
    private JPanel chatPnl; //הפנל של החלון
    private JTextField chatField; // התחום שבו רושמים הודעות בצאט של המשחק
    private JFrame chatWin; // חלון הצאט
//==============[משתנים]==============//    

    /**
     * פעולה ראשית שמפעילה את הלקוח
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.run();
    }

    /**
     * הבנאית של הלקוח
     *
     * @throws Exception
     */
    public Client() throws Exception {
        Utils.setScreenSize(); // פעולה שקובעת את הגודל של הלוח
        Utils.loadMedia(); // פעולה שטוענת את כל המידה
        checkClientDetails(); // פעולת התחברות
        crateGUI();
        crateChatGUI();
        setEventHandler();
        setPlayerImage();

    }

    /**
     * הגדרת המשתנים בגלובלים במהלך ההתחברות לשרת.
     */
    private void clientSetup() {
        canOpenChat = false;
        clientID = "";
        try {
            clientSocket = new Socket(serverHost, serverPort);
            os = new ObjectOutputStream(clientSocket.getOutputStream());
            is = new ObjectInputStream(clientSocket.getInputStream());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "We found some problems.\nThe server is FULL/NOT ONLINE at this moment.\nPlease try again next time.\nThank you.", "Warning!", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }

    }

    /**
     * יוצר את הגרפיקה של הלקוח
     *
     * @throws Exception
     */
    private void crateGUI() throws Exception {
        gui = new ClientGUI();
        gui.initGame(Utils.backgroundIcon);
        gui.setVisible(true);
        gui.enableAllButtons(false);
        clientID = (String) is.readObject();
        gui.setTitle("Connet 4 - " + clientName + " (" + clientID + ") @By Daniel Abay");
        if (clientID.equals("Player 1")) {
            gui.getLablPlayerName().setText("Yellow Player");
            gui.getLablPlayerName().setForeground(Color.yellow);

        } else {
            gui.getLablPlayerName().setText("Red Player");
            gui.getLablPlayerName().setForeground(Color.red);
        }
        gui.setLabelMsgText("Welcome, Please wait for another player to connect...");
    }

    /**
     * פעולה שיוצרת את הגרפיקה של הצאט.
     */
    private void crateChatGUI() {
        chatArea = new JTextArea();
        chatPnl = new JPanel(new BorderLayout());
        chatField = new JTextField(40);
        chatWin = new JFrame(clientName + "'s Chat");

        chatWin.setSize(350, 350);
        chatWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chatWin.setBackground(new java.awt.Color(0, 0, 0));
        chatWin.setForeground(new java.awt.Color(51, 255, 255));
        chatWin.setResizable(false);
        chatWin.setLocationRelativeTo(null);
        chatWin.setAlwaysOnTop(true);

        chatArea.setEditable(false);
        chatArea.setFont(new Font(Font.DIALOG, 15, 15));
        chatField.setFont(new Font(Font.DIALOG, 15, 15));
        chatArea.setForeground(Color.BLACK);
        chatArea.setBackground(Color.WHITE);
        chatArea.setText("Welcome " + clientName + " (" + clientID + ") " + ", Say 'Hi'. \n");

        chatPnl.add(chatArea, BorderLayout.CENTER);
        chatPnl.add(chatField, BorderLayout.SOUTH);
        chatPnl.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatWin.add(chatPnl, BorderLayout.CENTER);

        chatPnl.setVisible(true);
        chatField.setVisible(true);
    }

    /**
     * פעולת האזנה הסרבר שולח הודעה ללקוח והלקוח בודק מה הסרבר רצה ממנו על פי ההודעה שהוא שלח לו
     *
     * @throws Exception
     */
    private void listenerFromServer() throws Exception {
        Object msgFromServer;
        try {
            while (true) {
                try {
                    msgFromServer = is.readObject(); // מחכה לסרבר שישלח לו הודעה מה לעשות

                    if (msgFromServer instanceof String) {
                        if (((String) msgFromServer).contains("Please wait")) // במקרה שהסרבר שולח ללקוח הודעה שמכילה בבקשה המתן
                        {
                            if (clientID.equals("Player 1") && msgFromServer.equals("Please wait for your turn.")) // אם הלוקח הוא שחקן אחד והוא צריך להמתין לתור שלו
                            {
                                gui.setLabelMsgText("<html>>>Please <font color='yellow'>WAIT</font> for your turn...</html>");
                            } else if (clientID.equals("Player 2") && msgFromServer.equals("Please wait for your turn.")) // אם הלוקח הוא שחקן שתיים והוא צריך להמתין לתור שלו
                            {
                                gui.setLabelMsgText("<html>>>Please <font color='red'>WAIT</font> for your turn...</html>");
                            }
                            if (msgFromServer.equals("Welcome, Please wait for another player to connect...")) //  אם הסרבר שלוח שצריך להמתין לשחקן נוסיף שיצטרף למשחק
                            {
                                gui.setLabelMsgText("<html>Please wait for another player to connect...</html>");
                            }
                            gui.enableAllButtons(false);
                        } else if (((String) msgFromServer).equals("Play")) // במקרה שהסרבר שולח הודעה של שחק
                        {
                            if (clientID.equals("Player 1")) // אם השחקן הוא שחקן אחד אז תרשום בטקסט שתורו של שחקן צהוב
                            {
                                gui.setLabelMsgText("<html>>>Its your turn to <font color='yellow'>PLAY</font>.</html>");
                            } else if (clientID.equals("Player 2")) // אם השחקן הוא שחקן שתיים אז תרשום בטקסט שתורו של שחקן אדום
                            {
                                gui.setLabelMsgText("<html>>>Its your turn to <font color='red'>PLAY</font>.</html>");
                            }
                            gui.enableAllButtons(true);
                        } else if (((String) msgFromServer).equals("Place is Ok")) // אם המיקום שהלקוח לחץ בסדר אז הסרבר שולח לשני הלקחות שהמיקום בסדר
                        {
                            int row = (Integer) is.readObject(); // מקבל את המיקום בשורה בלוח
                            int col = (Integer) is.readObject(); // מקבל את המיקום בעמודה בלוח
                            String player = (String) is.readObject(); // שולח את השחקן שעשה את המהלך
                            if (player.equals("Player 1")) // אם השחקן שעשה את המהלך הוא השחקן אחד אז שים את הצבע צהוב
                            {
                                gui.setButtonOn(row, col, Utils.yellowIcon);
                            }
                            gui.enableAllButtons(false);
                            if (player.equals("Player 2")) // אם השחקן שעשה את המהלך הוא השחקן שתיים אז שים את הצבע אדום
                            {
                                gui.setButtonOn(row, col, Utils.redIcon);
                            }
                            gui.enableAllButtons(false);
                        } else if (((String) msgFromServer).equals(">> Player Yellow Won! <<")) // אם שלח לו הודעה ששחקן הצהוב ניצח
                        {
                            // ששחקן הצהוב ניצח ומתכונן לרצה של משחק חדש
                            gui.setLabelMsgText("<html>>> Player <font color='yellow'>Yellow</font> Won! <<</html>");
                            playSound(Utils.winSound);
                            newGame();

                        } else if (((String) msgFromServer).equals(">> Player Red Won! <<")) // אם שלח לו הודעה ששחקן האדום ניצח
                        {
                            // ששחקן האדום ניצח ומתכונן לרצה של משחק חדש
                            gui.setLabelMsgText("<html>>> Player <font color='red'>Red</font> Won! <<</html>");
                            playSound(Utils.winSound);
                            newGame();

                        } else if (((String) msgFromServer).equals(">> It's a Tie! <<")) // במקרה שיש תיקו במשחק
                        {
                            gui.setLabelMsgText(msgFromServer.toString());
                            newGame(); // מתחיל משחק חדש

                        } else if (((String) msgFromServer).equals("Disconnected")) // במקרה שאחד השחקנים מתנתק באמצע המשחק
                        {
                            gui.enableAllButtons(false); // מבטלת את כל הכפתורים
                            JOptionPane.showMessageDialog(gui.getWin(), "Your opponet left the game.", "Warning!", JOptionPane.ERROR_MESSAGE); // מודיע שהשחקן עזב את המשחק
                            for (int i = 3; i > -1; i--) {
                                // מריץ לולאה של חמש שניות עד שהמשחק יסתיים
                                // ומודיע לשחקן שהמשחק יוצא בעוד חמש שניות
                                gui.setLabelMsgText(">>Leaving the game in " + i + "...");
                                Thread.sleep(1000);
                            }
                            clientSocket.close(); // סוגר את החיבור לשרת
                            System.exit(0); //יוצא מהמשחק
                        } else if (((String) msgFromServer).equals("Two Connected")) // מודיע ללוקח ששני השחקנים מחוברים
                        {
                            gui.getWin().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //לתיקון בעיית חיבור שחקן ראשון וישר הוא יוצא
                            canOpenChat = true; // פותח אפשרות לפתוח את הצאט
                        } else if (msgFromServer.equals("Ilegal Place, Try Again!")) // מודיע שמיקום שהלוקח שם לא טוב
                        {
                            JOptionPane.showMessageDialog(gui.getWin(), "Ilegal Place, Try Again!", "Warning!", JOptionPane.ERROR_MESSAGE);
                        } else {
                            chatArea.append(msgFromServer + "\n");
                            chatArea.setCaretPosition(chatArea.getDocument().getLength());
                            chatWin.setVisible(true);
                            playSound(Utils.MassageReceived);
                        }

                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(gui.getWin(), "Warning!\nThe Server is temporary down.", "Warning!", JOptionPane.WARNING_MESSAGE);
                    System.exit(0);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * פעולה שעושה מהלך של הלקוח לוקחת את המיקום שהשחקן לחץ בו ושולחת לסרבר לבדיקה אם המקום פנוי
     *
     * @param btn
     * @throws Exception
     */
    private void makeMove(JButton btn) throws Exception {
        int row = 0;
        int col = (int) btn.getClientProperty("colNum");
        os.writeObject(row);
        os.writeObject(col);
    }

    /**
     * פעולה שמטפלת בארועים שהשחקן עושה בגרפיקה.
     */
    private void setEventHandler() {
        chatField.addActionListener(new ActionListener() // מאזין לקלט מהשורת טקסט בצאט
        {
            public void actionPerformed(ActionEvent e) {
                try {
                    sendChatMessageToServer(); // שולח הודעה לצאט של המשחק
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        });
        JFrame frame = gui.getWin(); // מאזין לפעולות שיש על לוח המשחק
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent we) {
            }

            @Override
            public void windowClosing(WindowEvent we) {
            }

            @Override
            public void windowClosed(WindowEvent we) {

            }

            @Override
            public void windowIconified(WindowEvent we) {
            }

            @Override
            public void windowDeiconified(WindowEvent we) {
            }

            @Override
            public void windowActivated(WindowEvent we) {
            }

            @Override
            public void windowDeactivated(WindowEvent we) {
            }
        });

        JButton[][] buttons = gui.getButtons();
        for (int row = 0; row < Utils.NUM_ROWS; row++) {
            for (int col = 0; col < Utils.NUM_COLS; col++) {
                buttons[row][col].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            playSound(Utils.clickSound); // משמיע צליל לחיצה
                            makeMove((JButton) e.getSource()); // פעולות שעושה מהלך
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }

        JMenuBar menuBar = gui.getMenuBar(); // מטפל באירועי תפריט
        JMenuItem menu1_2 = menuBar.getMenu(0).getItem(0); // Show Game Rules
        menu1_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String msg = "Connect 4 is a two-player connection game.";
                msg = msg + "\n";
                msg = msg + "The players first choose a color,\nAnd then take turns dropping colored discs from the top into a seven-column,"
                        + "six-row vertically suspended grid.\nThe pieces fall straight down, occupying the next available space within the column.";
                msg = msg + "\n";
                msg += "The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs.";
                JOptionPane.showMessageDialog(gui.getWin(), msg, "Rules", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JMenuItem menu1_3 = menuBar.getMenu(0).getItem(1); // Dev Info
        menu1_3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String msg = "           This Game Writen By Daniel Abay.\n";
                msg = msg + "                 All Right Reserved(C) 2018\n";
                msg += "              <------------------------------------->";
                JOptionPane.showMessageDialog(gui.getWin(), msg, "About Programmer", JOptionPane.DEFAULT_OPTION);
            }
        });
        JMenuItem menu2_1 = menuBar.getMenu(1).getItem(0); // if you want to turn on/off the music.
        menu2_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object[] options
                        = {
                            "OFF", "ON"
                        };
                int n = JOptionPane.showOptionDialog(gui.getWin(), "If you want to play music press 'ON'.\nIf not press 'OFF'.", "Music", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (n == 0) {
                    playBackgroundMusic(Utils.BGMusic, false);
                } else if (n == 1) {
                    playBackgroundMusic(Utils.BGMusic, true);
                }
            }
        });
        JMenuItem menu2_2 = menuBar.getMenu(1).getItem(1); // אם אתה רוצה להראות את הצאט
        menu2_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (canOpenChat) {
                    chatWin.setVisible(true);
                }
            }

        });
        chatWin.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent we) {
            }

            @Override
            public void windowClosing(WindowEvent we) {

            }

            @Override
            public void windowClosed(WindowEvent we) {

            }

            @Override
            public void windowIconified(WindowEvent we) {
            }

            @Override
            public void windowDeiconified(WindowEvent we) {
            }

            @Override
            public void windowActivated(WindowEvent we) {
            }

            @Override
            public void windowDeactivated(WindowEvent we) {
            }
        });
    }

    /**
     * הפעולה בודקת מי השחקן ששיחק ושמה את התמונה שלו על הלוח.
     */
    private void setPlayerImage() {
        if (clientID.equals("Player 1")) {
            playerImage = Utils.yellowIcon;
        } else if (clientID.equals("Player 2")) {
            playerImage = Utils.redIcon;
        }
    }

    /**
     * Method that play's background music.
     *
     * @param sound the music you want to play.
     * @param isOver ture = play music / false = stop music.
     */
    private void playBackgroundMusic(Sound sound, boolean isOver) {
        if (isOver) {
            sound.loop();
        } else {
            sound.stop();
        }
    }

    /**
     * פעולה שמפעילה סאונד במשחק פעם אחת
     *
     * @param sound הצליל
     */
    private void playSound(Sound sound) {
        sound.play();

    }

    /**
     * הפעולה המאתחלת את המשחק
     *
     * @throws Exception
     */
    private void newGame() throws Exception {
        Thread.sleep(3000);
        for (int i = 3; i > -1; i--) {
            gui.setLabelMsgText(">> Starting A New Game, Please Wait: " + i);
            Thread.sleep(1000);
        }
        gui.initGame(Utils.backgroundIcon);
    }

    /**
     * פעולה שבודקת אם המשתמש רשום במערכת בשביל להתחבר למשחק או שהוא אורח
     *
     * @throws Exception
     */
    private void checkClientDetails() throws Exception {
        JFrame frame = new JFrame("Window");
        frame.setAlwaysOnTop(true);
        //=================================================

        Object[] options
                = {
                    "Login",
                    "Guest"
                };
        int choose = JOptionPane.showOptionDialog(frame, "Welcome to Connect 4.\nLogin for registered members only.\nGuest for not registered members.\nby Daniel Abay©",
                "ברוכים הבאים לארבע בשורה", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                Utils.loginIcon, options, options[0]);
        if (choose == 0) {
            JLabel jUserName = new JLabel("User Name");
            JTextField userName = new JTextField();
            userName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, userName.getFont().getSize()));
            JLabel jPassword = new JLabel("Password");
            JTextField password = new JPasswordField();
            Object[] ob
                    = {
                        jUserName, userName, jPassword, password
                    };
            insertIpAndPort();
            clientSetup();
            while (true) {
                int result = JOptionPane.showConfirmDialog(frame, ob, "Login", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.CLOSED_OPTION || result == JOptionPane.CANCEL_OPTION) //if the client is aborting and dont want to play
                {
                    os.writeObject("0");
                    os.writeObject("0");
                    System.exit(0);
                } else if (result == JOptionPane.OK_OPTION) {
                    clientName = userName.getText(); // השם משתמש שהשחקן רשם
                    os.writeObject(clientName);
                    String passwordValue = password.getText(); // הסיסמה שלו
                    os.writeObject(passwordValue);

                    //<<==============================================>>
                    String isHaveAccess = (String) is.readObject();
                    if (isHaveAccess.equals("AccessApprove")) {
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Somthing went worng...\nPlease go back, correct the errors and resubmit your request.", "Warning!", JOptionPane.WARNING_MESSAGE);
                    }
                }

            }
        } else if (choose == 1) {
            insertIpAndPort();
            clientSetup();
            os.writeObject("Guest"); //Username 
            os.writeObject("0"); //Password
            clientName = "Guest";
        } else {
            System.exit(0);
        }
    }
    
    /**
     * פעולה שקולטת את האייפי והפורט של השרת
     */
    private void insertIpAndPort()
    {
        JFrame frame = new JFrame("Window");
        frame.setAlwaysOnTop(true);
        String string = (String) JOptionPane.showInputDialog(frame, "Hello There.\nPlease enter the server ip address\n(default: localhost:1234).",
                    "Server IP", JOptionPane.PLAIN_MESSAGE, null, null, "localhost:1234");
            if ((string != null) && (string.length() > 0) && (string.contains(":"))) 
            {
                String[] parts = string.split(":");
                String ip = parts[0]; //The ip address
                String port = parts[1]; //The port
                serverHost = ip;
                serverPort = Integer.parseInt(port);
            } else {
                if(string != null)
                    JOptionPane.showMessageDialog(null, "Please Add A Port, Thank you.", "Error!", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
    }

    /**
     * פעולה ששולחת את ההודעה לצאט של המשחק
     *
     * @throws Exception
     */
    public void sendChatMessageToServer() throws Exception {
        String msgToServer = chatField.getText();
        if (!msgToServer.isEmpty()) {
            os.writeObject(msgToServer);
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            chatField.setText("");
        }
    }

    //---------------[START]--------------------//
    private void run() throws Exception {
        listenerFromServer();
    }
    //---------------[START]---------------------//
}
