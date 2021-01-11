package net.advancius;

import com.google.gson.Gson;
import lombok.Data;
import net.advancius.commons.Identifier;
import net.advancius.connection.PacketConnection;
import net.advancius.connection.SocketConnection;
import net.advancius.packet.Packet;
import net.advancius.packet.PacketHandlerMethod;
import net.advancius.packet.PacketListener;
import net.advancius.packet.PacketResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Data
public class AdvanciusClient {

    public static Gson GSON = new Gson();

    private final Identifier identifier;
    private final String authenticationToken;

    private final String packetAddress;
    private final String socketAddress;

    private PacketConnection packetConnection;
    private SocketConnection socketConnection;

    private List<PacketListener> listenerList = new ArrayList<>();

    public static Builder Builder(Identifier identifier, String authenticationToken) {
        return new Builder(identifier, authenticationToken);
    }

    public CompletableFuture<Boolean> establishPacketConnection() {
        if (hasPacketConnection()) return CompletableFuture.completedFuture(true);

        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        System.out.println("Establishing connection through common pool...");
        new Thread(() -> {
            System.out.println("Attempting to disconnect if already connected.");
            destroyPacketConnection();
            packetConnection = new PacketConnection(this, packetAddress);

            try {
                System.out.println("Establishing packet connection...");
                packetConnection.connect();
                System.out.println("Connected!");
                completableFuture.complete(true);
            } catch (Exception exception) {
                System.err.println("Encountered error connecting.");
                exception.printStackTrace();
                completableFuture.cancel(false);
            }
        }).start();
        return completableFuture;
    }

    public CompletableFuture<Boolean> establishSocketConnection() {
        if (hasSocketConnection()) return CompletableFuture.completedFuture(true);

        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        System.out.println("Establishing connection through common pool...");
        new Thread(() -> {
            System.out.println("Attempting to disconnect if already connected.");
            destroySocketConnection();
            socketConnection = new SocketConnection(this, socketAddress);

            try {
                System.out.println("Establishing socket connection...");
                socketConnection.connect();
                System.out.println("Connected!");
                completableFuture.complete(true);
            } catch (Exception exception) {
                System.err.println("Encountered error connecting.");
                exception.printStackTrace();
                completableFuture.cancel(false);
            }
        }).start();
        return completableFuture;
    }

    public void destroyPacketConnection() {
        if (!hasPacketConnection()) return;
        try {
            packetConnection.disconnect();
            packetConnection = null;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void destroySocketConnection() {
        if (!hasSocketConnection()) return;
        try {
            socketConnection.disconnect();
            socketConnection = null;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean hasPacketConnection() {
        return packetConnection != null && packetConnection.isConnected();
    }

    public boolean hasSocketConnection() {
        return socketConnection != null && socketConnection.isConnected();
    }

    public void handlePacket(Packet packet) {
        for (PacketListener listener : listenerList) {
            PacketHandlerMethod handlerMethod = listener.getHandlerMethod(packet.getType());
            if (handlerMethod == null) continue;

            try {
                handlerMethod.executeMethod(packet);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void registerListener(PacketListener listener) {
        listenerList.add(listener);
    }

    public void unregisterListener(PacketListener listener) {
        listenerList.remove(listener);
    }

    public void sendPacket(Packet packet, Consumer<PacketResponse> responseConsumer) throws InterruptedException, ExecutionException, TimeoutException {
        if (!hasPacketConnection()) {
            throw new IllegalStateException("Packet connection is unestablished.");
        }
        packetConnection.sendPacket(packet, responseConsumer);
    }

    public CompletableFuture<PacketResponse> sendPacket(Packet packet) throws InterruptedException, ExecutionException, TimeoutException {
        return packetConnection.sendPacket(packet);
    }

    public static class Builder {

        private final Identifier identifier;
        private final String authenticationToken;

        private String packetAddress;
        private String socketAddress;

        public Builder(Identifier identifier, String authenticationToken) {
            this.identifier = identifier;
            this.authenticationToken = authenticationToken;
        }

        public Builder packetAddress(String address) {
            this.packetAddress = address;
            return this;
        }

        public Builder socketAddress(String address) {
            this.socketAddress = address;
            return this;
        }

        public AdvanciusClient build() {
            return new AdvanciusClient(identifier, authenticationToken, packetAddress, socketAddress);
        }
    }
}
