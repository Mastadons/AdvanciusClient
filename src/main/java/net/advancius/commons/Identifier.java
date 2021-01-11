package net.advancius.commons;

import lombok.Data;

@Data
public class Identifier {

    private final String namespace;
    private final String name;

    public static Identifier fromString(String identifier) {
        String[] components = identifier.split(":");

        if (components.length < 2) throw new IllegalArgumentException("Identifier must be in form namespace:name");

        return new Identifier(components[0], components[1]);
    }

    public boolean isMinecraft() {
        return namespace.equalsIgnoreCase("minecraft");
    }

    @Override
    public String toString() {
        return namespace + ":" + name;
    }
}
