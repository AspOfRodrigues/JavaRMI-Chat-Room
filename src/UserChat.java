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
    public ArrayList<String> roomList;
    public boolean IsConnected;
    public boolean ShouldSendMessage;
    private JFrame frame;
    private JTextField messageTextField;
    private JTextField roomNameCreation;
    JButton joinButton;
    JButton refreshButton;
    JButton leaveButton;
    JButton sendButton;
    JButton createButton;
    JScrollPane roomPanel;
    JList swingRoomList;
    JScrollPane messageScrollPanel;
    JTextArea textArea;
    JLabel roomName;
    JLabel userName;

    public void JoinRoomEvent(String roomName) throws MalformedURLException, NotBoundException, RemoteException {
        Scanner scanner = new Scanner(System.in);
        if (roomList.contains(roomName)) {
            String roomUrl = "rmi://localhost:2020/" + roomName;
            room = (IRoomChat) Naming.lookup(roomUrl);
            room.joinRoom(this.name, this);
            System.out.println(name + " has joined");
        }
    }

    public void UpdateRooms() throws RemoteException, MalformedURLException, NotBoundException {
        roomList = server.getRooms();
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
        this.server = (IServerChat) Naming.lookup("rmi://localhost:2020/Servidor");


    }

    @Override
    public void deliverMsg(String senderName, String msg) throws RemoteException {
        textArea.append(senderName + ":" + msg + "\n");
        
        if(senderName.equals("SERVIDOR") && msg.equals("Sala fechada pelo servidor")){
            roomList.remove(room.getRoomName());
            room = null;
            roomName.setText("");

            DefaultListModel lm = new DefaultListModel();
            for (String roomName : roomList) {
                System.out.println(roomName);
                lm.addElement(roomName);
            }
            swingRoomList.setModel(lm);
        }
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("Insert your name:");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();

        if (name.equals("SERVIDOR")) {
            System.out.println("Este é um nome protegido, por favor escolha outro nome");
            scanner.close();
            return;
        }

        UserChat user = new UserChat(name);
        user.initialize();

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
                    if(swingRoomList.getSelectedValue() == null) return;
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
                    if(room == null) return;
                    room.leaveRoom(name);
                    room.sendMsg(name,"quit");
                    textArea.setText("");
                    room = null;
                    roomName.setText("Room Name");
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
                    if(messageTextField.getText().isEmpty() || room == null) return;
                    room.sendMsg(name, messageTextField.getText());
                    messageTextField.setText("");
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });

        roomPanel = new JScrollPane();
        roomPanel.setBounds(10, 45, 85, 240);
        frame.getContentPane().add(roomPanel);

        swingRoomList = new JList();
        UpdateRooms();
        roomPanel.setViewportView(swingRoomList);

        roomNameCreation = new JTextField();
        roomNameCreation.setBounds(10, 290, 85, 21);
        frame.getContentPane().add(roomNameCreation);
        roomNameCreation.setColumns(10);

        createButton = new JButton("Request creation");
        createButton.setBounds(10, 312, 85, 21);
        frame.getContentPane().add(createButton);

        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String createRoomName = roomNameCreation.getText();
                    if(createRoomName.isEmpty()) return;
                    server.createRoom(createRoomName);
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