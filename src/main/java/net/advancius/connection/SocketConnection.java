package net.advancius.connection;

import lombok.Data;
import net.advancius.AdvanciusClient;
import net.advancius.packet.Packet;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

@Data
public class SocketConnection {

    private final AdvanciusClient client;

    private final WebSocketClient connection = new WebSocketClient(new SslContextFactory(true));
    private final String address;

    private Socket socket = new Socket(this);

    public void connect() throws Exception {
        connection.start();

        URI destination = new URI(address);

        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setHeader("Advancius-Authentication-Token", client.getAuthenticationToken());
        request.setHeader("Advancius-Identifier", client.getIdentifier().toString());

        connection.connect(socket, destination, request);
        socket.getLatch().await();
    }

    public void disconnect() {
        try {
            if (socket.getSession() != null) socket.getSession().close();
            connection.stop();
        } catch (Exception exception) {}
    }

    public boolean isConnected() {
        return connection.isRunning() && socket.getSession() != null && socket.getSession().isOpen();
    }

    @Data
    @WebSocket
    public static class Socket {

        private volatile Session session;

        private final SocketConnection connection;
        private final CountDownLatch latch = new CountDownLatch(1);

        @OnWebSocketClose
        public void onWebSocketClose(int statusCode, String reason) throws Exception {
            this.session = null;
            System.out.println("Server-side of socket was closed.");
            connection.disconnect();
        }

        @OnWebSocketConnect
        public void onWebSocketConnect(Session session) {
            System.out.println("Connected");
            this.session = session;
            latch.countDown();
        }

        @OnWebSocketError
        public void onWebSocketError(Throwable cause) {}

        @OnWebSocketMessage
        public void onWebSocketText(String message) {
            Packet packet = AdvanciusClient.GSON.fromJson(message, Packet.class);

            connection.getClient().handlePacket(packet);
        }
    }
}
