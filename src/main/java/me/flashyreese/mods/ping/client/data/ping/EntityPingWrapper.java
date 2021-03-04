package me.flashyreese.mods.ping.client.data.ping;

import me.flashyreese.mods.ping.client.data.PingType;
import net.minecraft.network.PacketByteBuf;

public class EntityPingWrapper extends PingWrapper {
    private final int entityID;

    public EntityPingWrapper(int entityID, int color, PingType type, String senderUUID) {
        super(color, type, senderUUID);
        this.entityID = entityID;
        this.writeToBuffer();
    }

    public int getEntityID() {
        return entityID;
    }

    public void writeToBuffer() {
        this.getPacketByteBuf().writeInt(this.entityID);
        this.getPacketByteBuf().writeInt(this.getColor());
        this.getPacketByteBuf().writeInt(this.getType().ordinal());
        this.getPacketByteBuf().writeString(this.getSenderUUID());
    }

    public static EntityPingWrapper of(PacketByteBuf buffer) {
        int entityId = buffer.readInt();
        int color = buffer.readInt();
        PingType type = PingType.values()[buffer.readInt()];
        String uuid = buffer.readString(36);
        return new EntityPingWrapper(entityId, color, type, uuid);
    }
}
