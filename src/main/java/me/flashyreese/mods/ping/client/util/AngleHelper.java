package me.flashyreese.mods.ping.client.util;

import net.minecraft.client.MinecraftClient;

public class AngleHelper {

    public static double getMouseAngle() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return getRelativeAngle(mc.getWindow().getWidth() * 0.5D, mc.getWindow().getHeight() * 0.5D, mc.mouse.getX(), mc.mouse.getY());
    }

    private static double getRelativeAngle(double originX, double originY, double x, double y) {
        double angle = Math.toDegrees(Math.atan2(x - originX, y - originY));

        // Remove 90 from the angle to make 0 and 180 at the top and bottom of the screen
        angle -= 180;

        return correctAngle(angle);
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