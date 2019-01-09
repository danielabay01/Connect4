
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Document : Client Created on : 16/04/2018, 14:02:21 Author : Daniel Abay
 */
public class Server implements Serializable
{

    //==============[סטטוסים של המשחק]========//
    private static final int GAME_START = 3; // אם המשחק התחיל
    private static final int GAME_OVER = 4; // אם נגמר המשחק

    //============[משתנים]================//
    private int gameStatus; // הסטטוס שהמשחק נמצא בו
    private int playerTurn; // התור של השחקן במשחק
    private boolean isWin; // אם יש ניצחון במשחק
    private int board[][]; // לוח המשחק
    private ArrayList<ClientData> clientsList; // רשימה של שחקנים שמחוברים לסרבר
    private JTextArea areaUsers; // תיבת טקסט שכל התנועות בסרבר מופיעות
    private JFrame win; //חלון ההודעות של הסרבר 
    private JPanel pnl; // פנל שעל החלון ההודעות בסרבר
    private ServerSocket serverSocket; // השקע בין השרת ללקוח
    private Semaphore sem; // סמפור לחסימת תהליכים
    private final GameLogic logic;
    private String firstPlayerName; // השם של השחקן הראשון שהתחבר
    private int port;
    //============[משתנים]================//

    /**
     * אתחול של המשתנים בסרבר וזימון והפעלת חלון הסרבר
     *
     * @throws IOException
     */
    public Server() throws IOException
    {
        insertPort();
        firstPlayerName = "";
        playerTurn = 0;
        gameStatus = 0;
        isWin = false;
        serverSocket = new ServerSocket(port);   // יצירת השקע של השרת 
        clientsList = new ArrayList<ClientData>();
        logic = new GameLogic();
        board = new int[logic.getNUM_ROWS()][logic.getNUM_COLS()]; // מגדיר את הגודל של לוח המשחק
        sem = new Semaphore(0); // יוצר סמפור חדש בגודל של אפס

        crateGUI(); // GUI Creation
        updateServerGUI(); // מעדכן את המידע על הסרבר
    }

    /**
     * פעולה שמאזינה ומחכה ללקוח שיתחבר למשחק משחק מתחיל רק כאשר שני לקוחות
     * מתחברים והשרת מפסיק להאזין כל עוד שני הלקוחות עדיין מחוברים לסרבר
     * @throws Exception
     */
    public void run() throws Exception
    {
        Socket clientSocket;
        ClientData client;

        //הלולאה אין סופית עד שהשרת נכבה
        while(true)
        {
            if(clientsList.isEmpty()) // כל עוד הרשימה של הלקוחות ריקה אז כנס
            {
                clearBoard(); //מנקה לוח
                clientSocket = serverSocket.accept();
                client = new ClientData(clientSocket, "Player " + 1, ""); // יצירת לקוח חדש
                if(login(client)) // בדיקה אם יש ללקוח הרשאה להיכנס אם לא אז הוא לא נכנס
                {
                    handleClient1(client);
                }
                
                // אם הלקוח הראשון לא הצליח להיכנס אז הוא לא נכנס לחלק הזה וחוזר אחורה
                while(clientsList.size() == 1)
                {
                    clientSocket = serverSocket.accept();
                    client = new ClientData(clientSocket, "Player " + 2, "");
                    if(login(client))
                    {
                        handleClient2(client);
                        playerTurn = 1;
                        gameStatus = GAME_START;
                        serverSocket.close();
                    }
                }
            }
            Thread.sleep(500);
        }
    }

    public static void main(String[] args) throws Exception
    {
        Server server = new Server();
        server.run();
    }

