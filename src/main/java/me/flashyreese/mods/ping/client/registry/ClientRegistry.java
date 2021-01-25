package me.flashyreese.mods.ping.client.registry;

import me.flashyreese.mods.ping.client.data.PingHandler;
import me.flashyreese.mods.ping.client.gui.PingSelectScreen;
import me.flashyreese.mods.ping.client.data.PingType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ClientRegistry {
    private final String PING_CATEGORY = "ping:key.categories.ping";
    public final KeyBinding KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.ping", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, PING_CATEGORY));
    private final KeyBinding PING_ALERT = KeyBindingHelper.registerKeyBinding(new KeyBinding("ping.key.alert", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_4, PING_CATEGORY));
    private final KeyBinding PING_BREAK = KeyBindingHelper.registerKeyBinding(new KeyBinding("ping.key.break", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_5, PING_CATEGORY));
    private final KeyBinding PING_LOOK = KeyBindingHelper.registerKeyBinding(new KeyBinding("ping.key.look", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_6, PING_CATEGORY));
    private final KeyBinding PING_GOTO = KeyBindingHelper.registerKeyBinding(new KeyBinding("ping.key.goto", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_7, PING_CATEGORY));
    private final KeyBinding PING_ATTACK = KeyBindingHelper.registerKeyBinding(new KeyBinding("ping.key.attack", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_8, PING_CATEGORY));

    private PingHandler pingHandler;

    public PingHandler getPingHandler() {
        if (pingHandler == null) pingHandler = new PingHandler();
        return pingHandler;
    }

    public void registerEvents() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> this.getPingHandler().onClientTick());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (KEY_BINDING.wasPressed()) {
                if (!(client.currentScreen instanceof PingSelectScreen)) {
                    client.openScreen(new PingSelectScreen());
                }
            }

            if (PING_ALERT.isPressed()) {
                this.getPingHandler().sendPing(client, PingType.ALERT);
            } else if (PING_BREAK.isPressed()) {
                this.getPingHandler().sendPing(client, PingType.BREAK);
            } else if (PING_LOOK.isPressed()) {
                this.getPingHandler().sendPing(client, PingType.LOOK);
            } else if (PING_GOTO.isPressed()) {
                this.getPingHandler().sendPing(client, PingType.GOTO);
            } else if (PING_ATTACK.isPressed()) {
                this.getPingHandler().sendPing(client, PingType.ATTACK);
            }
        });
    }
}