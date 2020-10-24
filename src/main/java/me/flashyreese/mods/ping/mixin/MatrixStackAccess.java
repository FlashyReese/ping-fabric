package me.flashyreese.mods.ping.mixin;

import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Deque;

@Mixin(MatrixStack.class)
public interface MatrixStackAccess {

    @Accessor("stack")
    Deque<MatrixStack.Entry> getStack();
}
