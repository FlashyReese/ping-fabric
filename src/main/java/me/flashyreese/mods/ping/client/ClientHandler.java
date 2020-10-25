package me.flashyreese.mods.ping.client;

import me.flashyreese.mods.ping.PingMod;
import me.flashyreese.mods.ping.data.PingType;
import me.flashyreese.mods.ping.data.PingWrapper;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.awt.*;

public class ClientHandler {

    public static void sendPing(PingType type) {
        BlockHitResult raycastResult = raycast(MinecraftClient.getInstance().player, 50);
        if (raycastResult.getType() == HitResult.Type.BLOCK) {
            sendPing(raycastResult, new Color(PingMod.config().VISUAL.pingR, PingMod.config().VISUAL.pingG, PingMod.config().VISUAL.pingB).getRGB(), type);
        }
    }

    private static void sendPing(BlockHitResult raytrace, int color, PingType type) {
        if (ClientSidePacketRegistry.INSTANCE.canServerReceive(PingMod.PING_HIGHLIGHT_ID)) {
            ClientSidePacketRegistry.INSTANCE.sendToServer(PingMod.PING_HIGHLIGHT_ID, new PingWrapper(raytrace.getBlockPos(), color, type).getPacketByteBuf());
        }
    }

    private static BlockHitResult raycast(PlayerEntity player, double distance) {
        float eyeHeight = player.getStandingEyeHeight();
        return (BlockHitResult) player.raycast(distance, eyeHeight, false);
    }

    public static void registerKeybinds() {
        KeyBindingHelper.registerKeyBinding(KeyHandler.KEY_BINDING);
        KeyBindingHelper.registerKeyBinding(KeyHandler.PING_ALERT);
        KeyBindingHelper.registerKeyBinding(KeyHandler.PING_MINE);
        KeyBindingHelper.registerKeyBinding(KeyHandler.PING_LOOK);
        KeyBindingHelper.registerKeyBinding(KeyHandler.PING_GOTO);
    }
}