    /**
     * הפעולה מטפלת בלקוח הראשון שהתחבר לשרת בפעולה מתהל כל הקלט והפלט בין השחקן
     * לבין השרת השחקן שולח מיקום שהוא לחץ בלוח והפעולה הנל בודקת אם המיקום הזה
     * פנוי או חוקי ומעדכן את הלוח ובודקת אם יש ניצחון לשחק הנוכחי אם לא אז
     * מחליפים לתור של השחקן השני
     *
     * @param client
     * @throws Exception
     */
    private void handleClient1(final ClientData client) throws Exception
    {
        Thread t;
        t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    client.getOutputStream().writeObject(client.getName()); // Sending the client user name.
                    client.getOutputStream().writeObject("Welcome, Please wait for another player to connect..."); // Sending msg to client.
                    updateServerGUI();
                    sem.acquire(); //Blocking HandleClient1 to countine. (Watinig for Player 2 to connect).
                    broadcastMsg("Two Connected"); // מודיע ששני שחקנים מחוברים ללקוחות

                    areaUsers.append(">>" + client.getUserRealName() + " (" + client.getName() + ")" + " Turn\n"); // מדפיס את התור של השחקן הנוכחי

                    while(gameStatus != GAME_OVER)
                    {
                        System.out.println("");
                        if(playerTurn == 1)
                        {
                            client.getOutputStream().writeObject("Play");
                            client.getOutputStream().flush();
                            Thread.sleep(200);
                        }
                        else
                        {
                            client.getOutputStream().writeObject("Please wait for your turn.");
                        }

                        //=====================<<<>>>========================//    
                        Object msg = client.getInputStream().readObject(); // ממתין לקלט מהלקוח
                        if(msg instanceof String) // אם זה טקסט אז מתייחס לצאט
                        {
                            broadcastMsg(client.getUserRealName() + " (" + client.getUserName() + ") " + ": " + msg);
                        }
                        else if(msg instanceof Integer) // אם זה מספר אז הוא מתייחס ללוח המשחק
                        {
                            int row = (Integer) msg;
                            int col = (Integer) client.getInputStream().readObject();
                            if(logic.isColNotFull(col, board) && logic.isLegalPlace(row, col))
                            {
                                row = logic.getEmptyRow(col, board);
                                updateBoard(row, col, client.getName());
                                for(int i = 0; i < clientsList.size(); i++) // שולח את ההדעות לשני השחקנים(שידור)
                                {
                                    clientsList.get(i).getOutputStream().writeObject("Place is Ok"); // מודיע שהמיקום שהשחקן לחץ בסדר
                                    clientsList.get(i).getOutputStream().writeObject(row);
                                    Thread.sleep(10);
                                    clientsList.get(i).getOutputStream().writeObject(col);
                                    clientsList.get(i).getOutputStream().writeObject(client.getName());
                                    clientsList.get(i).getOutputStream().flush();
                                }
                                areaUsers.append(client.getName() + ": clicked on -> (" + row + "," + col + ")\n");
                                if(isPlayerWin(1) == true)
                                {
                                    broadcastMsg(">> Player Yellow Won! <<");
                                    updateServerGUI();
                                    areaUsers.append("\n                                             >> [GAME OVER] <<\n");
                                    areaUsers.append("-------------------------------------------------------------------------------------------------\n");
                                    areaUsers.append("                                         >> Player Yellow Won! <<\n");
                                    areaUsers.append("-------------------------------------------------------------------------------------------------\n \n");
                                    clearBoard();
                                    clientsList.get(1).getOutputStream().writeObject("Please wait for your turn."); // שולח לשחקן שני שימתין לתור שלו בשמשחק הבא
                                }
                                else if(logic.isTie(board))
                                {
                                    broadcastMsg(">> It's a Tie! <<");
                                    updateServerGUI();
                                    areaUsers.append("-------------------------------------------------------------------------------------------------\n");
                                    areaUsers.append("                                           >> It's a Tie! <<\n");
                                    areaUsers.append("-------------------------------------------------------------------------------------------------\n \n");
                                    clearBoard();
                                }
                                else
                                {
                                    clientsList.get(1).getOutputStream().writeObject("Play"); // שולח לשחקן השני שהוא יכול לשחק
                                    playerTurn = 2; // מחליף את התור של השחקן
                                    areaUsers.append(">>" + clientsList.get(1).getUserRealName() + " (" + clientsList.get(1).getName() + ")" + " Turn\n");
                                    areaUsers.setCaretPosition(areaUsers.getDocument().getLength());
                                }
                            }
                            else
                            {
                                client.getOutputStream().writeObject("Ilegal Place, Try Again!");
                            }
                            Thread.sleep(200);
                        }
                    }
                    client.close(); // מנתק את החיבור עם אותו לקוח
                }
                catch(Exception ex)
                {
                    try
                    {
                        if(clientsList.size() > 1)
                        {
                            clientsList.get(1).getOutputStream().writeObject("Disconnected"); // שולח לשחקן שהשחקן הראשון התנתק
                        }
                        client.close(); // מנתק את החיבור עם אותו לקוח
                        clientsList.removeAll(clientsList); // מסיר את כל השחקנים שברישמת שחקנים
                        firstPlayerName = "";
                        updateServerGUI();
                        areaUsers.append("Wating for players..");
                        if(serverSocket.isClosed())
                            serverSocket = new ServerSocket(port);   // יצירת השקע של השרת 
                    }
                    catch(Exception ex1)
                    {
                        ex1.printStackTrace();
                    }
                }
            }
        }
        );
        t.start();
    }

    /**
     * הפעולה מטפלת בלקוח שני שהתחבר לשרת בפעולה מתהל כל הקלט והפלט בין השחקן
     * לבין השרת השחקן שולח מיקום שהוא לחץ בלוח והפעולה הנל בודקת אם המיקום הזה
     * פנוי או חוקי ומעדכן את הלוח ובודקת אם יש ניצחון לשחק הנוכחי אם לא אז
     * מחליפים לתור של השחקן השני
     *
     * @param client
     * @throws Exception
     */
    private void handleClient2(final ClientData client) throws Exception
    {
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    client.getOutputStream().writeObject(client.getName()); // Sending the client user name.
                    client.getOutputStream().writeObject("Welcome, Please wait for another player to connect...");
                    updateServerGUI();
                    sem.release(); //relesing the block form HandleClient1.
                    Thread.sleep(500); //Wating for other brodcasts to finsh (don't want to interrupt them).

                    while(gameStatus != GAME_OVER)
                    {
                        if(playerTurn == 2)
                        {
                            client.getOutputStream().writeObject("Play");
                            client.getOutputStream().flush();
                            Thread.sleep(200);
                        }
                        else
                        {
                            client.getOutputStream().writeObject("Please wait for your turn.");
                        }

                        //=====================<<<>>>========================//    
                        Object msg = client.getInputStream().readObject(); // ממתין לקלט מהלקוח
                        if(msg instanceof String) // אם זה טקסט אז מתייחס לצאט
                        {
                            broadcastMsg(client.getUserRealName() + " (" + client.getUserName() + ") " + ": " + msg);
                        }
                        else if(msg instanceof Integer) // אם זה מספר אז הוא מתייחס ללוח המשחק
                        {
                            int row = (Integer) msg;
                            int col = (Integer) client.getInputStream().readObject();
                            if(logic.isColNotFull(col, board) && logic.isLegalPlace(row, col))
                            {
                                row = logic.getEmptyRow(col, board);
                                updateBoard(row, col, client.getName());
                                for(int i = 0; i < clientsList.size(); i++) // שולח את ההדעות לשני השחקנים(שידור)
                                {
                                    clientsList.get(i).getOutputStream().writeObject("Place is Ok"); // מודיע שהמיקום שהשחקן לחץ בסדר
                                    clientsList.get(i).getOutputStream().writeObject(row);
                                    Thread.sleep(10);
                                    clientsList.get(i).getOutputStream().writeObject(col);
                                    clientsList.get(i).getOutputStream().writeObject(client.getName());
                                    clientsList.get(i).getOutputStream().flush();
                                }
                                areaUsers.append(client.getName() + ": clicked on -> (" + row + "," + col + ")\n");
                                if(isPlayerWin(2) == true)
                                {
                                    broadcastMsg(">> Player Red Won! <<");
                                    updateServerGUI();
                                    areaUsers.append("\n                                             >> [GAME OVER] <<\n");
                                    areaUsers.append("-------------------------------------------------------------------------------------------------\n");
                                    areaUsers.append("                                            >> Player Red Won! <<\n");
                                    areaUsers.append("-------------------------------------------------------------------------------------------------\n \n");
                                    clearBoard();
                                    clientsList.get(0).getOutputStream().writeObject("Please wait for your turn."); // שולח לשחקן הראשון שימתין לתור שלו בשמשחק הבא
                                }
                                else if(logic.isTie(board))
                                {
                                    broadcastMsg(">> It's a Tie! <<");
                                    updateServerGUI();
                                    areaUsers.append("-------------------------------------------------------------------------------------------------\n");
                                    areaUsers.append("                                           >> It's a Tie! <<\n");
                                    areaUsers.append("-------------------------------------------------------------------------------------------------\n \n");
                                    clearBoard();
                                }
                                else
                                {
                                    clientsList.get(0).getOutputStream().writeObject("Play");
                                    playerTurn = 1;
                                    areaUsers.append(">>" + clientsList.get(0).getUserRealName() + " (" + clientsList.get(0).getName() + ")" + " Turn\n");
                                    areaUsers.setCaretPosition(areaUsers.getDocument().getLength());
                                }
                            }
                            else
                            {
                                client.getOutputStream().writeObject("Ilegal Place, Try Again!");
                            }
                            Thread.sleep(200);
                        }
                    }
                    client.close(); // מנתק את החיבור עם אותו לקוח
                }
                catch(Exception ex)
                {
                    try
                    {
                        if(clientsList.size() > 1)
                        {
                            clientsList.get(0).getOutputStream().writeObject("Disconnected"); // שולח לשחקן שהשחקן הראשון התנתק
                        }
                        client.close(); // מנתק את החיבור עם אותו לקוח
                        clientsList.removeAll(clientsList); // מסיר את כל השחקנים שברישמת שחקנים
                        firstPlayerName = "";
                        updateServerGUI();
                        areaUsers.append("Wating for players..");
                        if(serverSocket.isClosed())
                            serverSocket = new ServerSocket(port);   // יצירת השקע של השרת 
                    }
                    catch(Exception ex1)
                    {
                        ex1.printStackTrace();
                    }
                }
            }
        }
        );
        t.start();
    }

    /**
     * פעולה שמעדכת את החלון של הסרבר שמופיע בו כל הנתונים עדכנים של הסרבר
     * @throws UnknownHostException
     */
    private void updateServerGUI() throws UnknownHostException
    {
        areaUsers.setText(""); // מנקה את החלון הסרבר
        printLogo(); // מדפיס את הלוגו של הסרבר
        String serverIP = InetAddress.getLocalHost().getHostAddress(); // מקבל את האייפי של השרת
        areaUsers.append("> IP Address: " + serverIP + "\n");
        areaUsers.append("> Online Users: " + "\n");
        areaUsers.append("- - - - - - - - - - - - - - - - - -\n");
        if(!clientsList.isEmpty()) // כל עוד הרשימה של הלקוחות לא ריקה
        {
            // תדפיס את כל הלקוחות המחוברים
            for(int i = 0; i < clientsList.size(); i++)
            {
                areaUsers.append("[" + clientsList.get(i).getUserRealName() + " Connected] |\n");
                if(clientsList.size() < 2) // אם עדיין לא התחברו שני לקוחות
                {
                    areaUsers.append("Wating for another player..\n");
                }

                if(clientsList.size() == 2)
                {
                    areaUsers.append("- - - - - - - - - - - - - - - - - -\n");
                }
            }
        }
    }

    /**
     * פעולות שמדפיסה לוגו של המשחק בחלון של הסרבר.
     */
    private void printLogo()
    {
        areaUsers.append(" _____                             _      __                 \n");
        areaUsers.append("/  __ \\                           | |    / _|                \n");
        areaUsers.append("| /  \\/ ___  _ __  _ __   ___  ___| |_  | |_ ___  _   _ _ __ \n");
        areaUsers.append("| |    / _ \\| '_ \\| '_ \\ / _ \\/ __| __| |  _/ _ \\| | | | '__|\n");
        areaUsers.append("| \\__/\\ (_) | | | | | | |  __/ (__| |_  | || (_) | |_| | |   \n");
        areaUsers.append(" \\____/\\___/|_| |_|_| |_|\\___|\\___|\\__| |_| \\___/ \\__,_|_| \nBy @Daniel Abay \n\n");
    }

    /**
     * פעולה שיוצרת את חלון הסרבר.
     */
    private void crateGUI()
    {
        areaUsers = new JTextArea();
        pnl = new JPanel(new BorderLayout());
        win = new JFrame("Server");

        win.setSize(450, 330);
        win.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        win.setAlwaysOnTop(true);
        win.setBackground(new java.awt.Color(0, 0, 0));
        win.setForeground(new java.awt.Color(51, 255, 255));
        win.setResizable(false);
        win.setLocationRelativeTo(null);

        areaUsers.setEditable(false);
        areaUsers.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaUsers.setFont(areaUsers.getFont().deriveFont(Font.BOLD));
        areaUsers.setForeground(Color.WHITE);
        areaUsers.setBackground(Color.BLACK);

        pnl.add(areaUsers, BorderLayout.NORTH);
        pnl.add(new JScrollPane(areaUsers), BorderLayout.CENTER);
        win.add(pnl, BorderLayout.CENTER);

        pnl.setVisible(true);
        win.setVisible(true);
    }

    /**
     * פעולה שמנקה את לוח המשחק.
     */
    private void clearBoard()
    {
        for(int row = 0; row < logic.getNUM_ROWS(); row++)
        {
            for(int col = 0; col < logic.getNUM_COLS(); col++)
            {
                board[row][col] = 0;
            }

        }
    }

    /**
     * פעולה שמעדכנת את הלוח אחרי ששחקן עשה מהלך.
     * @param row השורה
     * @param col העמודה
     * @param playerName השחקן שעשה את המהלך
     */
    private void updateBoard(int row, int col, String playerName)
    {
        for(int i = 0; i < logic.getNUM_ROWS(); i++)
        {
            for(int k = 0; k < logic.getNUM_COLS(); k++)
            {
                if(i == row && k == col)
                {
                    if(playerName.equals("Player 1"))
                    {
                        board[i][k] = 1;
                    }
                    else if(playerName.equals("Player 2"))
                    {
                        board[i][k] = 2;
                    }
                }
            }

        }
    }

    /**
     * פעולה שבודקת אם שחקן ששיחק ניצח במשחק.
     * @param player השחקן
     * @return אם ניצח או לא
     */
    private boolean isPlayerWin(int player)
    {
        isWin = logic.isPlayerWin(board, player);
        if(isWin == true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * פעולה שעושה שידור לכל הלקוחות המחוברים לסרבר.
     * @param msg ההודעה
     * @throws Exception
     */
    private void broadcastMsg(Object msg) throws Exception
    {
        for(int i = 0; i < clientsList.size(); i++)
        {
            clientsList.get(i).getOutputStream().writeObject(msg);
        }
    }

    /**
     * פעולה שבודקת התחברות של שחקן אם הוא רושם במעכרת או שהוא אורח.
     * @param client הלקוח
     * @return אם הוא יכול להיכנס למשחק או לא
     * @throws Exception
     */
    private boolean login(ClientData client) throws Exception
    {
        boolean isHaveAccess = false; // משתנה אם לשחקן יש אפשרות להיכנס למשחק
        while(true)
        {
            String userName = (String) client.getInputStream().readObject(); // מקבל את שם המשתמש מהלקוח
            String password = (String) client.getInputStream().readObject(); // מקבל סיסמה מהלקוח
            if(userName.equals("0") && password.equals("0")) // במקרה שהשחקן מחליט שהוא בסוף לא רוצה לשחק
            {
                break;
            }
            else if(userName.equals("Guest") && password.equals("0")) // אם השחקן רוצה להיכנס בתור אורח
            {
                client.setUserRealName(userName);
                clientsList.add(client);
                return true;
            }
            else
            {
                isHaveAccess = checkClientDeatils(userName, password); // בודק אם שם משתמש וסיסמה נכונים
                if(isHaveAccess)
                {
                    client.setUserRealName(userName);
                    clientsList.add(client);
                    client.getOutputStream().writeObject("AccessApprove");
                    firstPlayerName = userName;
                    return true;
                }
                else
                {
                    client.getOutputStream().writeObject("AccessDeined");
                }
            }
        }
        return false;
    }

    /**
     * פעולה שבודקת אם השחקן נמצא במסד הנתונים של הסרבר אם הוא אורח או רשום היא
     * יכנס.
     * @param userName השם משתמש
     * @param password הסיסמה
     * @return אם הוא רשום במעכרת אז הוא יכול להיכנס ואם לא אז הוא לא יכול
     * @throws SQLException
     */
    private boolean checkClientDeatils(String userName, String password) throws SQLException
    {
        Connection con;
        Statement st;
        ResultSet rs;

        String canPlay = "1";
        con = UtilsDB.getDBConnection("mydb.accdb");
        st = con.createStatement();

        String sqlQuery = "select * from users where un='" + userName + "' AND pw='" + password + "' AND canplay='" + canPlay + "'";
        
        rs = st.executeQuery(sqlQuery);
        if(rs.next())
        {
            if(userName.equals(firstPlayerName))
                return false;
            
            else if(canPlay.equals("0"))
                return false;
            
             con.close(); //סוגר קישור עם מוסד נתונים
             return true;
        }
        else
        {
            con.close();
            return false;
        }
    }
    
    /**
     * פעולה שקולטת את האייפי והפורט של השרת
     */
    private void insertPort()
    {
        JFrame frame = new JFrame("Window");
        frame.setAlwaysOnTop(true);
        String string = (String) JOptionPane.showInputDialog(frame, "Hello There.\nPlease enter the server port.\n(example: 1234).",
                    "Server Port", JOptionPane.PLAIN_MESSAGE, null, null, "");
            if ((string != null) && string.length() > 0) 
            {
                port = (Integer.parseInt(string));
            } else {
                if(string != null)
                    JOptionPane.showMessageDialog(null, "Please Add A Port, Thank you.", "Error!", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
    }
}
