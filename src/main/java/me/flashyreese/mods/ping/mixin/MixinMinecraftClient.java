package me.flashyreese.mods.ping.mixin;

import me.flashyreese.mods.ping.client.KeyHandler;
import me.flashyreese.mods.ping.client.PingHandler;
import me.flashyreese.mods.ping.client.gui.PingSelectGui;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void postTick(CallbackInfo callbackInfo) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if ((mc.world == null || mc.isPaused()) && PingSelectGui.active) {
            PingSelectGui.deactivate();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", shift = At.Shift.BEFORE))
    public void preTick(CallbackInfo callbackInfo) {
        PingHandler.onClientTick();
        KeyHandler.onClientTick();
    }
}
