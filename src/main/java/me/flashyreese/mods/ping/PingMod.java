package me.flashyreese.mods.ping;

import me.flashyreese.mods.ping.network.PacketRegistry;
import net.fabricmc.api.ModInitializer;

public class PingMod implements ModInitializer {
    private static PacketRegistry packetRegistry;

    @Override
    public void onInitialize() {
        getPacketHandler().registerC2SListeners();
    }

    public static PacketRegistry getPacketHandler() {
        if (packetRegistry == null) packetRegistry = new PacketRegistry();
        return packetRegistry;
    }
}
