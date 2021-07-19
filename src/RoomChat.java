import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RoomChat extends UnicastRemoteObject implements IRoomChat {
    private Map<String, IUserChat> userList;
    public String roomName;
    public boolean roomClosed;

    public RoomChat(String roomName) throws RemoteException {
        super();
        this.roomName = roomName;
        this.userList = new HashMap<>();
        roomClosed = false;
    }

    @Override
    public void sendMsg(String usrName, String msg) throws RemoteException {
        if (!roomClosed) {
            for (IUserChat user : userList.values()) {
                user.deliverMsg(usrName, msg);
            }
        }
    }

    @Override
    public void joinRoom(String usrName, IUserChat user) throws RemoteException {
        this.userList.put(usrName, user);
    }

    @Override
    public void leaveRoom(String usrName) throws RemoteException {
        userList.remove(usrName);
    }

    @Override
    public void closeRoom() throws RemoteException {
        this.sendMsg("SERVIDOR", "Sala fechada pelo servidor");
        roomClosed = true;
        try {
            Naming.unbind("rmi://localhost:2020/" + roomName);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getRoomName() throws RemoteException {
        return this.roomName;
    }
}
