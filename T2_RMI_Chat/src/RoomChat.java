import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RoomChat implements IRoomChat{
    private Map<String, IUserChat> userList;
    public String roomName;

    public RoomChat(String roomName)
    {
        this.roomName = roomName;
    }


    @Override
    public void sendMsg(String usrName, String msg) {
        for (IUserChat user:userList.values()) {
            user.deliverMsg(usrName ,msg);
        }
    }

    @Override
    public void joinRoom(String usrName, IUserChat user) {
        this.userList.put(usrName,user);
    }

    @Override
    public void leaveRoom(String usrName) {
        userList.remove(usrName);
    }

    @Override
    public void closeRoom() {
        if(userList.isEmpty())
        {
            // nao sei
        }
    }

    @Override
    public String getRoomName() {
        return this.roomName;
    }
}
