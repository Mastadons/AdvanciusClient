package net.advancius.packet;

import java.lang.reflect.Method;

public interface PacketListener {

    default PacketHandlerMethod getHandlerMethod(String packetType) {
        for (Method declaredMethod : this.getClass().getDeclaredMethods()) {
            if (!isHandlerMethod(declaredMethod)) continue;
            if (!declaredMethod.getAnnotation(PacketHandler.class).packetType().equalsIgnoreCase(packetType)) continue;

            return new PacketHandlerMethod(this, declaredMethod);
        }
        return null;
    }

    static boolean isHandlerMethod(Method method) {
        return method.isAnnotationPresent(PacketHandler.class) && method.getParameterCount() == 1 && method.getParameterTypes()[0] == Packet.class;
    }
}
