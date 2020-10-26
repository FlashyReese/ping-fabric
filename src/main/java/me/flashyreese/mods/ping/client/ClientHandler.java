package me.flashyreese.mods.ping.client;

import me.flashyreese.mods.ping.PingMod;
import me.flashyreese.mods.ping.client.gui.PingSelectScreen;
import me.flashyreese.mods.ping.data.PingType;
import me.flashyreese.mods.ping.data.PingWrapper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ClientHandler {
    private final String PING_CATEGORY = "ping:key.categories.ping";
    public final KeyBinding KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.ping", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, PING_CATEGORY));
    private final KeyBinding PING_ALERT = KeyBindingHelper.registerKeyBinding(new KeyBinding("ping.key.alert", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_4, PING_CATEGORY));
    private final KeyBinding PING_MINE = KeyBindingHelper.registerKeyBinding(new KeyBinding("ping.key.mine", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_5, PING_CATEGORY));
    private final KeyBinding PING_LOOK = KeyBindingHelper.registerKeyBinding(new KeyBinding("ping.key.look", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_6, PING_CATEGORY));
    private final KeyBinding PING_GOTO = KeyBindingHelper.registerKeyBinding(new KeyBinding("ping.key.goto", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_8, PING_CATEGORY));


    public void sendPing(PingType type) {
        BlockHitResult raycastResult = raycast(MinecraftClient.getInstance().player, 50);
        if (raycastResult.getType() == HitResult.Type.BLOCK) {
            sendPing(raycastResult, new Color(PingMod.config().VISUAL.pingR, PingMod.config().VISUAL.pingG, PingMod.config().VISUAL.pingB).getRGB(), type);
        }
    }

    private void sendPing(BlockHitResult raytrace, int color, PingType type) {
        if (ClientSidePacketRegistry.INSTANCE.canServerReceive(PingMod.getPacketHandler().PING_HIGHLIGHT_ID)) {
            ClientSidePacketRegistry.INSTANCE.sendToServer(PingMod.getPacketHandler().PING_HIGHLIGHT_ID, new PingWrapper(raytrace.getBlockPos(), color, type).getPacketByteBuf());
        }
    }

    private BlockHitResult raycast(PlayerEntity player, double distance) {
        float eyeHeight = player.getStandingEyeHeight();
        return (BlockHitResult) player.raycast(distance, eyeHeight, false);
    }

    public void registerHandlers() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            PingMod.getPingHandler().onClientTick();
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (KEY_BINDING.wasPressed()) {
                if (!(client.currentScreen instanceof PingSelectScreen)) {
                    client.openScreen(new PingSelectScreen());
                }
            }

            if (PING_ALERT.isPressed()) {
                this.sendPing(PingType.ALERT);
            } else if (PING_MINE.isPressed()) {
                this.sendPing(PingType.MINE);
            } else if (PING_LOOK.isPressed()) {
                this.sendPing(PingType.LOOK);
            } else if (PING_GOTO.isPressed()) {
                this.sendPing(PingType.GOTO);
            }
        });
    }
}