package me.flashyreese.mods.ping.client.gui;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.flashyreese.mods.ping.PingMod;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.text.TranslatableText;

public class PingModMenuEntry implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(new TranslatableText("ping.config.title"));

            builder.setSavingRunnable(() -> {
                PingMod.config().writeChanges();
            });

            ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.ping.general"));
            general.addEntry(builder.entryBuilder().startIntSlider(new TranslatableText("ping.config.ping_accept_distance"), PingMod.config().GENERAL.pingAcceptDistance, 1, 255)
                    .setDefaultValue(64)
                    .setSaveConsumer(val -> PingMod.config().GENERAL.pingAcceptDistance = val)
                    .build());

            general.addEntry(builder.entryBuilder().startIntSlider(new TranslatableText("ping.config.ping_duration"), PingMod.config().GENERAL.pingDuration, 1, 1200)
                    .setDefaultValue(100)
                    .setSaveConsumer(val -> PingMod.config().GENERAL.pingDuration = val)
                    .build());

            general.addEntry(builder.entryBuilder().startBooleanToggle(new TranslatableText("ping.config.ping_sound"), PingMod.config().GENERAL.sound)
                    .setDefaultValue(true)
                    .setSaveConsumer(val -> PingMod.config().GENERAL.sound = val)
                    .build());


            ConfigCategory visual = builder.getOrCreateCategory(new TranslatableText("category.ping.visual"));
            visual.addEntry(builder.entryBuilder().startIntSlider(new TranslatableText("ping.config.ping_red"), PingMod.config().VISUAL.pingR, 0, 255)
                    .setDefaultValue(64)
                    .setSaveConsumer(val -> PingMod.config().VISUAL.pingR = val)
                    .build());

            visual.addEntry(builder.entryBuilder().startIntSlider(new TranslatableText("ping.config.ping_green"), PingMod.config().VISUAL.pingG, 0, 1200)
                    .setDefaultValue(100)
                    .setSaveConsumer(val -> PingMod.config().VISUAL.pingG = val)
                    .build());
            visual.addEntry(builder.entryBuilder().startIntSlider(new TranslatableText("ping.config.ping_blue"), PingMod.config().VISUAL.pingB, 0, 255)
                    .setDefaultValue(64)
                    .setSaveConsumer(val -> PingMod.config().VISUAL.pingB = val)
                    .build());
            visual.addEntry(builder.entryBuilder().startBooleanToggle(new TranslatableText("ping.config.block_overlay"), PingMod.config().VISUAL.blockOverlay)
                    .setDefaultValue(true)
                    .setSaveConsumer(val -> PingMod.config().VISUAL.blockOverlay = val)
                    .build());

            return builder.build();
        };
    }
}
