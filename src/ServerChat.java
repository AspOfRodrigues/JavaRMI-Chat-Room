import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServerChat extends UnicastRemoteObject implements IServerChat {
    private ArrayList<String> roomList;
    private ArrayList<IUserChat> clientsList;

    protected ServerChat() throws RemoteException {
        roomList = new ArrayList<>();
    }

    // Interface Methods
    @Override
    public ArrayList<String> getRooms() {
        return roomList;
    }

    public void removeRoom(String name){
        roomList.remove(name);
    }

    @Override
    public void createRoom(String roomName) throws RemoteException {
        if (roomList.contains(roomName)) {
            System.out.println("Sala já existe");
        } else {
            roomList.add(roomName);
            try {
                Naming.rebind("rmi://localhost:2020/" + roomName, new RoomChat(roomName));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        LocateRegistry.createRegistry(2020);
        ServerChat server = new ServerChat();
        Naming.rebind("rmi://localhost:2020/RMIChatServer", server);
        Scanner scanner = new Scanner(System.in);

        try {
            server.createRoom("Room1");

            while (true) {
                System.out.println("1 - Criar sala");
                System.out.println("2 - Fechar sala");
                System.out.println("3 - Ver salas");
                String option = scanner.nextLine();

                if (option.equals("1")) {
                    System.out.println("Digite o nome da sala:");
                    String roomName = scanner.nextLine();

                    server.createRoom(roomName);
                } else if (option.equals("2")) {
                    System.out.println("Digite o nome da sala:");
                    String roomName = scanner.nextLine();

                    String roomUrl = "rmi://localhost:2020/" + roomName;

                    IRoomChat room = (IRoomChat) Naming.lookup(roomUrl);

                    room.closeRoom();

                    server.roomList.remove(roomName);
                } else if (option.equals("3")) {
                    System.out.println(server.roomList);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        scanner.close();
    }


}
