package me.flashyreese.mods.ping.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.flashyreese.mods.ping.PingMod;
import me.flashyreese.mods.ping.client.PingHandler;
import me.flashyreese.mods.ping.client.util.AngleHelper;
import me.flashyreese.mods.ping.data.PingType;
import me.flashyreese.mods.ping.util.KeyBindingExtended;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.lwjgl.opengl.GL11;

public class PingSelectScreen extends Screen {
    public final int ITEM_SIZE = 32;

    public PingSelectScreen() {
        super(new TranslatableText("ping.pingSelect.title"));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderGui(matrices, mouseX, mouseY);
    }

    private void renderGui(MatrixStack matrixStack, int mouseX, int mouseY) {
        int outerRadius = 75;
        int innerRadius = 25;

        int pingTypes = PingType.values().length - 1;

        int centerX = this.client.getWindow().getScaledWidth() / 2;
        int centerY = this.client.getWindow().getScaledHeight() / 2;
        this.client.textRenderer.draw(matrixStack, this.title, centerX - this.client.textRenderer.getWidth(this.title) / 2.0F, centerY - outerRadius - 20, 0xFFFFFF);

        int degrees = (int) (360.0D / pingTypes);
        int currentAngle = 360 - degrees / 2;
        int mouseAngle = (int) AngleHelper.getMouseAngle();

        for (int i = 0; i < pingTypes; i++) {
            PingType type = PingType.values()[i + 1];
            int nextAngle = currentAngle + degrees;
            nextAngle = (int) AngleHelper.correctAngle(nextAngle);

            boolean mouseIn = AngleHelper.isAngleBetween(mouseAngle, currentAngle, nextAngle);

            boolean isHovered = !AngleHelper.isInsideCircle(mouseX, mouseY, centerX, centerY, 25)
                    && AngleHelper.isInsideCircle(mouseX, mouseY, centerX, centerY, 75)
                    && mouseIn;
            if (isHovered) {
                this.drawDoughnutSegment(matrixStack, currentAngle, currentAngle + degrees / 2, centerX, centerY, outerRadius + 5, innerRadius, 0xE0000000);
                this.drawDoughnutSegment(matrixStack, currentAngle + degrees / 2, currentAngle + degrees, centerX, centerY, outerRadius + 5, innerRadius, 0xE0000000);
            } else {
                this.drawDoughnutSegment(matrixStack, currentAngle, currentAngle + degrees / 2, centerX, centerY, outerRadius, innerRadius, 0x90000000);
                this.drawDoughnutSegment(matrixStack, currentAngle + degrees / 2, currentAngle + degrees, centerX, centerY, outerRadius, innerRadius, 0x90000000);
            }

            double drawX = centerX;
            double drawY = centerY;

            double outerPointX = (isHovered ? outerRadius + 5 : outerRadius) * Math.sin(Math.toRadians(currentAngle + degrees * 0.5D));
            double outerPointY = (isHovered ? outerRadius + 5 : outerRadius) * Math.cos(Math.toRadians(currentAngle + degrees * 0.5D));
            double innerPointX = innerRadius * Math.sin(Math.toRadians(currentAngle + degrees * 0.5D));
            double innerPointY = innerRadius * Math.cos(Math.toRadians(currentAngle + degrees * 0.5D));

            drawX += (outerPointX + innerPointX) / 2;
            drawY -= (outerPointY + innerPointY) / 2;


            float min = -ITEM_SIZE / 2.0F;
            float max = ITEM_SIZE / 2.0F;

            matrixStack.push();
            RenderSystem.enableBlend();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            MinecraftClient.getInstance().getTextureManager().bindTexture(PingHandler.TEXTURE);
            // Button Icon
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(drawX + min, drawY + max, 0).texture(type.getMinU(), type.getMaxV()).color(255, 255, 255, 255).next();
            bufferBuilder.vertex(drawX + max, drawY + max, 0).texture(type.getMaxU(), type.getMaxV()).color(255, 255, 255, 255).next();
            bufferBuilder.vertex(drawX + max, drawY + min, 0).texture(type.getMaxU(), type.getMinV()).color(255, 255, 255, 255).next();
            bufferBuilder.vertex(drawX + min, drawY + min, 0).texture(type.getMinU(), type.getMinV()).color(255, 255, 255, 255).next();
            tessellator.draw();
            RenderSystem.disableBlend();
            matrixStack.pop();

            if (isHovered) {
                this.client.textRenderer.draw(matrixStack, type.getTranslatedText(), centerX - this.client.textRenderer.getWidth(type.getTranslatedText()) / 2.0F, centerY + outerRadius + 10, 0xFFFFFF);
            }

            currentAngle += degrees;
            currentAngle = (int) AngleHelper.correctAngle(currentAngle);
        }
    }

