package me.flashyreese.mods.ping.client;

import me.flashyreese.mods.ping.client.util.GLUUtils;
import me.flashyreese.mods.ping.client.util.PingRenderHelper;
import me.flashyreese.mods.ping.client.util.VertexHelper;
import me.flashyreese.mods.ping.data.PingType;
import me.flashyreese.mods.ping.data.PingWrapper;
import me.flashyreese.mods.ping.mixin.MatrixStackAccess;
import me.flashyreese.mods.ping.network.packet.ServerBroadcastPing;
import me.flashyreese.mods.ping.util.Config;
import me.flashyreese.mods.ping.util.PingSounds;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PingHandler {

    public static final PingHandler INSTANCE = new PingHandler();
    public static final Identifier TEXTURE = new Identifier("ping", "textures/ping.png");
    private static List<PingWrapper> activePings = new ArrayList<>();

    public void onPingPacket(ServerBroadcastPing packet) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && MathHelper.sqrt(mc.player.squaredDistanceTo(packet.ping.pos.getX(), packet.ping.pos.getY(), packet.ping.pos.getZ())) <= Config.GENERAL.pingAcceptDistance) {
            if (Config.GENERAL.sound) {
                mc.getSoundManager().play(new PositionedSoundInstance(PingSounds.BLOOP, SoundCategory.PLAYERS, 0.25F, 1.0F, packet.ping.pos.getX(), packet.ping.pos.getY(), packet.ping.pos.getZ()));
            }
            packet.ping.timer = Config.GENERAL.pingDuration;
            activePings.add(packet.ping);
        }
    }

    public static void onRenderWorld(Camera camera, float tickDelta, long limitTime, MatrixStack matrix) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity renderEntity = mc.getCameraEntity();
        if (renderEntity == null || activePings.isEmpty()) return;
        Vec3d staticPos = BlockEntityRenderDispatcher.INSTANCE.camera.getPos();
        Camera renderInfo = BlockEntityRenderDispatcher.INSTANCE.camera;
        double clipX = staticPos.getX() + (renderEntity.getX() - staticPos.getX());
        double clipY = staticPos.getY() + (renderEntity.getY() - staticPos.getY()) + 1;
        double clipZ = staticPos.getZ() + (renderEntity.getZ() - staticPos.getZ());

        MatrixStack projectionLook = new MatrixStack();
        //EntityViewRenderEvent.CameraSetup cameraSetup = ForgeHooksClient.onCameraSetup(mc.gameRenderer, renderInfo, tickDelta);
        //renderInfo.setRotation(camera.getYaw(), camera.getPitch());
        projectionLook.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(renderInfo.getPitch()));
        projectionLook.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(renderInfo.getYaw() + 180.0F));
        projectionLook.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(0));

        MatrixStack entityLocation = new MatrixStack();
        ((MatrixStackAccess) entityLocation).getStack().getLast().getModel().multiply(mc.gameRenderer.getBasicProjectionMatrix(renderInfo, tickDelta, false)); //Don't use FOV

        Frustum clippingHelper = new Frustum(((MatrixStackAccess) projectionLook).getStack().getLast().getModel(), ((MatrixStackAccess) entityLocation).getStack().getLast().getModel());
        clippingHelper.setPosition(clipX, clipY, clipZ);

        for (PingWrapper ping : activePings) {
            double px = ping.pos.getX() + 0.5D - staticPos.getX();
            double py = ping.pos.getY() + 0.5D - staticPos.getY();
            double pz = ping.pos.getZ() + 0.5D - staticPos.getZ();

            if (clippingHelper.isVisible(ping.getBox())) {
                ping.isOffscreen = false;
                if (Config.VISUAL.blockOverlay) {
                    renderPingOverlay(ping.pos.getX() - staticPos.getX(), ping.pos.getY() - staticPos.getY(), ping.pos.getZ() - staticPos.getZ(), matrix, ping);
                }
                renderPing(px, py, pz, matrix, renderEntity, ping);
            } else {
                ping.isOffscreen = true;
                translatePingCoordinates(px, py, pz, ping);
            }
        }
    }

    public static void renderPingOffscreen(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        for (PingWrapper ping : activePings) {
            if (!ping.isOffscreen || mc.currentScreen != null || mc.options.debugEnabled) {
                continue;
            }
            int width = mc.getWindow().getWidth();
            int height = mc.getWindow().getHeight();

            int x1 = -(width / 2) + 32;
            int y1 = -(height / 2) + 32;
            int x2 = (width / 2) - 32;
            int y2 = (height / 2) - 32;

            double pingX = ping.screenX;
            double pingY = ping.screenY;

            pingX -= width * 0.5D;
            pingY -= height * 0.5D;

            //TODO Fix that player rotation is not being taken into account. Been an issue since the creation of the mod
            double angle = Math.atan2(pingY, pingX);
            angle += (Math.toRadians(90));
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double m = cos / sin;

            if (cos > 0) {
                pingX = y2 / m;
                pingY = y2;
            } else {
                pingX = y1 / m;
                pingY = y1;
            }

            if (pingX > x2) {
                pingX = x2;
                pingY = x2 * m;
            } else if (pingX < x1) {
                pingX = x1;
                pingY = x1 * m;
            }

            pingX += width * 0.5D;
            pingY += height * 0.5D;

            MatrixStack matrixStack = new MatrixStack();
            matrixStack.push();
            MatrixStack.Entry matrixEntry = ((MatrixStackAccess) matrixStack).getStack().getLast();
            Matrix4f matrix4f = matrixEntry.getModel();
            RenderLayer pingType = PingRenderType.getPingIcon(TEXTURE);
            VertexConsumerProvider.Immediate buffer = mc.getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer vertexBuilder = buffer.getBuffer(pingType);

            matrixStack.translate(pingX / 2, pingY / 2, 0);

            float min = -8;
            float max = 8;

            // Ping Notice Background
            int r = ping.color >> 16 & 255;
            int g = ping.color >> 8 & 255;
            int b = ping.color & 255;
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, max, PingType.BACKGROUND.getMinU(), PingType.BACKGROUND.getMaxV(), r, g, b, 255);
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, max, PingType.BACKGROUND.getMaxU(), PingType.BACKGROUND.getMaxV(), r, g, b, 255);
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, min, PingType.BACKGROUND.getMaxU(), PingType.BACKGROUND.getMinV(), r, g, b, 255);
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, min, PingType.BACKGROUND.getMinU(), PingType.BACKGROUND.getMinV(), r, g, b, 255);

            // Ping Notice Icon
            float alpha = ping.type == PingType.ALERT ? mc.world != null ? (float) (1.0F + (0.01D * Math.sin(mc.world.getTimeOfDay()))) : 0.85F : 0.85F;
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, max, ping.type.getMinU(), ping.type.getMaxV(), 1.0F, 1.0F, 1.0F, alpha);
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, max, ping.type.getMaxU(), ping.type.getMaxV(), 1.0F, 1.0F, 1.0F, alpha);
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, min, ping.type.getMaxU(), ping.type.getMinV(), 1.0F, 1.0F, 1.0F, alpha);
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, min, ping.type.getMinU(), ping.type.getMinV(), 1.0F, 1.0F, 1.0F, alpha);
            buffer.draw(pingType);

            matrixStack.translate(0, 0, 0);

            matrixStack.pop();
        }
    }

    public static void onClientTick() {
        Iterator<PingWrapper> iterator = activePings.iterator();
        while (iterator.hasNext()) {
            PingWrapper pingWrapper = iterator.next();
            if (pingWrapper.animationTimer > 0) {
                pingWrapper.animationTimer -= 5;
            }
            pingWrapper.timer--;

            if (pingWrapper.timer <= 0) {
                iterator.remove();
            }
        }
    }

    private static void renderPing(double px, double py, double pz, MatrixStack matrixStack, Entity renderEntity, PingWrapper ping) {
        MinecraftClient mc = MinecraftClient.getInstance();
        matrixStack.push();
        matrixStack.translate(px, py, pz);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-renderEntity.yaw));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(renderEntity.pitch));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));

        MatrixStack.Entry matrixEntry = ((MatrixStackAccess) matrixStack).getStack().getLast();
        Matrix4f matrix4f = matrixEntry.getModel();
        VertexConsumerProvider.Immediate buffer = mc.getBufferBuilders().getEntityVertexConsumers();
        RenderLayer pingType = PingRenderType.getPingIcon(TEXTURE);
        VertexConsumer vertexBuilder = buffer.getBuffer(pingType);

        float min = -0.25F - (0.25F * (float) ping.animationTimer / 20F);
        float max = 0.25F + (0.25F * (float) ping.animationTimer / 20F);

        // Block Overlay Background
        int r = ping.color >> 16 & 255;
        int g = ping.color >> 8 & 255;
        int b = ping.color & 255;
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, max, PingType.BACKGROUND.getMinU(), PingType.BACKGROUND.getMaxV(), r, g, b, 255);
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, max, PingType.BACKGROUND.getMaxU(), PingType.BACKGROUND.getMaxV(), r, g, b, 255);
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, min, PingType.BACKGROUND.getMaxU(), PingType.BACKGROUND.getMinV(), r, g, b, 255);
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, min, PingType.BACKGROUND.getMinU(), PingType.BACKGROUND.getMinV(), r, g, b, 255);

        // Block Overlay Icon
        float alpha = ping.type == PingType.ALERT ? mc.world != null ? (float) (1.0F + (0.01D * Math.sin(mc.world.getTimeOfDay()))) : 0.85F : 0.85F;
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, max, ping.type.getMinU(), ping.type.getMaxV(), 1.0F, 1.0F, 1.0F, alpha);
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, max, ping.type.getMaxU(), ping.type.getMaxV(), 1.0F, 1.0F, 1.0F, alpha);
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, min, ping.type.getMaxU(), ping.type.getMinV(), 1.0F, 1.0F, 1.0F, alpha);
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, min, ping.type.getMinU(), ping.type.getMinV(), 1.0F, 1.0F, 1.0F, alpha);
        buffer.draw(pingType);

        matrixStack.pop();
    }

    private static void renderPingOverlay(double x, double y, double z, MatrixStack matrixStack, PingWrapper ping) {
        Sprite icon = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(new ItemStack(Blocks.WHITE_STAINED_GLASS)).getSprite();
        float padding = 0F + (0.20F * (float) ping.animationTimer / (float) 20);
        float box = 1 + padding + padding;

        matrixStack.push();
        matrixStack.translate(x + 0.5, y + 0.5, z + 0.5);
        PingRenderHelper.drawBlockOverlay(box, box, box, matrixStack, icon, ping.color, 175);
        matrixStack.translate(0, 0, 0);

        matrixStack.pop();
    }

    private static void translatePingCoordinates(double px, double py, double pz, PingWrapper ping) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(4);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);

        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        if (GLUUtils.gluProject((float) px, (float) py, (float) pz, modelView, projection, viewport, screenCoords)) {
            ping.screenX = screenCoords.get(0);
            ping.screenY = screenCoords.get(1);
        }
    }

}
