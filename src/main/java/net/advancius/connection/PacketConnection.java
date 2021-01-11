package net.advancius.connection;

import com.google.gson.Gson;
import lombok.Data;
import net.advancius.AdvanciusClient;
import net.advancius.packet.Packet;
import net.advancius.packet.PacketResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Data
public class PacketConnection {

    private final AdvanciusClient client;

    private final HttpClient connection = new HttpClient(new SslContextFactory(true));
    private final String address;

    public void connect() throws Exception {
        connection.start();
    }

    public void disconnect() {
        try {
            connection.stop();
        } catch (Exception exception) {}
    }

    public boolean isConnected() {
        return connection.isRunning();
    }

    public void sendPacket(Packet packet, Consumer<PacketResponse> callback) throws InterruptedException, ExecutionException, TimeoutException {
        Request request = connection.POST(address);

        request.header(HttpHeader.CONTENT_TYPE, "application/json");
        request.header("Advancius-Authentication-Token", client.getAuthenticationToken());
        request.header("Advancius-Identifier", client.getIdentifier().toString());

        request.content(new StringContentProvider(AdvanciusClient.GSON.toJson(packet)), "application/json");

        ContentResponse response = request.send();

        PacketResponse packetResponse = PacketResponse.fromContentResponse(response);
        if (callback != null) callback.accept(packetResponse);
    }

    public CompletableFuture<PacketResponse> sendPacket(Packet packet) throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<PacketResponse> completableFuture = new CompletableFuture();
        sendPacket(packet, completableFuture::complete);

        return completableFuture;
    }
}
