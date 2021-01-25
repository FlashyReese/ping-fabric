package me.flashyreese.mods.ping.network;

import me.flashyreese.mods.ping.client.PingClientMod;
import me.flashyreese.mods.ping.client.data.PingWrapper;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PacketRegistry {
    public final Identifier PING_HIGHLIGHT_ID = new Identifier("ping", "highlight");

    public void registerPacket() {
        ServerSidePacketRegistry.INSTANCE.register(PING_HIGHLIGHT_ID, this::redistributePing);
    }

    public void registerClientPacket() {
        ClientSidePacketRegistry.INSTANCE.register(PING_HIGHLIGHT_ID, this::processPing);
    }

    public void redistributePing(PacketContext packetContext, PacketByteBuf attachedData) {
        PingWrapper wrapper = PingWrapper.of(attachedData, packetContext.getPlayer().getUuidAsString());
        packetContext.getTaskQueue().execute(() -> {
            for (ServerPlayerEntity playerEntity : packetContext.getPlayer().world.getServer().getPlayerManager().getPlayerList()) {
                if (ServerSidePacketRegistry.INSTANCE.canPlayerReceive(playerEntity, PING_HIGHLIGHT_ID)) {
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, PING_HIGHLIGHT_ID, wrapper.getPacketByteBuf());
                }
            }
        });
    }

    public void processPing(PacketContext packetContext, PacketByteBuf attachedData) {
        PingWrapper wrapper = PingWrapper.of(attachedData, packetContext.getPlayer().getUuidAsString());
        packetContext.getTaskQueue().execute(() -> {
            if (ClientSidePacketRegistry.INSTANCE.canServerReceive(PING_HIGHLIGHT_ID)) {
                PingClientMod.getClientRegistry().getPingHandler().onPingPacket(wrapper);
            }
        });
    }
}