    @Override
    public void tick() {
        if (!(InputUtil.isKeyPressed(this.client.getWindow().getHandle(), ((KeyBindingExtended)PingMod.getClientHandler().KEY_BINDING).getBoundKey().getCode()) || InputUtil.isKeyPressed(this.client.getWindow().getHandle(), ((KeyBindingExtended)PingMod.getClientHandler().KEY_BINDING).getBoundKey().getCode() + 100))) {
            final double mouseX = this.client.mouse.getX() * ((double) this.client.getWindow().getScaledWidth() / this.client.getWindow().getWidth());
            final double mouseY = this.client.mouse.getY() * ((double) this.client.getWindow().getScaledHeight() / this.client.getWindow().getHeight());

            this.mouseClicked(mouseX, mouseY, 0);
            this.client.openScreen(null);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.client.getWindow().getScaledWidth() / 2;
        int centerY = this.client.getWindow().getScaledHeight() / 2;
        if (!AngleHelper.isInsideCircle(mouseX, mouseY, centerX, centerY, 25)
                && AngleHelper.isInsideCircle(mouseX, mouseY, centerX, centerY, 75)) {
            int pingTypes = PingType.values().length - 1;

            int degrees = (int) (360.0D / pingTypes);
            int currentAngle = 360 - degrees / 2;
            int mouseAngle = (int) AngleHelper.getMouseAngle();

            for (int i = 0; i < pingTypes; i++) {
                PingType type = PingType.values()[i + 1];
                int nextAngle = currentAngle + degrees;
                nextAngle = (int) AngleHelper.correctAngle(nextAngle);

                boolean mouseIn = AngleHelper.isAngleBetween(mouseAngle, currentAngle, nextAngle);
                if (mouseIn) {
                    PingMod.getClientHandler().sendPing(this.client, type);
                }

                currentAngle += degrees;
                currentAngle = (int) AngleHelper.correctAngle(currentAngle);
            }
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


    public void drawDoughnutSegment(MatrixStack matrixStack, int startingAngle, int endingAngle, float centerX, float centerY, double outerRingRadius, double innerRingRadius, int color) {
        float f = (float) (color >> 24 & 0xff) / 255F;
        float f1 = (float) (color >> 16 & 0xff) / 255F;
        float f2 = (float) (color >> 8 & 0xff) / 255F;
        float f3 = (float) (color & 0xff) / 255F;
        matrixStack.push();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(GL11.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        for (int i = startingAngle; i <= endingAngle; i++) {
            double x = Math.sin(Math.toRadians(i)) * innerRingRadius;
            double y = Math.cos(Math.toRadians(i)) * innerRingRadius;
            bufferBuilder.vertex(centerX + x, centerY - y, 0).color(f1, f2, f3, f).next();
        }
        for (int i = endingAngle; i >= startingAngle; i--) {
            double x = Math.sin(Math.toRadians(i)) * outerRingRadius;
            double y = Math.cos(Math.toRadians(i)) * outerRingRadius;
            bufferBuilder.vertex(centerX + x, centerY - y, 0).color(f1, f2, f3, f).next();
        }
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        matrixStack.pop();
    }
}
