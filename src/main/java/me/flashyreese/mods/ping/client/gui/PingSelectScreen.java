package me.flashyreese.mods.ping.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.flashyreese.mods.ping.PingMod;
import me.flashyreese.mods.ping.client.PingHandler;
import me.flashyreese.mods.ping.data.PingType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class PingSelectScreen extends Screen {

    public final int ITEM_PADDING = 10;
    public final int ITEM_SIZE = 32;

    public PingSelectScreen() {
        super(new TranslatableText("ping.pingSelect.title"));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderGui();
        this.renderText(matrices);
    }

    public void renderGui() {
        int numOfItems = PingType.values().length - 1;

        MinecraftClient mc = MinecraftClient.getInstance();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        // Menu Background
        if (PingMod.config().VISUAL.menuBackground) {
            RenderSystem.pushMatrix();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            int halfWidth = (ITEM_SIZE * (numOfItems)) - (ITEM_PADDING * (numOfItems));
            int halfHeight = (ITEM_SIZE + ITEM_PADDING) / 2;
            int backgroundX = mc.getWindow().getScaledWidth() / 2 - halfWidth;
            int backgroundY = mc.getWindow().getScaledHeight() / 4 - halfHeight;

            bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(backgroundX, backgroundY + 15 + halfHeight * 2, 0).color(0F, 0F, 0F, 0.5F).next();
            bufferBuilder.vertex(backgroundX + halfWidth * 2, backgroundY + 15 + halfHeight * 2, 0).color(0F, 0F, 0F, 0.5F).next();
            bufferBuilder.vertex(backgroundX + halfWidth * 2, backgroundY, 0).color(0F, 0F, 0F, 0.5F).next();
            bufferBuilder.vertex(backgroundX, backgroundY, 0).color(0F, 0F, 0F, 0.5F).next();
            tessellator.draw();

            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            RenderSystem.popMatrix();
        }

        MinecraftClient.getInstance().getTextureManager().bindTexture(PingHandler.TEXTURE);

        final double mouseX = mc.mouse.getX() * ((double) mc.getWindow().getScaledWidth() / mc.getWindow().getWidth());
        final double mouseY = mc.mouse.getY() * ((double) mc.getWindow().getScaledHeight() / mc.getWindow().getHeight());

        int half = numOfItems / 2;
        for (int i = 0; i < numOfItems; i++) {
            PingType type = PingType.values()[i + 1];
            int drawX = mc.getWindow().getScaledWidth() / 2 - (ITEM_SIZE * half) - (ITEM_PADDING * (half));
            int drawY = mc.getWindow().getScaledHeight() / 4;

            drawX += ITEM_SIZE / 2 + ITEM_PADDING / 2 + (ITEM_PADDING * i) + ITEM_SIZE * i;

            boolean mouseIn = mouseX >= (drawX - ITEM_SIZE * 0.5D) && mouseX <= (drawX + ITEM_SIZE * 0.5D) &&
                    mouseY >= (drawY - ITEM_SIZE * 0.5D) && mouseY <= (drawY + ITEM_SIZE * 0.5D);

            float min = -ITEM_SIZE / 2.0F;
            float max = ITEM_SIZE / 2.0F;

            int r = 255;
            int g = 255;
            int b = 255;

            // Button Background
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            if (mouseIn) {
                r = PingMod.config().VISUAL.pingR;
                g = PingMod.config().VISUAL.pingG;
                b = PingMod.config().VISUAL.pingB;
            }
            bufferBuilder.vertex(drawX + min, drawY + max, 0).texture(PingType.BACKGROUND.getMinU(), PingType.BACKGROUND.getMaxV()).color(r, g, b, 255).next();
            bufferBuilder.vertex(drawX + max, drawY + max, 0).texture(PingType.BACKGROUND.getMaxU(), PingType.BACKGROUND.getMaxV()).color(r, g, b, 255).next();
            bufferBuilder.vertex(drawX + max, drawY + min, 0).texture(PingType.BACKGROUND.getMaxU(), PingType.BACKGROUND.getMinV()).color(r, g, b, 255).next();
            bufferBuilder.vertex(drawX + min, drawY + min, 0).texture(PingType.BACKGROUND.getMinU(), PingType.BACKGROUND.getMinV()).color(r, g, b, 255).next();
            tessellator.draw();

            // Button Icon
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(drawX + min, drawY + max, 0).texture(type.getMinU(), type.getMaxV()).color(255, 255, 255, 255).next();
            bufferBuilder.vertex(drawX + max, drawY + max, 0).texture(type.getMaxU(), type.getMaxV()).color(255, 255, 255, 255).next();
            bufferBuilder.vertex(drawX + max, drawY + min, 0).texture(type.getMaxU(), type.getMinV()).color(255, 255, 255, 255).next();
            bufferBuilder.vertex(drawX + min, drawY + min, 0).texture(type.getMinU(), type.getMinV()).color(255, 255, 255, 255).next();
            tessellator.draw();
        }
    }

    public void renderText(MatrixStack matrixStack) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int numOfItems = PingType.values().length - 1;

        final double mouseX = mc.mouse.getX() * ((double) mc.getWindow().getScaledWidth() / mc.getWindow().getWidth());
        final double mouseY = mc.mouse.getY() * ((double) mc.getWindow().getScaledHeight() / mc.getWindow().getHeight());

        int halfHeight = (ITEM_SIZE + ITEM_PADDING) / 2;
        int backgroundY = mc.getWindow().getScaledHeight() / 4 - halfHeight;

        int half = numOfItems / 2;
        for (int i = 0; i < numOfItems; i++) {
            PingType type = PingType.values()[i + 1];
            int drawX = mc.getWindow().getScaledWidth() / 2 - (ITEM_SIZE * half) - (ITEM_PADDING * (half));
            int drawY = mc.getWindow().getScaledHeight() / 4;

            drawX += ITEM_SIZE / 2 + ITEM_PADDING / 2 + (ITEM_PADDING * i) + ITEM_SIZE * i;

            boolean mouseIn = mouseX >= (drawX - ITEM_SIZE * 0.5D) && mouseX <= (drawX + ITEM_SIZE * 0.5D) &&
                    mouseY >= (drawY - ITEM_SIZE * 0.5D) && mouseY <= (drawY + ITEM_SIZE * 0.5D);

            if (mouseIn) {
                RenderSystem.pushMatrix();
                RenderSystem.color4f(255, 255, 255, 255);
                mc.textRenderer.drawWithShadow(matrixStack, type.toString(), mc.getWindow().getScaledWidth() / 2.0F - mc.textRenderer.getWidth(type.toString()) / 2.0F, backgroundY + halfHeight * 2, 0xFFFFFF);
                RenderSystem.popMatrix();
            }
        }
    }

    @Override
    public void tick() {
        if (!(InputUtil.isKeyPressed(this.client.getWindow().getHandle(), PingMod.getClientHandler().KEY_BINDING.getDefaultKey().getCode()) || InputUtil.isKeyPressed(this.client.getWindow().getHandle(), PingMod.getClientHandler().KEY_BINDING.getDefaultKey().getCode() + 100))) {
            final double mouseX = this.client.mouse.getX() * ((double) this.client.getWindow().getScaledWidth() / this.client.getWindow().getWidth());
            final double mouseY = this.client.mouse.getY() * ((double) this.client.getWindow().getScaledHeight() / this.client.getWindow().getHeight());

            this.mouseClicked(mouseX, mouseY, 0);
            this.client.openScreen(null);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int items = PingType.values().length - 1;

        int half = items / 2;
        for (int i = 0; i < items; i++) {
            PingType type = PingType.values()[i + 1];
            int drawX = this.client.getWindow().getScaledWidth() / 2 - (ITEM_SIZE * half) - (ITEM_PADDING * (half));
            int drawY = this.client.getWindow().getScaledHeight() / 4;

            drawX += ITEM_SIZE / 2 + ITEM_PADDING / 2 + (ITEM_PADDING * i) + ITEM_SIZE * i;

            boolean mouseIn = mouseX >= (drawX - ITEM_SIZE * 0.5D) && mouseX <= (drawX + ITEM_SIZE * 0.5D) &&
                    mouseY >= (drawY - ITEM_SIZE * 0.5D) && mouseY <= (drawY + ITEM_SIZE * 0.5D);

            if (mouseIn) {
                PingMod.getClientHandler().sendPing(type);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
