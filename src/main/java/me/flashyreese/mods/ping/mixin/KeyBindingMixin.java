package me.flashyreese.mods.ping.mixin;

import me.flashyreese.mods.ping.util.KeyBindingExtended;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public class KeyBindingMixin implements KeyBindingExtended {
    @Shadow
    private InputUtil.Key boundKey;

    @Override
    public InputUtil.Key getBoundKey() {
        return this.boundKey;
    }
}
