package shadenade_week1;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.WebSocket;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChatServer extends WebSocketServer {
    private UserAuthentication userAuth;
    private Connection dbConnection;

    public ChatServer(InetSocketAddress address) {
        super(address);
        this.userAuth = new UserAuthentication();
        this.dbConnection = DatabaseConnection.connect();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message from " + conn.getRemoteSocketAddress() + ": " + message);
        // Here you can handle the message and store it in the database
        storeMessage(1, 2, message); // Example to store a message from user 1 to user 2
        broadcast(message);
    }

    private void storeMessage(int senderId, int receiverId, String message) {
        String insertMessageQuery = "INSERT INTO messages (sender_id, receiver_id, message) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(insertMessageQuery)) {
            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully!");
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(new InetSocketAddress("localhost", 8080));
        server.start();
        System.out.println("Chat server started on port: " + server.getPort());
    }
}



