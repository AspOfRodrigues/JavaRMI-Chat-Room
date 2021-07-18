import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ServerChat extends UnicastRemoteObject implements IServerChat{
    private ArrayList<String> roomList;
    private ArrayList<IUserChat> clientsList;
    private ArrayList<RoomChat> availableRooms;

    protected ServerChat() throws RemoteException {
    }

    public RoomChat getSelectedRoom(int index)
    {
        return availableRooms.get(index);
    }

    // Interface Methods
    @Override
    public ArrayList<String> getRooms() {
        return roomList;
    }

    @Override
    public void createRoom(String roomName) {
        availableRooms.add(new RoomChat(roomName));
        roomList.add(roomName);
    }

    public void registerChatClient(IUserChat chatClient) throws RemoteException
    {
        clientsList.add(chatClient);

    }

    public static void main (String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        Naming.rebind("RMIChatServer", new ServerChat());
        String chatServerURL = "rmi://localhost/RMIChatServer";
        IServerChat serverChat = (IServerChat) Naming.lookup(chatServerURL);
        //pegar sala para criar o client
        new Thread(new UserChat(args[0],serverChat)).start();
    }

}
