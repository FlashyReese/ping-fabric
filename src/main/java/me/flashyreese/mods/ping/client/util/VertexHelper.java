package me.flashyreese.mods.ping.client.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Matrix4f;

public class VertexHelper {

    public static void renderPosTexColor(VertexConsumer builder, Matrix4f matrix4f, float x, float y, float z, float u, float v, int r, int g, int b, int a) {
        builder.vertex(matrix4f, x, y, z).texture(u, v).color(r, g, b, a).next();
    }

    public static void renderPosTexColorNoZ(VertexConsumer builder, Matrix4f matrix4f, float x, float y, float u, float v, int r, int g, int b, int a) {
        builder.vertex(matrix4f, x, y, 0).texture(u, v).color(r, g, b, a).next();
    }

    public static void renderPosTexColorNoZ(VertexConsumer builder, Matrix4f matrix4f, float x, float y, float u, float v, float r, float g, float b, float a) {
        builder.vertex(matrix4f, x, y, 0).texture(u, v).color(r, g, b, a).next();
    }
}