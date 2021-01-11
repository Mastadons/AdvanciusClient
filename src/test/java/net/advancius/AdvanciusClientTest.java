package net.advancius;

import net.advancius.commons.Identifier;
import net.advancius.packet.Packet;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AdvanciusClientTest {

    public static void main(String[] arguments) throws InterruptedException, ExecutionException, TimeoutException {
        Identifier identifier = new Identifier("external", "test");
        String authenticationToken = "MWQwNGQ5ZjgtMDRkNi00MDVlLTgzMDMtY2E2NDY1MzNjYmE0";

        AdvanciusClient client = AdvanciusClient.Builder(identifier, authenticationToken)
                .packetAddress("https://51.79.35.72:8199/packet")
                .socketAddress("wss://51.79.35.72:8199/events")
                .build();

        client.establishPacketConnection().get();
        client.establishSocketConnection().get();

        Packet packet = Packet.generatePacket("cross_command");
        packet.getMetadata().setMetadata("server", "Bungee");
        packet.getMetadata().setMetadata("command", "alert :OO");
        packet.getMetadata().setMetadata("sender", "my computer");

        client.sendPacket(packet);
    }
}
