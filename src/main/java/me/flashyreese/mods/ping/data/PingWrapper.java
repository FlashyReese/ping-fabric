package me.flashyreese.mods.ping.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class PingWrapper {
    private BlockPos blockPos;
    private int entityId = -1;
    private final int color;
    private final PingType type;
    //public final long uuid;
    private boolean isOffscreen = false;
    private float screenX;
    private float screenY;
    private int animationTimer = 20;
    private int timer;
    private final PacketByteBuf packetByteBuf;

    public PingWrapper(BlockPos pos, int color, PingType type) {
        this.blockPos = pos;
        this.color = color;
        this.type = type;
        this.packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        this.writeToBuffer(packetByteBuf, true);
    }

    public PingWrapper(int entityId, int color, PingType type){
        this.entityId = entityId;
        this.color = color;
        this.type = type;
        this.packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        this.writeToBuffer(packetByteBuf, false);
    }

    private void writeToBuffer(ByteBuf buffer, boolean isBlock) {
        buffer.writeBoolean(isBlock);
        if (isBlock){
            buffer.writeInt(this.blockPos.getX());
            buffer.writeInt(this.blockPos.getY());
            buffer.writeInt(this.blockPos.getZ());
        }else{
            buffer.writeInt(this.entityId);
        }
        buffer.writeInt(this.color);
        buffer.writeInt(this.type.ordinal());
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getColor() {
        return color;
    }

    public PingType getType() {
        return type;
    }

    public boolean isOffscreen() {
        return isOffscreen;
    }

    public void setOffscreen(boolean offscreen) {
        isOffscreen = offscreen;
    }

    public float getScreenX() {
        return screenX;
    }

    public void setScreenX(float screenX) {
        this.screenX = screenX;
    }

    public float getScreenY() {
        return screenY;
    }

    public void setScreenY(float screenY) {
        this.screenY = screenY;
    }

    public int getAnimationTimer() {
        return animationTimer;
    }

    public void setAnimationTimer(int animationTimer) {
        this.animationTimer = animationTimer;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public PacketByteBuf getPacketByteBuf() {
        return packetByteBuf;
    }

    public Box getBox() {
        return new Box(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ(), this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ());
    }

    public static PingWrapper of(ByteBuf buffer) {
        boolean isBlock = buffer.readBoolean();
        int x = -1;
        int y = -1;
        int z = -1;
        int entityId = -1;
        if (isBlock){
            x = buffer.readInt();
            y = buffer.readInt();
            z = buffer.readInt();
        }else{
            entityId = buffer.readInt();
        }
        int color = buffer.readInt();
        PingType type = PingType.values()[buffer.readInt()];
        return isBlock ? new PingWrapper(new BlockPos(x, y, z), color, type) : new PingWrapper(entityId, color, type);
    }
}