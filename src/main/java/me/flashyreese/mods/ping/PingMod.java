package me.flashyreese.mods.ping;

import io.netty.buffer.Unpooled;
import me.flashyreese.mods.ping.client.ClientHandler;
import me.flashyreese.mods.ping.client.PingHandler;
import me.flashyreese.mods.ping.data.PingWrapper;
import me.flashyreese.mods.ping.network.PacketHandler;
import me.flashyreese.mods.ping.network.packet.ServerBroadcastPing;
import me.flashyreese.mods.ping.util.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PingMod implements ModInitializer, ClientModInitializer {

    public static final Identifier PING_HIGHLIGHT_ID = new Identifier("ping", "highlight");

    @Override
    public void onInitialize() {
        PacketHandler.initialize();
        ServerSidePacketRegistry.INSTANCE.register(PING_HIGHLIGHT_ID, (packetContext, attachedData) -> {
            PingWrapper wrapper = PingWrapper.readFromBuffer(attachedData);
            packetContext.getTaskQueue().execute(() -> {
                for (ServerPlayerEntity playerEntity : packetContext.getPlayer().world.getServer().getPlayerManager().getPlayerList()) {
                    if (playerEntity != packetContext.getPlayer() && ServerSidePacketRegistry.INSTANCE.canPlayerReceive(playerEntity, PING_HIGHLIGHT_ID)) {
                        if (playerEntity.squaredDistanceTo(playerEntity) <= Config.GENERAL.pingAcceptDistance) {
                            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                            wrapper.writeToBuffer(buf);
                            ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, PING_HIGHLIGHT_ID, buf);
                        }
                    }
                }
            });
        });
    }

    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(PING_HIGHLIGHT_ID,
                (packetContext, attachedData) -> {
                    PingWrapper wrapper = PingWrapper.readFromBuffer(attachedData);
                    packetContext.getTaskQueue().execute(() -> {
                        if (ClientSidePacketRegistry.INSTANCE.canServerReceive(PING_HIGHLIGHT_ID)){
                            PingHandler.INSTANCE.onPingPacket(new ServerBroadcastPing(wrapper));
                        }
                    });
                });
        ClientHandler.registerKeybinds();
    }
}
