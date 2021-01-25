package me.flashyreese.mods.ping.client.data;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum PingType {
    BACKGROUND(null),
    ALERT("ping.key.alert"),
    BREAK("ping.key.break"),
    LOOK("ping.key.look"),
    GOTO("ping.key.goto"),
    ATTACK("ping.key.attack");

    private final float minU;
    private final float minV;
    private final float maxU;
    private final float maxV;
    private final String identifier;

    PingType(String identifier) {
        this.identifier = identifier;
        int spriteSheetSize = 256;
        int spriteSize = 64;
        int x = spriteSize * ordinal();
        int y = (x / spriteSheetSize) * spriteSize;
        this.minU = (float) x / spriteSheetSize;
        this.maxU = (float) (x + spriteSize) / spriteSheetSize;
        this.minV = (float) y / spriteSheetSize;
        this.maxV = (float) (y + spriteSize) / spriteSheetSize;
    }

    public float getMinU() {
        return minU;
    }

    public float getMinV() {
        return minV;
    }

    public float getMaxU() {
        return maxU;
    }

    public float getMaxV() {
        return maxV;
    }

    public Text getTranslatedText(){
        return new TranslatableText(this.identifier);
    }
}