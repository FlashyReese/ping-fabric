package me.flashyreese.mods.ping.client.util;

import net.minecraft.client.MinecraftClient;

public class AngleHelper {

    public static double getMouseAngle() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return getRelativeAngle(mc.getWindow().getWidth() * 0.5D, mc.getWindow().getHeight() * 0.5D, mc.mouse.getX(), mc.mouse.getY());
    }

    private static double getRelativeAngle(double originX, double originY, double x, double y) {
        return correctAngle(Math.toDegrees(Math.atan2(y - originY, x - originX)) + 90);
    }

    public static double correctAngle(double angle) {
        if (angle < 0) {
            angle += 360;
        } else if (angle > 360) {
            angle -= 360;
        }
        return angle;
    }
}