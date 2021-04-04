package net.advancius;

import net.advancius.commons.Identifier;
import net.advancius.packet.Packet;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AdvanciusClientTest {

    public static void main(String[] arguments) throws InterruptedException, ExecutionException, TimeoutException {
        Identifier identifier = new Identifier("external", "chico");
        String authenticationToken = "MWQwNGQ5ZjgtMDRkNi00MDVlLTgzMDMtY2E2NDY1MzNjYmE0";

        AdvanciusClient client = AdvanciusClient.Builder(identifier, authenticationToken)
                .packetAddress("https://51.79.35.72:8199/packet")
                .socketAddress("wss://51.79.35.72:8199/events")
                .build();

        client.establishPacketConnection().get();
        client.establishSocketConnection().get();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter your stuffies:");
            String person = scanner.nextLine();
            String message = scanner.nextLine();

            Packet packet = Packet.generatePacket("server_chat");
            packet.getMetadata().setMetadata("person", person);
            packet.getMetadata().setMetadata("message", message);
//44651689-c5b1-45c9-bb8f-f8b5547868b3
            client.sendPacket(packet);
        }
    }
}
