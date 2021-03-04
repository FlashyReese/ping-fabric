package me.flashyreese.mods.ping.network.packet;

import me.flashyreese.mods.ping.client.PingClientMod;
import me.flashyreese.mods.ping.client.data.ping.EntityPingWrapper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class HighlightEntityPacket extends PingPacket {
    public static final Identifier IDENTIFIER = new Identifier("ping", "packet.highlight.entity");

    public HighlightEntityPacket(int entityID, int color, int type, String uuid) {
        this.writeInt(entityID);
        this.writeInt(color);
        this.writeInt(type);
        this.writeString(uuid);
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    public static void redistributePing(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        EntityPingWrapper wrapper = EntityPingWrapper.of(packetByteBuf);
        minecraftServer.execute(() -> {
            //No need to pass uuid of self in PingWrapper Fixme:
            for (ServerPlayerEntity playerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(playerEntity, IDENTIFIER, wrapper.getPacketByteBuf());
            }
        });
    }

    public static void processPing(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        EntityPingWrapper wrapper = EntityPingWrapper.of(packetByteBuf);
        client.execute(() -> {
            //No need to pass uuid of self in PingWrapper Fixme:
            PingClientMod.getClientRegistry().getPingHandler().onEntityPingPacket(wrapper);
        });
    }
}
