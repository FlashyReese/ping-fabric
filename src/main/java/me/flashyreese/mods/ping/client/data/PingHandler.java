package me.flashyreese.mods.ping.client.data;

import me.flashyreese.mods.ping.client.PingClientMod;
import me.flashyreese.mods.ping.client.data.ping.BlockPingWrapper;
import me.flashyreese.mods.ping.client.data.ping.EntityPingWrapper;
import me.flashyreese.mods.ping.client.data.ping.PingWrapper;
import me.flashyreese.mods.ping.client.util.GLUUtils;
import me.flashyreese.mods.ping.client.util.PingRenderHelper;
import me.flashyreese.mods.ping.client.util.VertexHelper;
import me.flashyreese.mods.ping.network.packet.HighlightBlockPacket;
import me.flashyreese.mods.ping.util.PingSounds;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PingHandler {
    public static final Identifier TEXTURE = new Identifier("ping", "textures/ping.png");
    private final List<PingWrapper> activePings = new ArrayList<>();

    //todo: check if Block is already highlighted by the same player
    public void onBlockPingPacket(BlockPingWrapper ping) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && MathHelper.sqrt(mc.player.squaredDistanceTo(ping.getBlockPos().getX(), ping.getBlockPos().getY(), ping.getBlockPos().getZ())) <= PingClientMod.config().GENERAL.pingAcceptDistance) {
            if (PingClientMod.config().GENERAL.sound) {
                mc.getSoundManager().play(new PositionedSoundInstance(PingSounds.BLOOP, SoundCategory.PLAYERS, 0.25F, 1.0F, ping.getBlockPos().getX(), ping.getBlockPos().getY(), ping.getBlockPos().getZ()));
            }
            ping.setTimer(PingClientMod.config().GENERAL.pingDuration);
            Optional<PingWrapper> pingWrapperOptional = this.activePings.stream().filter(pingWrapper -> pingWrapper.getSenderUUID().equals(ping.getSenderUUID()) && pingWrapper instanceof BlockPingWrapper && ((BlockPingWrapper) pingWrapper).getBlockPos().equals(ping.getBlockPos())).findFirst();
            if (!pingWrapperOptional.isPresent()) {
                this.activePings.add(ping);
            }
        }
    }

    public void onEntityPingPacket(EntityPingWrapper ping) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            Entity entity = mc.player.world.getEntityById(ping.getEntityID());

            if (entity != null) {
                Vec3d pos = entity.getPos();
                if (PingClientMod.config().GENERAL.sound) {
                    mc.getSoundManager().play(new PositionedSoundInstance(PingSounds.BLOOP, SoundCategory.PLAYERS, 0.25F, 1.0F, pos.getX(), pos.getY(), pos.getZ()));
                }
                ping.setTimer(PingClientMod.config().GENERAL.pingDuration);
                this.activePings.add(ping);
            }
        }
    }

    public boolean hasOutline(Entity entity) {
        if (MinecraftClient.getInstance().player == null) return false;
        for (PingWrapper ping : this.activePings) {
            if (ping instanceof EntityPingWrapper && entity == MinecraftClient.getInstance().player.world.getEntityById(((EntityPingWrapper) ping).getEntityID())) {
                return true;
            }
        }
        return false;
    }

    public void onRenderWorld(float tickDelta, MatrixStack matrix) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity cameraEntity = mc.getCameraEntity();
        if (cameraEntity == null || this.activePings.isEmpty()) return;
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
        entityLocation.peek().getModel().multiply(mc.gameRenderer.getBasicProjectionMatrix(renderCamera, tickDelta, false)); //Don't use FOV

        Frustum clippingHelper = new Frustum(projectionLook.peek().getModel(), entityLocation.peek().getModel());
        clippingHelper.setPosition(clipX, clipY, clipZ);

        for (PingWrapper ping : this.activePings) {
            if (ping instanceof BlockPingWrapper) {
                BlockPingWrapper pingWrapper = (BlockPingWrapper) ping;
                double px = pingWrapper.getBlockPos().getX() + 0.5D - staticPos.getX();
                double py = pingWrapper.getBlockPos().getY() + 0.5D - staticPos.getY();
                double pz = pingWrapper.getBlockPos().getZ() + 0.5D - staticPos.getZ();

                if (clippingHelper.isVisible(pingWrapper.getBox())) {
                    pingWrapper.setOffscreen(false);
                    if (PingClientMod.config().VISUAL.blockOverlay) {
                        renderPingOverlay(pingWrapper.getBlockPos().getX() - staticPos.getX(), pingWrapper.getBlockPos().getY() - staticPos.getY(), pingWrapper.getBlockPos().getZ() - staticPos.getZ(), matrix, ping);
                    }
                    renderPing(px, py, pz, matrix, cameraEntity, ping);
                } else {
                    pingWrapper.setOffscreen(true);
                    translatePingCoordinates(px, py, pz, pingWrapper);
                }
            }
        }
    }

    public void renderPingOffscreen(MatrixStack matrices) {
        MinecraftClient mc = MinecraftClient.getInstance();
        for (PingWrapper ping : this.activePings) {
            if (ping instanceof BlockPingWrapper) {
                BlockPingWrapper pingWrapper = (BlockPingWrapper) ping;

                if (!pingWrapper.isOffscreen() || mc.currentScreen != null || mc.options.debugEnabled) {
                    continue;
                }
                int width = mc.getWindow().getWidth();
                int height = mc.getWindow().getHeight();

                int x1 = -(width / 2) + 32;
                int y1 = -(height / 2) + 32;
                int x2 = (width / 2) - 32;
                int y2 = (height / 2) - 32;

                double pingX = pingWrapper.getScreenX();
                double pingY = pingWrapper.getScreenY();

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

                matrices.push();
                Matrix4f matrix4f = matrices.peek().getModel();
                RenderLayer pingType = PingRenderType.getPingIcon(TEXTURE);
                VertexConsumerProvider.Immediate buffer = mc.getBufferBuilders().getEntityVertexConsumers();
                VertexConsumer vertexBuilder = buffer.getBuffer(pingType);

                matrices.translate(pingX / 2, pingY / 2, 0);

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

                matrices.translate(0, 0, 0);

                matrices.pop();
            }
        }
    }

    public void onClientTick() {
        Iterator<PingWrapper> iterator = this.activePings.iterator();
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

        MatrixStack.Entry matrixEntry = matrixStack.peek();
        Matrix4f matrix4f = matrixEntry.getModel();
        VertexConsumerProvider.Immediate buffer = mc.getBufferBuilders().getEntityVertexConsumers();
        RenderLayer pingType = PingRenderType.getPingIcon(TEXTURE);
        VertexConsumer vertexBuilder = buffer.getBuffer(pingType);

        float min = -0.25F - (0.25F * (float) ping.getAnimationTimer() / 20F);
        float max = 0.25F + (0.25F * (float) ping.getAnimationTimer() / 20F);

        // Block Overlay Background
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

    private void translatePingCoordinates(double px, double py, double pz, BlockPingWrapper ping) {
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

    public void sendBlockPing(MinecraftClient client, PingType type) {
        Optional<Entity> optional = DebugRenderer.getTargetedEntity(client.cameraEntity, PingClientMod.config().GENERAL.pingAcceptDistance);
        if (client.player == null) return;
        if (optional.isPresent()) {
            sendBlockPing(optional.get().getEntityId(), new Color(PingClientMod.config().VISUAL.pingR, PingClientMod.config().VISUAL.pingG, PingClientMod.config().VISUAL.pingB).getRGB(), type, client.player.getUuidAsString());
        } else {
            BlockHitResult raycastResult = raycast(client.player, PingClientMod.config().GENERAL.pingAcceptDistance);
            if (raycastResult.getType() == HitResult.Type.BLOCK) {
                sendBlockPing(raycastResult, new Color(PingClientMod.config().VISUAL.pingR, PingClientMod.config().VISUAL.pingG, PingClientMod.config().VISUAL.pingB).getRGB(), type, client.player.getUuidAsString());
            }
        }

    }

    private void sendBlockPing(BlockHitResult raytrace, int color, PingType type, String uuid) {
        //todo: check if Block is already highlighted by the same player
        ClientPlayNetworking.send(HighlightBlockPacket.IDENTIFIER, new BlockPingWrapper(raytrace.getBlockPos(), color, type, uuid).getPacketByteBuf());
    }

    private void sendBlockPing(int entityID, int color, PingType type, String uuid) {
        ClientPlayNetworking.send(HighlightBlockPacket.IDENTIFIER, new EntityPingWrapper(entityID, color, type, uuid).getPacketByteBuf());
    }

    private BlockHitResult raycast(PlayerEntity player, double distance) {
        float eyeHeight = player.getStandingEyeHeight();
        return (BlockHitResult) player.raycast(distance, eyeHeight, false);
    }
}
