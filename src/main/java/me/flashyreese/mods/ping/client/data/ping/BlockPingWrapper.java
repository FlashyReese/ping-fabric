package me.flashyreese.mods.ping.client.data.ping;

import me.flashyreese.mods.ping.client.data.PingType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class BlockPingWrapper extends PingWrapper{
    private final BlockPos blockPos;
    private boolean isOffscreen = false;
    private float screenX;
    private float screenY;

    public BlockPingWrapper(BlockPos blockPos, int color, PingType type, String senderUUID) {
        super(color, type, senderUUID);
        this.blockPos = blockPos;
        this.writeToBuffer();
    }

    public BlockPos getBlockPos() {
        return blockPos;
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

    public Box getBox() {
        return new Box(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ(), this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ());
    }

    public void writeToBuffer(){
        this.getPacketByteBuf().writeInt(this.blockPos.getX());
        this.getPacketByteBuf().writeInt(this.blockPos.getY());
        this.getPacketByteBuf().writeInt(this.blockPos.getZ());
        this.getPacketByteBuf().writeInt(this.getColor());
        this.getPacketByteBuf().writeInt(this.getType().ordinal());
        this.getPacketByteBuf().writeString(this.getSenderUUID());
    }

    public static BlockPingWrapper of(PacketByteBuf buffer){
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        int color = buffer.readInt();
        PingType type = PingType.values()[buffer.readInt()];
        String uuid = buffer.readString(36);
        return new BlockPingWrapper(new BlockPos(x, y, z), color, type, uuid);
    }
}
