package me.flashyreese.mods.ping.client.data.ping;

import io.netty.buffer.Unpooled;
import me.flashyreese.mods.ping.client.data.PingType;
import net.minecraft.network.PacketByteBuf;

public abstract class PingWrapper {
    private final int color;
    private final PingType type;
    private final String senderUUID;
    private final PacketByteBuf packetByteBuf;

    private int animationTimer = 20;
    private int timer;

    public PingWrapper(int color, PingType type, String senderUUID) {
        this.color = color;
        this.type = type;
        this.senderUUID = senderUUID;
        this.packetByteBuf = new PacketByteBuf(Unpooled.buffer());
    }

    public int getColor() {
        return color;
    }

    public PingType getType() {
        return type;
    }

    public String getSenderUUID() {
        return senderUUID;
    }

    public PacketByteBuf getPacketByteBuf() {
        return packetByteBuf;
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
}
