package net.advancius.packet;

import lombok.Data;
import net.advancius.commons.Metadata;

import java.util.UUID;

@Data
public class Packet {

    private final String type;
    private final UUID id;
    private final long timestamp;

    private final Metadata metadata;

    public static Packet generatePacket(String type) {
        return new Packet(type, UUID.randomUUID(), System.currentTimeMillis(), new Metadata());
    }
}
