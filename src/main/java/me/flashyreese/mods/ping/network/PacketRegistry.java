package me.flashyreese.mods.ping.network;

import me.flashyreese.mods.ping.network.packet.HighlightBlockPacket;
import me.flashyreese.mods.ping.network.packet.HighlightEntityPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class PacketRegistry {
    @Environment(EnvType.CLIENT)
    public void registerS2CListeners() {
        ClientPlayNetworking.registerGlobalReceiver(HighlightBlockPacket.IDENTIFIER, HighlightBlockPacket::processPing);
        ClientPlayNetworking.registerGlobalReceiver(HighlightEntityPacket.IDENTIFIER, HighlightEntityPacket::processPing);
    }

    public void registerC2SListeners() {
        ServerPlayNetworking.registerGlobalReceiver(HighlightBlockPacket.IDENTIFIER, HighlightBlockPacket::redistributePing);
        ServerPlayNetworking.registerGlobalReceiver(HighlightEntityPacket.IDENTIFIER, HighlightEntityPacket::redistributePing);
    }
}