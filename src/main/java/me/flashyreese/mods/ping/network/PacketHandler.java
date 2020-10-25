package me.flashyreese.mods.ping.network;

import me.flashyreese.mods.ping.PingMod;
import me.flashyreese.mods.ping.client.PingHandler;
import me.flashyreese.mods.ping.data.PingWrapper;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class PacketHandler {

    public static void initialize() {
        ServerSidePacketRegistry.INSTANCE.register(PingMod.PING_HIGHLIGHT_ID, PacketHandler::redistributePing);
    }

    public static void initializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(PingMod.PING_HIGHLIGHT_ID, PacketHandler::processPing);
    }

    public static void redistributePing(PacketContext packetContext, PacketByteBuf attachedData) {
        PingWrapper wrapper = PingWrapper.of(attachedData);
        packetContext.getTaskQueue().execute(() -> {
            for (ServerPlayerEntity playerEntity : packetContext.getPlayer().world.getServer().getPlayerManager().getPlayerList()) {
                if (ServerSidePacketRegistry.INSTANCE.canPlayerReceive(playerEntity, PingMod.PING_HIGHLIGHT_ID)) {
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, PingMod.PING_HIGHLIGHT_ID, wrapper.getPacketByteBuf());
                }
            }
        });
    }

    public static void processPing(PacketContext packetContext, PacketByteBuf attachedData) {
        PingWrapper wrapper = PingWrapper.of(attachedData);
        packetContext.getTaskQueue().execute(() -> {
            if (ClientSidePacketRegistry.INSTANCE.canServerReceive(PingMod.PING_HIGHLIGHT_ID)) {
                PingHandler.INSTANCE.onPingPacket(wrapper);
            }
        });
    }
}