package me.flashyreese.mods.ping.mixin;

import me.flashyreese.mods.ping.client.PingClientMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "hasOutline", at = @At("RETURN"), cancellable = true)
    private void shouldGlow(Entity entity, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (entity != null) {
            callbackInfoReturnable.setReturnValue(callbackInfoReturnable.getReturnValue() || PingClientMod.getClientRegistry().getPingHandler().hasOutline(entity));
        }
    }
}
