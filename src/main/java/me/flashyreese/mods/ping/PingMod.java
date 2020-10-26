package me.flashyreese.mods.ping;

import me.flashyreese.mods.ping.client.ClientHandler;
import me.flashyreese.mods.ping.client.PingHandler;
import me.flashyreese.mods.ping.network.PacketHandler;
import me.flashyreese.mods.ping.util.PingClientModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import java.io.File;

public class PingMod implements ModInitializer, ClientModInitializer {

    private static PingHandler pingHandler;
    private static ClientHandler clientHandler;
    private static PacketHandler packetHandler;

    private static PingClientModConfig CONFIG;

    @Override
    public void onInitialize() {
        getPacketHandler().initialize();
    }

    @Override
    public void onInitializeClient() {
        getPacketHandler().initializeClient();
        getClientHandler().registerHandlers();
    }

    public static PingHandler getPingHandler() {
        if (pingHandler == null) pingHandler = new PingHandler();
        return pingHandler;
    }

    public static ClientHandler getClientHandler() {
        if (clientHandler == null) clientHandler = new ClientHandler();
        return clientHandler;
    }

    public static PacketHandler getPacketHandler() {
        if (packetHandler == null) packetHandler = new PacketHandler();
        return packetHandler;
    }

    public static PingClientModConfig config() {
        if (CONFIG == null) CONFIG = PingClientModConfig.load(new File("config/ping.json"));
        return CONFIG;
    }
}
