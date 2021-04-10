package me.flashyreese.mods.ping.client.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.flashyreese.mods.ping.client.PingClientMod;
import me.flashyreese.mods.ping.client.config.PingClientConfig;
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

            builder.setSavingRunnable(() -> PingClientMod.config().writeChanges());

            ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.ping.general"));
            general.addEntry(builder.entryBuilder().startEnumSelector(new TranslatableText("ping.config.ping_menu_mode"), PingClientConfig.General.PingMenuMode.class, PingClientMod.config().GENERAL.pingMenuMode)
                    .setDefaultValue(PingClientConfig.General.PingMenuMode.HOLD)
                    .setSaveConsumer(val -> PingClientMod.config().GENERAL.pingMenuMode = val)
                    .build());
            general.addEntry(builder.entryBuilder().startIntSlider(new TranslatableText("ping.config.ping_accept_distance"), PingClientMod.config().GENERAL.pingAcceptDistance, 1, 255)
                    .setDefaultValue(64)
                    .setSaveConsumer(val -> PingClientMod.config().GENERAL.pingAcceptDistance = val)
                    .build());

            general.addEntry(builder.entryBuilder().startIntSlider(new TranslatableText("ping.config.ping_duration"), PingClientMod.config().GENERAL.pingDuration, 1, 1200)
                    .setDefaultValue(100)
                    .setSaveConsumer(val -> PingClientMod.config().GENERAL.pingDuration = val)
                    .build());

            general.addEntry(builder.entryBuilder().startBooleanToggle(new TranslatableText("ping.config.ping_sound"), PingClientMod.config().GENERAL.sound)
                    .setDefaultValue(true)
                    .setSaveConsumer(val -> PingClientMod.config().GENERAL.sound = val)
                    .build());


            ConfigCategory visual = builder.getOrCreateCategory(new TranslatableText("category.ping.visual"));
            visual.addEntry(builder.entryBuilder().startIntSlider(new TranslatableText("ping.config.ping_red"), PingClientMod.config().VISUAL.pingR, 0, 255)
                    .setDefaultValue(64)
                    .setSaveConsumer(val -> PingClientMod.config().VISUAL.pingR = val)
                    .build());

            visual.addEntry(builder.entryBuilder().startIntSlider(new TranslatableText("ping.config.ping_green"), PingClientMod.config().VISUAL.pingG, 0, 255)
                    .setDefaultValue(100)
                    .setSaveConsumer(val -> PingClientMod.config().VISUAL.pingG = val)
                    .build());
            visual.addEntry(builder.entryBuilder().startIntSlider(new TranslatableText("ping.config.ping_blue"), PingClientMod.config().VISUAL.pingB, 0, 255)
                    .setDefaultValue(64)
                    .setSaveConsumer(val -> PingClientMod.config().VISUAL.pingB = val)
                    .build());
            visual.addEntry(builder.entryBuilder().startBooleanToggle(new TranslatableText("ping.config.block_overlay"), PingClientMod.config().VISUAL.blockOverlay)
                    .setDefaultValue(true)
                    .setSaveConsumer(val -> PingClientMod.config().VISUAL.blockOverlay = val)
                    .build());

            return builder.build();
        };
    }
}
