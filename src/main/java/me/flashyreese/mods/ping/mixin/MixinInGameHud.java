package me.flashyreese.mods.ping.mixin;

import me.flashyreese.mods.ping.client.PingHandler;
import me.flashyreese.mods.ping.client.gui.PingSelectGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.flashyreese.mods.ping.client.RenderHandler.renderGui;
import static me.flashyreese.mods.ping.client.RenderHandler.renderText;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world != null && !mc.options.hudHidden && !mc.isPaused() && PingSelectGui.active) {
            renderGui();
            renderText(matrices);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SubtitlesHud;render(Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.BEFORE))
    public void preSubtitlesHud(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
        PingHandler.renderPingOffscreen(matrices, tickDelta);
    }
}
