package me.flashyreese.mods.ping.client.util;

import me.flashyreese.mods.ping.client.PingRenderType;
import me.flashyreese.mods.ping.mixin.MatrixStackAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class PingRenderHelper {

    public static void drawBlockOverlay(float width, float height, float length, MatrixStack matrixStack, Sprite sprite, int color, int alpha) {
        MatrixStack.Entry matrixEntry = ((MatrixStackAccess) matrixStack).getStack().getLast();
        Matrix4f posMatrix = matrixEntry.getModel();
        RenderLayer pingOverlay = PingRenderType.getPingOverlay();
        VertexConsumerProvider.Immediate buffer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertexBuilder = buffer.getBuffer(pingOverlay);

        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;

        // TOP
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), (height / 2), -(length / 2), sprite.getMinU(), sprite.getMinV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), (height / 2), -(length / 2), sprite.getMaxU(), sprite.getMinV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), (height / 2), (length / 2), sprite.getMaxU(), sprite.getMaxV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), (height / 2), (length / 2), sprite.getMinU(), sprite.getMaxV(), r, g, b, alpha);

        // BOTTOM
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), -(height / 2), (length / 2), sprite.getMinU(), sprite.getMaxV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), -(height / 2), (length / 2), sprite.getMaxU(), sprite.getMaxV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), -(height / 2), -(length / 2), sprite.getMaxU(), sprite.getMinV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), -(height / 2), -(length / 2), sprite.getMinU(), sprite.getMinV(), r, g, b, alpha);

        // NORTH
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), (height / 2), (length / 2), sprite.getMinU(), sprite.getMaxV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), (height / 2), (length / 2), sprite.getMaxU(), sprite.getMaxV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), -(height / 2), (length / 2), sprite.getMaxU(), sprite.getMinV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), -(height / 2), (length / 2), sprite.getMinU(), sprite.getMinV(), r, g, b, alpha);

        // SOUTH
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), -(height / 2), -(length / 2), sprite.getMinU(), sprite.getMinV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), -(height / 2), -(length / 2), sprite.getMaxU(), sprite.getMinV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), (height / 2), -(length / 2), sprite.getMaxU(), sprite.getMaxV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), (height / 2), -(length / 2), sprite.getMinU(), sprite.getMaxV(), r, g, b, alpha);

        // EAST
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), (height / 2), -(length / 2), sprite.getMinU(), sprite.getMaxV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), (height / 2), (length / 2), sprite.getMaxU(), sprite.getMaxV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), -(height / 2), (length / 2), sprite.getMaxU(), sprite.getMinV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), -(height / 2), -(length / 2), sprite.getMinU(), sprite.getMinV(), r, g, b, alpha);

        // WEST
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), -(height / 2), -(length / 2), sprite.getMinU(), sprite.getMinV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), -(height / 2), (length / 2), sprite.getMaxU(), sprite.getMinV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), (height / 2), (length / 2), sprite.getMaxU(), sprite.getMaxV(), r, g, b, alpha);
        VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), (height / 2), -(length / 2), sprite.getMinU(), sprite.getMaxV(), r, g, b, alpha);
        buffer.draw(pingOverlay);
    }
}