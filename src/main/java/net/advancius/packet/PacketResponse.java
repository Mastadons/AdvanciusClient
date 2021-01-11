package net.advancius.packet;

import com.google.gson.JsonObject;
import lombok.Data;
import net.advancius.AdvanciusClient;
import net.advancius.commons.Metadata;
import org.eclipse.jetty.client.api.ContentResponse;

@Data
public class PacketResponse {

    private final int status;
    private final boolean exception;
    private final Metadata metadata;

    public static PacketResponse fromContentResponse(ContentResponse response) {
        JsonObject responseData = AdvanciusClient.GSON.fromJson(response.getContentAsString(), JsonObject.class);

        Metadata metadata = new Metadata();
        metadata.deserialize(responseData.get("data").getAsJsonObject());

        return new PacketResponse(response.getStatus(), responseData.get("exception").getAsBoolean(), metadata);
    }
}
