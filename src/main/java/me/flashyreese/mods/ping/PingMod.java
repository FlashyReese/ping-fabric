package me.flashyreese.mods.ping;

import me.flashyreese.mods.ping.client.ClientHandler;
import me.flashyreese.mods.ping.network.PacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class PingMod implements ModInitializer, ClientModInitializer {

    @Override
    public void onInitialize() {
        PacketHandler.initialize();
    }

    @Override
    public void onInitializeClient() {
        ClientHandler.registerKeybinds();
    }
}
