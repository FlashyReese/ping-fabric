package me.flashyreese.mods.ping.util;

import com.google.common.collect.Lists;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.List;

public class PingSounds {
    private static final List<SoundEvent> SOUNDS = Lists.newArrayList();
    public static final SoundEvent BLOOP = createSound("bloop");

    private static SoundEvent createSound(String name) {
        Identifier resourceLocation = new Identifier("ping", name);
        SoundEvent sound = new SoundEvent(resourceLocation);
        SOUNDS.add(sound);
        return sound;
    }
}