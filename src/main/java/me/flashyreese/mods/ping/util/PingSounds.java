package me.flashyreese.mods.ping.util;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class PingSounds {
    public static final SoundEvent BLOOP = createSound("bloop");

    private static SoundEvent createSound(String name) {
        Identifier resourceLocation = new Identifier("ping", name);
        return new SoundEvent(resourceLocation);
    }
}