import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class UserChat extends UnicastRemoteObject implements IUserChat, Runnable {
    public String name;
    public IServerChat serverChat;
    public IRoomChat roomChat;
    protected UserChat(String name, IServerChat server) throws RemoteException {
        this.name = name;
        this.serverChat = server;
        serverChat.registerChatClient(this);
        enterChatRoom(this);

    }

    public void enterChatRoom(IUserChat user)
    {
        // Fazer gerencia de salas para registrar o client em uma sala da maneira certa
        RoomChat room = new RoomChat("Sample");
        room.joinRoom(this.name, user);
    }

    @Override
    public void deliverMsg(String senderName, String msg) {
        System.out.println(senderName + ":" + msg);
    }

    @Override
    public void run(){
        Scanner scanner = new Scanner(System.in);
        String message;
        while(true)
        {
            message = scanner.nextLine();
            roomChat.sendMsg(name,message);
        }
    }
}
