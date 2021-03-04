package me.flashyreese.mods.ping.network.packet;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class PingPacket extends PacketByteBuf {
    public PingPacket() {
        super(Unpooled.buffer());
    }

    public abstract Identifier getIdentifier();
}
