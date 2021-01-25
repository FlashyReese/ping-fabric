package me.flashyreese.mods.ping.client;

import me.flashyreese.mods.ping.PingMod;
import me.flashyreese.mods.ping.client.config.PingClientConfig;
import me.flashyreese.mods.ping.client.registry.ClientRegistry;
import net.fabricmc.api.ClientModInitializer;

import java.io.File;

public class PingClientMod implements ClientModInitializer {
    private static ClientRegistry clientRegistry;

    private static PingClientConfig CONFIG;

    @Override
    public void onInitializeClient() {
        PingMod.getPacketHandler().registerClientPacket();
        getClientRegistry().registerEvents();
    }

    public static ClientRegistry getClientRegistry() {
        if (clientRegistry == null) clientRegistry = new ClientRegistry();
        return clientRegistry;
    }

    public static PingClientConfig config() {
        if (CONFIG == null) CONFIG = PingClientConfig.load(new File("config/ping.json"));
        return CONFIG;
    }
}
