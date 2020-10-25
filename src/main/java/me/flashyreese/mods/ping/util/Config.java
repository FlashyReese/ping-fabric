package me.flashyreese.mods.ping.util;

public class Config {
    public static final General GENERAL = new General();
    public static final Visual VISUAL = new Visual();

    public static class General {
        public double pingAcceptDistance = 64;
        public int pingDuration = 125;
        public boolean sound = true;

        /*General(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            pingAcceptDistance = builder
                    .comment("Maximum distance a Ping can be from you and still be received")
                    .translation("ping.configgui.pingAcceptDistance")
                    .defineInRange("pingAcceptDistance", 64.0D, 0.0D, 255.0D);
            pingDuration = builder
                    .comment("How many ticks a Ping should remain active before disappearing")
                    .translation("ping.configgui.pingDuration")
                    .defineInRange("pingDuration", 125, 0, Integer.MAX_VALUE - 1);
            sound = builder
                    .comment("Whether to play a sound when a Ping is received")
                    .translation("ping.configgui.sound")
                    .define("sound", true);
            builder.pop();
        }*/
    }

    public static class Visual {
        public int pingR = 255;
        public int pingG = 0;
        public int pingB = 0;
        public boolean blockOverlay = true;
        public boolean menuBackground = true;

        /*Visual(ForgeConfigSpec.Builder builder) {
            builder.push("visual");
            blockOverlay = builder
                    .comment("Whether to render a colored overlay on the Pinged block")
                    .translation("ping.configgui.blockOverlay")
                    .define("blockOverlay", true);
            menuBackground = builder
                    .comment("Whether to render the Ping Menu background")
                    .translation("ping.configgui.menuBackground")
                    .define("menuBackground", true);
            builder.push("pingColor");
            pingR = builder
                    .translation("ping.configgui.pingRed")
                    .defineInRange("red", 255, 0, 255);
            pingG = builder
                    .translation("ping.configgui.pingGreen")
                    .defineInRange("green", 0, 0, 255);
            pingB = builder
                    .translation("ping.configgui.pingBlue")
                    .defineInRange("blue", 0, 0, 255);
            builder.pop();
        }*/
    }
}
