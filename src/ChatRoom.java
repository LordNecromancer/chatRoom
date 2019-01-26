import java.util.ArrayList;

/**
 * Created by Sun on 08/10/2018.
 */
public class ChatRoom {
    ArrayList<ClientThread> members = new ArrayList<>();
    String name;

    public ChatRoom(String name) {
        this.name = name;
    }
}
