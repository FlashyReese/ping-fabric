package me.flashyreese.mods.ping;

import me.flashyreese.mods.ping.client.ClientHandler;
import me.flashyreese.mods.ping.network.PacketHandler;
import me.flashyreese.mods.ping.util.PingClientModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import java.io.File;

public class PingMod implements ModInitializer, ClientModInitializer {

    private static PingClientModConfig CONFIG;
    public static final Identifier PING_HIGHLIGHT_ID = new Identifier("ping", "highlight");

    @Override
    public void onInitialize() {
        PacketHandler.initialize();
    }

    @Override
    public void onInitializeClient() {
        PacketHandler.initializeClient();
        ClientHandler.registerKeybinds();
    }

    public static PingClientModConfig config() {
        if (CONFIG == null) {
            CONFIG = PingClientModConfig.load(new File("config/ping.json"));
        }

        return CONFIG;
    }
}
