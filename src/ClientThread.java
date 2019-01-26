import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket socket;
    private DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    private boolean justJoinedChatRoom = false;
    private String name = "Anonymous";
    ChatRoom chatRoom;

    public ClientThread(Socket socket) throws IOException {
        this.socket = socket;
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }


    @Override
    public void run() {

        String line;
        while (true) {
            try {
                line = dataInputStream.readUTF();


                if (line.startsWith("@")) {
                    joinRoom(line);
                } else if (line.startsWith("%name%")) {
                    name = line.substring(6);
                } else if (line.equals("getRoomsList$")) {
                    Main.getRoomsList(this);
                } else if (line.startsWith("#")) {
                    createRoom(line);

                } else {
                    handlingUserMassages(line);
                }


            } catch (IOException ignored) {
                try {
                    closeConnection();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private void handlingUserMassages(String line) throws IOException {
        if (chatRoom != null) {
            if (!justJoinedChatRoom) {
                justJoinedChatRoom = true;
                this.dataOutputStream.writeUTF("JoinedChatRoom$");
                Main.sendToAll(this, "Welcome " + name + "\r\n");
            } else {


                Main.sendToAll(this, name + "  :  " + line);
            }
        } else {


            this.dataOutputStream.writeUTF("NoSuchRoom$");
        }
    }

    private void joinRoom(String line) throws IOException {
        chatRoom = Main.joinChatRoom(this, line);
        if (chatRoom != null) {
            this.dataOutputStream.writeUTF("JoinedChatRoom$");
            Main.sendToAll(this, "Welcome " + name + "\r\n");
            justJoinedChatRoom = true;

        }
    }

    private void createRoom(String line) throws IOException {
        ChatRoom newChatRoom = new ChatRoom(line.substring(1));
        chatRoom = newChatRoom;
        Main.chatRooms.add(newChatRoom);
        Main.joinChatRoom(this, line.substring(1));
        this.dataOutputStream.writeUTF("JoinedChatRoom$");
        Main.sendToAll(this, "Welcome " + name + "\r\n");
        justJoinedChatRoom = true;
    }

    private void closeConnection() throws IOException {

        dataInputStream.close();
        dataOutputStream.close();
        socket.close();
        Main.sockets.remove(this);
    }


}

