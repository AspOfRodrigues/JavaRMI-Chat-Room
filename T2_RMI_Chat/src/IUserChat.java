public interface IUserChat extends java.rmi.Remote {
    public String name = null;
    public void deliverMsg(String senderName, String msg);
}