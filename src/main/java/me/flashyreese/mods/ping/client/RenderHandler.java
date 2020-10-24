package me.flashyreese.mods.ping.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.flashyreese.mods.ping.data.PingType;
import me.flashyreese.mods.ping.util.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

public class RenderHandler {
    public static final int ITEM_PADDING = 10;
    public static final int ITEM_SIZE = 32;

    public static void renderGui() {
        int numOfItems = PingType.values().length - 1;

        MinecraftClient mc = MinecraftClient.getInstance();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        // Menu Background
        if (Config.VISUAL.menuBackground) {
            RenderSystem.pushMatrix();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);

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
                r = Config.VISUAL.pingR;
                g = Config.VISUAL.pingG;
                b = Config.VISUAL.pingB;
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

    public static void renderText(MatrixStack matrixStack) {
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
}
