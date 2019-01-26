
import com.sun.security.ntlm.NTLMException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main implements Serializable {
    static ArrayList<ClientThread> sockets = new ArrayList<>();
    static ArrayList<ChatRoom> chatRooms = new ArrayList<>();

    public static void main(String[] args) throws IOException, NTLMException, ClassNotFoundException {


        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket socket = serverSocket.accept();
            ClientThread serverThread = new ClientThread(socket);
            sockets.add(serverThread);
            serverThread.start();

        }
    }

    public synchronized static void sendToAll(ClientThread client, String msg) {
        if (client.chatRoom != null) {
            for (int i = 0; i < client.chatRoom.members.size(); i++) {
                try {
                    client.chatRoom.members.get(i).dataOutputStream.writeUTF(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Client doesn't have a chat room");
        }
    }

    public static ChatRoom joinChatRoom(ClientThread client, String chatRoomName) throws IOException {
        for (ChatRoom chatRoom : chatRooms) {
            if (chatRoom.name.equals(chatRoomName)) {
                chatRoom.members.add(client);
                return chatRoom;

            }
        }

        client.dataOutputStream.writeUTF("Chat room doesn't exist");
        return null;
    }

    static void getRoomsList(ClientThread client) throws IOException {

        for (int i = 0; i < chatRooms.size(); i++) {
            client.dataOutputStream.writeUTF(chatRooms.get(i).name);
        }

    }
}


