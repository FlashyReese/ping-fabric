package me.flashyreese.mods.ping.client.gui;

import me.flashyreese.mods.ping.client.ClientHandler;
import me.flashyreese.mods.ping.client.KeyHandler;
import me.flashyreese.mods.ping.data.PingType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

import static me.flashyreese.mods.ping.client.RenderHandler.ITEM_PADDING;
import static me.flashyreese.mods.ping.client.RenderHandler.ITEM_SIZE;

public class PingSelectGui extends Screen {
    public static final PingSelectGui INSTANCE = new PingSelectGui();
    public static boolean active = false;

    public PingSelectGui() {
        super(new TranslatableText("ping.pingSelect.title"));
    }

    public static void activate() {
        if (MinecraftClient.getInstance().currentScreen == null) {
            active = true;
            MinecraftClient.getInstance().openScreen(INSTANCE);
        }
    }

    public static void deactivate() {
        active = false;
        if (MinecraftClient.getInstance().currentScreen == INSTANCE) {
            MinecraftClient.getInstance().openScreen(null);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int items = PingType.values().length - 1;

        int half = items / 2;
        for (int i = 0; i < items; i++) {
            PingType type = PingType.values()[i + 1];
            int drawX = mc.getWindow().getScaledWidth() / 2 - (ITEM_SIZE * half) - (ITEM_PADDING * (half));
            int drawY = mc.getWindow().getScaledHeight() / 4;

            drawX += ITEM_SIZE / 2 + ITEM_PADDING / 2 + (ITEM_PADDING * i) + ITEM_SIZE * i;

            boolean mouseIn = mouseX >= (drawX - ITEM_SIZE * 0.5D) && mouseX <= (drawX + ITEM_SIZE * 0.5D) &&
                    mouseY >= (drawY - ITEM_SIZE * 0.5D) && mouseY <= (drawY + ITEM_SIZE * 0.5D);

            if (mouseIn) {
                ClientHandler.sendPing(type);
                KeyHandler.ignoreNextRelease = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        active = false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}