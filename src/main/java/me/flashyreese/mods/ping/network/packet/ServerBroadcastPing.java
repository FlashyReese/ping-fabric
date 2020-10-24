package me.flashyreese.mods.ping.network.packet;

import me.flashyreese.mods.ping.data.PingWrapper;
import net.minecraft.network.PacketByteBuf;

/**
 * Sent from the Server, handled on the Client
 */
public class ServerBroadcastPing {
    public PingWrapper ping;

    public ServerBroadcastPing(PingWrapper ping) {
        this.ping = ping;
    }

    public static void encode(ServerBroadcastPing pingPacket, PacketByteBuf buf) {
        pingPacket.ping.writeToBuffer(buf);
    }

    public static ServerBroadcastPing decode(PacketByteBuf buf) {
        return new ServerBroadcastPing(PingWrapper.readFromBuffer(buf));
    }

    /*public static class Handler {
        public static void handle(ServerBroadcastPing message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> PingHandler.INSTANCE.onPingPacket(message));
            ctx.get().setPacketHandled(true);
        }
    }*/
}