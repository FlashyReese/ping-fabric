package me.flashyreese.mods.ping.client;

import me.flashyreese.mods.ping.client.gui.PingSelectGui;
import me.flashyreese.mods.ping.data.PingType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
    private static final String PING_CATEGORY = "ping:key.categories.ping";
    static final KeyBinding KEY_BINDING = new KeyBinding("key.ping", GLFW.GLFW_KEY_V, PING_CATEGORY);
    static final KeyBinding PING_ALERT = new KeyBinding("ping.key.alert", GLFW.GLFW_KEY_KP_4, PING_CATEGORY);
    static final KeyBinding PING_MINE = new KeyBinding("ping.key.mine", GLFW.GLFW_KEY_KP_5, PING_CATEGORY);
    static final KeyBinding PING_LOOK = new KeyBinding("ping.key.look", GLFW.GLFW_KEY_KP_6, PING_CATEGORY);
    static final KeyBinding PING_GOTO = new KeyBinding("ping.key.goto", GLFW.GLFW_KEY_KP_8, PING_CATEGORY);

    private static boolean lastKeyState = false;
    public static boolean ignoreNextRelease = false;

    public static void onClientTick() {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world == null) {
            return;
        }

        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        boolean keyPressed = (KEY_BINDING.getDefaultKey().getCode() >= 0 ? InputUtil.isKeyPressed(handle, KEY_BINDING.getDefaultKey().getCode()) : InputUtil.isKeyPressed(handle, KEY_BINDING.getDefaultKey().getCode() + 100));

        if (keyPressed != lastKeyState) {
            if (keyPressed) {
                PingSelectGui.activate();
            } else {
                if (!ignoreNextRelease) {
                    final double mouseX = mc.mouse.getX() * ((double) mc.getWindow().getScaledWidth() / mc.getWindow().getWidth());
                    final double mouseY = mc.mouse.getY() * ((double) mc.getWindow().getScaledHeight() / mc.getWindow().getHeight());

                    PingSelectGui.INSTANCE.mouseClicked(mouseX, mouseY, 0);
                }
                ignoreNextRelease = false;
                PingSelectGui.deactivate();
            }
        }
        lastKeyState = keyPressed;

        if (canSendQuickPing(PING_ALERT)) {
            ClientHandler.sendPing(PingType.ALERT);
        } else if (canSendQuickPing(PING_MINE)) {
            ClientHandler.sendPing(PingType.MINE);
        } else if (canSendQuickPing(PING_LOOK)) {
            ClientHandler.sendPing(PingType.LOOK);
        } else if (canSendQuickPing(PING_GOTO)) {
            ClientHandler.sendPing(PingType.GOTO);
        }
    }

    private static boolean canSendQuickPing(KeyBinding keyBinding) {
        return keyBinding.isPressed(); //Will continuously be triggered when key is held down due to vanilla issue.
    }
}