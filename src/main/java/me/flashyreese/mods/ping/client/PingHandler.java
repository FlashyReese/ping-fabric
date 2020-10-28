package me.flashyreese.mods.ping.client;

import me.flashyreese.mods.ping.PingMod;
import me.flashyreese.mods.ping.client.util.GLUUtils;
import me.flashyreese.mods.ping.client.util.PingRenderHelper;
import me.flashyreese.mods.ping.client.util.VertexHelper;
import me.flashyreese.mods.ping.data.PingType;
import me.flashyreese.mods.ping.data.PingWrapper;
import me.flashyreese.mods.ping.mixin.MatrixStackAccess;
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
import net.minecraft.util.math.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PingHandler {
    public static final Identifier TEXTURE = new Identifier("ping", "textures/ping.png");
    private static List<PingWrapper> activePings = new ArrayList<>();

    public void onPingPacket(PingWrapper ping) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (ping.getBlockPos() != null) {
            if (mc.player != null && MathHelper.sqrt(mc.player.squaredDistanceTo(ping.getBlockPos().getX(), ping.getBlockPos().getY(), ping.getBlockPos().getZ())) <= PingMod.config().GENERAL.pingAcceptDistance) {
                if (PingMod.config().GENERAL.sound) {
                    mc.getSoundManager().play(new PositionedSoundInstance(PingSounds.BLOOP, SoundCategory.PLAYERS, 0.25F, 1.0F, ping.getBlockPos().getX(), ping.getBlockPos().getY(), ping.getBlockPos().getZ()));
                }
                ping.setTimer(PingMod.config().GENERAL.pingDuration);
                activePings.add(ping);
            }
        } else {
            if (mc.player != null) {
                Entity entity = mc.player.world.getEntityById(ping.getEntityId());

                if (entity != null) {
                    Vec3d pos = entity.getPos();
                    if (PingMod.config().GENERAL.sound) {
                        mc.getSoundManager().play(new PositionedSoundInstance(PingSounds.BLOOP, SoundCategory.PLAYERS, 0.25F, 1.0F, pos.getX(), pos.getY(), pos.getZ()));
                    }
                    ping.setTimer(PingMod.config().GENERAL.pingDuration);
                    activePings.add(ping);
                }
            }
        }
    }

    public boolean hasOutline(Entity entity){
        for (PingWrapper ping: activePings){
            if (entity == MinecraftClient.getInstance().player.world.getEntityById(ping.getEntityId())){
                return true;
            }
        }
        return false;
    }

    public void onRenderWorld(Camera camera, float tickDelta, long limitTime, MatrixStack matrix) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity cameraEntity = mc.getCameraEntity();
        if (cameraEntity == null || activePings.isEmpty()) return;
        Vec3d staticPos = BlockEntityRenderDispatcher.INSTANCE.camera.getPos();
        Camera renderCamera = BlockEntityRenderDispatcher.INSTANCE.camera;
        double clipX = staticPos.getX() + (cameraEntity.getX() - staticPos.getX());
        double clipY = staticPos.getY() + (cameraEntity.getY() - staticPos.getY()) + 1;
        double clipZ = staticPos.getZ() + (cameraEntity.getZ() - staticPos.getZ());

        MatrixStack projectionLook = new MatrixStack();
        //EntityViewRenderEvent.CameraSetup cameraSetup = ForgeHooksClient.onCameraSetup(mc.gameRenderer, renderCamera, tickDelta);
        //renderCamera.setRotation(camera.getYaw(), camera.getPitch());
        //renderCamera.setRotation(camera.getYaw(), camera.getPitch());
        projectionLook.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(renderCamera.getPitch()));
        projectionLook.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(renderCamera.getYaw() + 180.0F));
        projectionLook.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(0));

        MatrixStack entityLocation = new MatrixStack();
        ((MatrixStackAccess) entityLocation).getStack().getLast().getModel().multiply(mc.gameRenderer.getBasicProjectionMatrix(renderCamera, tickDelta, false)); //Don't use FOV

        Frustum clippingHelper = new Frustum(((MatrixStackAccess) projectionLook).getStack().getLast().getModel(), ((MatrixStackAccess) entityLocation).getStack().getLast().getModel());
        clippingHelper.setPosition(clipX, clipY, clipZ);

        for (PingWrapper ping : activePings) {
            if (ping.getBlockPos() == null) continue;
            double px = ping.getBlockPos().getX() + 0.5D - staticPos.getX();
            double py = ping.getBlockPos().getY() + 0.5D - staticPos.getY();
            double pz = ping.getBlockPos().getZ() + 0.5D - staticPos.getZ();

            if (clippingHelper.isVisible(ping.getBox())) {
                ping.setOffscreen(false);
                if (PingMod.config().VISUAL.blockOverlay) {
                    renderPingOverlay(ping.getBlockPos().getX() - staticPos.getX(), ping.getBlockPos().getY() - staticPos.getY(), ping.getBlockPos().getZ() - staticPos.getZ(), matrix, ping);
                }
                renderPing(px, py, pz, matrix, cameraEntity, ping);
            } else {
                ping.setOffscreen(true);
                translatePingCoordinates(px, py, pz, ping);
            }
        }
    }

    public void renderPingOffscreen(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        for (PingWrapper ping : activePings) {
            //if (ping.getBlockPos() == null) continue;

            if (!ping.isOffscreen() || mc.currentScreen != null || mc.options.debugEnabled) {
                continue;
            }
            int width = mc.getWindow().getWidth();
            int height = mc.getWindow().getHeight();

            int x1 = -(width / 2) + 32;
            int y1 = -(height / 2) + 32;
            int x2 = (width / 2) - 32;
            int y2 = (height / 2) - 32;

            double pingX = ping.getScreenX();
            double pingY = ping.getScreenY();

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
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, max, PingType.BACKGROUND.getMinU(), PingType.BACKGROUND.getMaxV(), 255, 255, 255, 255);
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, max, PingType.BACKGROUND.getMaxU(), PingType.BACKGROUND.getMaxV(), 255, 255, 255, 255);
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, min, PingType.BACKGROUND.getMaxU(), PingType.BACKGROUND.getMinV(), 255, 255, 255, 255);
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, min, PingType.BACKGROUND.getMinU(), PingType.BACKGROUND.getMinV(), 255, 255, 255, 255);

            // Ping Notice Icon
            float alpha = 0.85F;
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, max, ping.getType().getMinU(), ping.getType().getMaxV(), 1.0F, 1.0F, 1.0F, alpha);
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, max, ping.getType().getMaxU(), ping.getType().getMaxV(), 1.0F, 1.0F, 1.0F, alpha);
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, min, ping.getType().getMaxU(), ping.getType().getMinV(), 1.0F, 1.0F, 1.0F, alpha);
            VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, min, ping.getType().getMinU(), ping.getType().getMinV(), 1.0F, 1.0F, 1.0F, alpha);
            buffer.draw(pingType);

            matrixStack.translate(0, 0, 0);

            matrixStack.pop();
        }
    }

    public void onClientTick() {
        Iterator<PingWrapper> iterator = activePings.iterator();
        while (iterator.hasNext()) {
            PingWrapper pingWrapper = iterator.next();
            if (pingWrapper.getAnimationTimer() > 0) {
                pingWrapper.setAnimationTimer(pingWrapper.getAnimationTimer() - 5);
            }
            pingWrapper.setTimer(pingWrapper.getTimer() - 1);

            if (pingWrapper.getTimer() <= 0) {
                iterator.remove();
            }
        }
    }

    private void renderPing(double px, double py, double pz, MatrixStack matrixStack, Entity renderEntity, PingWrapper ping) {
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

        float min = -0.25F - (0.25F * (float) ping.getAnimationTimer() / 20F);
        float max = 0.25F + (0.25F * (float) ping.getAnimationTimer() / 20F);

        // Block Overlay Background
        /*int r = ping.getColor() >> 16 & 255;
        int g = ping.getColor() >> 8 & 255;
        int b = ping.getColor() & 255;*/
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, max, PingType.BACKGROUND.getMinU(), PingType.BACKGROUND.getMaxV(), 255, 255, 255, 255);
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, max, PingType.BACKGROUND.getMaxU(), PingType.BACKGROUND.getMaxV(), 255, 255, 255, 255);
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, min, PingType.BACKGROUND.getMaxU(), PingType.BACKGROUND.getMinV(), 255, 255, 255, 255);
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, min, PingType.BACKGROUND.getMinU(), PingType.BACKGROUND.getMinV(), 255, 255, 255, 255);

        // Block Overlay Icon
        float alpha = 0.85F;
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, max, ping.getType().getMinU(), ping.getType().getMaxV(), 1.0F, 1.0F, 1.0F, alpha);
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, max, ping.getType().getMaxU(), ping.getType().getMaxV(), 1.0F, 1.0F, 1.0F, alpha);
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, max, min, ping.getType().getMaxU(), ping.getType().getMinV(), 1.0F, 1.0F, 1.0F, alpha);
        VertexHelper.renderPosTexColorNoZ(vertexBuilder, matrix4f, min, min, ping.getType().getMinU(), ping.getType().getMinV(), 1.0F, 1.0F, 1.0F, alpha);
        buffer.draw(pingType);

        matrixStack.pop();
    }

    private void renderPingOverlay(double x, double y, double z, MatrixStack matrixStack, PingWrapper ping) {
        Sprite icon = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(new ItemStack(Blocks.WHITE_STAINED_GLASS)).getSprite();
        float padding = 0F + (0.20F * (float) ping.getAnimationTimer() / (float) 20);
        float box = 1 + padding + padding;

        matrixStack.push();
        matrixStack.translate(x + 0.5, y + 0.5, z + 0.5);
        PingRenderHelper.drawBlockOverlay(box, box, box, matrixStack, icon, ping.getColor(), 175);
        matrixStack.translate(0, 0, 0);

        matrixStack.pop();
    }

    private void translatePingCoordinates(double px, double py, double pz, PingWrapper ping) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(4);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);

        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        if (GLUUtils.gluProject((float) px, (float) py, (float) pz, modelView, projection, viewport, screenCoords)) {
            ping.setScreenX(screenCoords.get(0));
            ping.setScreenY(screenCoords.get(1));
        }
    }

}
