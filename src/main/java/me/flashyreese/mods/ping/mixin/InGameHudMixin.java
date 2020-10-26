package me.flashyreese.mods.ping.mixin;

import me.flashyreese.mods.ping.PingMod;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SubtitlesHud;render(Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.BEFORE))
    public void preSubtitlesHud(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
        PingMod.getPingHandler().renderPingOffscreen(matrices, tickDelta);
    }
}
