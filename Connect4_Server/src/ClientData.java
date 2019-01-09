
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Daniel Abay
 */
public class ClientData extends Socket
{

    private Socket clientSocket; // השקע בין הלקוח לשרת
    private ObjectOutputStream outputStream; // פלט
    private ObjectInputStream inputStream; // קלט
    private String playerNumber; // מספר השחקן
    private String userRealName; // השם האמיתי של השחקן

    public ClientData(Socket clientSocket, String playerName, String userRealName) throws IOException
    {
        this.clientSocket = clientSocket;
        this.playerNumber = playerName;
        this.userRealName = userRealName;

        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        inputStream = new ObjectInputStream(clientSocket.getInputStream());

    }

    public String getName()
    {
        return playerNumber;
    }

    public ObjectInputStream getInputStream()
    {
        return inputStream;
    }

    public ObjectOutputStream getOutputStream()
    {
        return outputStream;
    }

    /**
     * @return את השם האמיתי של השחקן
     */
    public String getUserRealName()
    {
        return userRealName;
    }

    public void setUserRealName(String userRealName)
    {
        this.userRealName = userRealName;
    }

    public String getUserName()
    {
        return playerNumber;
    }

    public void close()
    {
        try
        {
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
