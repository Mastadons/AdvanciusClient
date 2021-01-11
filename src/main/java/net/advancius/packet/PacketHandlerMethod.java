package net.advancius.packet;

import lombok.Data;
import net.advancius.commons.Reflection;

import java.lang.reflect.Method;

@Data
public class PacketHandlerMethod {

    private final PacketListener listener;
    private final Method method;

    public void executeMethod(Packet packet) {
        if (!isMatchingPacket(packet)) return;

        Reflection.runMethod(method, listener, packet);
    }

    private boolean isMatchingPacket(Packet packet) {
        return method.getParameterTypes()[0].isAssignableFrom(packet.getClass());
    }
}
