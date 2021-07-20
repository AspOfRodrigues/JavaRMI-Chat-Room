import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;


public class UserChat extends UnicastRemoteObject implements IUserChat {
    public String name;
    public IServerChat server;
    public IRoomChat room;
    public boolean IsConnected;
    public boolean ShouldSendMessage;
    private JFrame frame;
    private JTextField messageTextField;
    JButton joinButton;
    JButton refreshButton;
    JButton leaveButton;
    JButton sendButton;
    JScrollPane roomPanel;
    JList swingRoomList;
    JScrollPane messageScrollPanel;
    JTextArea textArea;
    JLabel roomName;
    JLabel userName;

    public void JoinRoomEvent(String roomName) throws MalformedURLException, NotBoundException, RemoteException {
        ArrayList<String> roomList = server.getRooms();
        Scanner scanner = new Scanner(System.in);
        if (roomList.contains(roomName)) {
            String roomUrl = "rmi://localhost:2020/" + roomName;
            room = (IRoomChat) Naming.lookup(roomUrl);
            room.joinRoom(this.name, this);
            System.out.println(name + " has joined");
        }
    }

    public void UpdateRooms() throws RemoteException, MalformedURLException, NotBoundException {
        IServerChat server = (IServerChat) Naming.lookup("rmi://localhost:2020/RMIChatServer");
        ArrayList<String> roomList = server.getRooms();
        DefaultListModel lm = new DefaultListModel();

        for (String roomName : roomList) {
            System.out.println(roomName);
            lm.addElement(roomName);
        }
        swingRoomList.setModel(lm);
    }

    protected UserChat(String name) throws RemoteException, MalformedURLException, NotBoundException {
        super();
        this.name = name;
        this.IsConnected = false;
        this.ShouldSendMessage = false;
        this.server = (IServerChat) Naming.lookup("rmi://localhost:2020/RMIChatServer");


    }

    @Override
    public void deliverMsg(String senderName, String msg) {
        textArea.append(senderName + ":" + msg + "\n");
        System.out.println(senderName + ":" + msg);
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {

        System.out.println("Insert your name:");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();



        UserChat user = new UserChat(name);
        user.initialize();
        if (name.equals("Servidor")) {
            System.out.println("Este é um nome protegido, por favor escolha outro nome");
            scanner.close();
            return;
        }


        scanner.close();
    }

    private void initialize() throws MalformedURLException, NotBoundException, RemoteException {
        frame = new JFrame();
        frame.setBounds(100, 100, 733, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        joinButton = new JButton("Join");
        joinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JoinRoomEvent(swingRoomList.getSelectedValue().toString());
                    textArea.setText("");
                    room.sendMsg(name, "has Joined");
                    roomName.setText(swingRoomList.getSelectedValue().toString());
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                } catch (NotBoundException notBoundException) {
                    notBoundException.printStackTrace();
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        joinButton.setBounds(10, 432, 85, 21);
        frame.getContentPane().add(joinButton);

        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(10, 401, 85, 21);
        frame.getContentPane().add(refreshButton);

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    UpdateRooms();
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                } catch (NotBoundException notBoundException) {
                    notBoundException.printStackTrace();
                }
            }
        });

        leaveButton = new JButton("Leave");
        leaveButton.setBounds(10, 370, 85, 21);
        frame.getContentPane().add(leaveButton);

        leaveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    room.leaveRoom(name);
                    room.sendMsg(name,"quit");
                    textArea.setText("");
                    System.out.println(name + " quit");
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });

        messageTextField = new JTextField();
        messageTextField.setBounds(116, 432, 493, 21);
        frame.getContentPane().add(messageTextField);
        messageTextField.setColumns(10);

        sendButton = new JButton("Send");
        sendButton.setBounds(624, 432, 85, 21);
        frame.getContentPane().add(sendButton);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    room.sendMsg(name, messageTextField.getText());
                    messageTextField.setText("");
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });

        roomPanel = new JScrollPane();
        roomPanel.setBounds(10, 45, 85, 304);
        frame.getContentPane().add(roomPanel);

        swingRoomList = new JList();
        UpdateRooms();
        /*
        swingRoomList.setModel(new AbstractListModel() {
            String[] values = new String[] { "Available Rooms" , "Please Refresh"};
            public int getSize() {
                return values.length;
            }
            public Object getElementAt(int index) {
                return values[index];
            }
        });
         */
        roomPanel.setViewportView(swingRoomList);

        messageScrollPanel = new JScrollPane();
        messageScrollPanel.setBounds(116, 45, 570, 377);
        frame.getContentPane().add(messageScrollPanel);

        textArea = new JTextArea();
        messageScrollPanel.setViewportView(textArea);

        roomName = new JLabel("Room Name");
        roomName.setBounds(600, 10, 249, 21);
        frame.getContentPane().add(roomName);

        userName = new JLabel("Nome");
        userName.setText(name);
        userName.setBounds(10, 10, 166, 21);
        frame.getContentPane().add(userName);
        frame.setVisible(true);
    }

}