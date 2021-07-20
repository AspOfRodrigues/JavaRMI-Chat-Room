import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JFrame frame;
    private JTextField RoomNametextField;
    JScrollPane RoomscrollPane;
    JButton createRoom;
    JButton closeRoom;
    JList RoomList;
    JLabel RoomNameLabel;

    protected ServerChat() throws RemoteException {
        roomList = new ArrayList<>();
    }

    // Interface Methods
    @Override
    public ArrayList<String> getRooms() {
        return roomList;
    }

    public void removeRoom(String name) {
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
        UpdateRooms();

    }

    public void CloseRoom(String roomName, ServerChat server) throws MalformedURLException, NotBoundException, RemoteException {
        System.out.println("Digite o nome da sala:");

        String roomUrl = "rmi://localhost:2020/" + roomName;

        IRoomChat room = (IRoomChat) Naming.lookup(roomUrl);

        room.closeRoom();

        server.roomList.remove(roomName);

        UpdateRooms();
    }

    public void UpdateRooms()
    {
        DefaultListModel lm = new DefaultListModel();

        for (String roomName : roomList) {
            System.out.println(roomName);
            lm.addElement(roomName);
        }
        RoomList.setModel(lm);
    }


    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        LocateRegistry.createRegistry(2020);
        ServerChat server = new ServerChat();
        Naming.rebind("rmi://localhost:2020/RMIChatServer", server);
        Scanner scanner = new Scanner(System.in);
        server.initialize(server);

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

    private void initialize(ServerChat server) throws RemoteException, MalformedURLException {
        frame = new JFrame();
        frame.setBounds(100, 100, 577, 404);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        RoomscrollPane = new JScrollPane();
        RoomscrollPane.setBounds(10, 26, 543, 257);
        frame.getContentPane().add(RoomscrollPane);

        RoomList = new JList();
        RoomscrollPane.setViewportView(RoomList);

        DefaultListModel lm = new DefaultListModel();

        for (String roomName : roomList) {
            System.out.println(roomName);
            lm.addElement(roomName);
        }
        RoomList.setModel(lm);

        createRoom = new JButton("Create");
        createRoom.setBounds(20, 293, 85, 21);
        frame.getContentPane().add(createRoom);

        createRoom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    server.createRoom(RoomNametextField.getText());
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });

        closeRoom = new JButton("Close");
        closeRoom.setBounds(20, 336, 85, 21);
        frame.getContentPane().add(closeRoom);

        closeRoom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    server.CloseRoom(RoomList.getSelectedValue().toString(),server);
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                } catch (NotBoundException notBoundException) {
                    notBoundException.printStackTrace();
                }
            }
        });

        RoomNametextField = new JTextField();
        RoomNametextField.setBounds(129, 294, 197, 20);
        frame.getContentPane().add(RoomNametextField);
        RoomNametextField.setColumns(10);

        RoomNameLabel = new JLabel("Room Name");
        RoomNameLabel.setBounds(336, 297, 112, 13);
        frame.getContentPane().add(RoomNameLabel);
        frame.setVisible(true);
    }

}
