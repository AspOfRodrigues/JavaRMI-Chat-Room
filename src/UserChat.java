import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class UserChat extends UnicastRemoteObject implements IUserChat {
    public String name;

    protected UserChat(String name) throws RemoteException {
        super();
        this.name = name;
    }

    @Override
    public void deliverMsg(String senderName, String msg) {
        System.out.println(senderName + ":" + msg);
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        if (name.equals("Servidor")) {
            System.out.println("Este é um nome protegido, por favor escolha outro nome");
            scanner.close();
            return;
        }

        try {
            UserChat user = new UserChat(name);

            IServerChat server = (IServerChat) Naming.lookup("rmi://localhost:2020/RMIChatServer");
            while (true) {
                ArrayList<String> roomList = server.getRooms();
                System.out.println(roomList);
                System.out.println("1 - Atualizar lista de salas");
                System.out.println("2 - Entrar em uma sala");

                String option = scanner.nextLine();

                if (option.equals("2")) {
                    System.out.println("Digite o nome da sala:");
                    String roomName = scanner.nextLine();

                    roomList = server.getRooms();
                    if (roomList.contains(roomName)) {
                        String roomUrl = "rmi://localhost:2020/" + roomName;
                        IRoomChat room = (IRoomChat) Naming.lookup(roomUrl);
                        room.joinRoom(user.name, user);

//                        user.roomIsOpened = true;
                        while (true) {
                            String msg = scanner.nextLine();
                            if (msg.equals("\\quit")) {
                                room.leaveRoom(user.name);
                                break;
                            }
                            room.sendMsg(user.name, msg);
                        }
                    } else {
                        server.createRoom(roomName);
                        System.out.println("Sala inexistente, solicitando criacao...");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        scanner.close();
    }
}
