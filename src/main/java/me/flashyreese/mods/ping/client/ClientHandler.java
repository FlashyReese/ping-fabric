package me.flashyreese.mods.ping.client;

import io.netty.buffer.Unpooled;
import me.flashyreese.mods.ping.PingMod;
import me.flashyreese.mods.ping.data.PingType;
import me.flashyreese.mods.ping.data.PingWrapper;
import me.flashyreese.mods.ping.network.packet.ServerBroadcastPing;
import me.flashyreese.mods.ping.util.Config;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.awt.*;

public class ClientHandler {

    public static void sendPing(PingType type) {
        BlockHitResult raytraceBlock = raytrace(MinecraftClient.getInstance().player, 50);
        if (raytraceBlock.getType() == HitResult.Type.BLOCK) {
            sendPing(raytraceBlock, new Color(Config.VISUAL.pingR, Config.VISUAL.pingG, Config.VISUAL.pingB).getRGB(), type);
        }
    }

    private static void sendPing(BlockHitResult raytrace, int color, PingType type) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        PingWrapper wrapper = new PingWrapper(raytrace.getBlockPos(), color, type);
        wrapper.writeToBuffer(buf);
        ClientSidePacketRegistry.INSTANCE.sendToServer(PingMod.PING_HIGHLIGHT_ID, buf);
        PingHandler.INSTANCE.onPingPacket(new ServerBroadcastPing(wrapper));
    }

    private static BlockHitResult raytrace(PlayerEntity player, double distance) {
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