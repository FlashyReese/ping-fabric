package me.flashyreese.mods.ping.network.packet;

import me.flashyreese.mods.ping.data.PingWrapper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.Supplier;

public class ClientSendPing {
    private PingWrapper ping;

    public ClientSendPing(PingWrapper ping) {
        this.ping = ping;
    }

    public static void encode(ClientSendPing pingPacket, PacketByteBuf buf) {
        pingPacket.ping.writeToBuffer(buf);
    }

    public static ClientSendPing decode(PacketByteBuf buf) {
        return new ClientSendPing(PingWrapper.readFromBuffer(buf));
    }

    public static class Handler {
        /*public static void handle(ClientSendPing message, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayerEntity playerMP = ctx.get().getSender();
            if (playerMP != null && !(playerMP instanceof FakePlayer)) {
                for (PlayerEntity player : playerMP.world.getPlayers()) {
                    if (player instanceof ServerPlayerEntity) {
                        PacketHandler.CHANNEL.sendTo(new ServerBroadcastPing(message.ping), ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
                    }
                }
                ctx.get().setPacketHandled(true);
            }
        }*/
    }
}
