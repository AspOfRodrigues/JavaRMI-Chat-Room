import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IServerChat extends java.rmi.Remote {
    public ArrayList<String> getRooms(); // ArrayList<String>  = roomList
    public void createRoom(String roomName);
    public void registerChatClient(IUserChat chatClient) throws RemoteException;

}

