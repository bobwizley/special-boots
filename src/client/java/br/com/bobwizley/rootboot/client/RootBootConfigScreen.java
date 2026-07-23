package br.com.bobwizley.rootboot.client;

import br.com.bobwizley.rootboot.RootBoot;
import br.com.bobwizley.rootboot.config.RootBootConfig;
import br.com.bobwizley.rootboot.feature.timeoffset.TimeOffsetFeature;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class RootBootConfigScreen {

    private RootBootConfigScreen() {
    }

    public static Screen create(Screen parent) {
        RootBootConfig config = RootBoot.config();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.rootboot.config"))
                .setSavingRunnable(() -> config.save(RootBoot.configPath()));

        ConfigEntryBuilder entries = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("category.rootboot.general"));

        general.addEntry(entries.startTextDescription(
                Component.translatable("text.rootboot.restart_required")).build());

        general.addEntry(entries.startBooleanToggle(
                        Component.translatable("option.rootboot.time_offset"),
                        config.isEnabled(TimeOffsetFeature.ID))
                .setDefaultValue(true)
                .setTooltip(Component.translatable("option.rootboot.time_offset.tooltip"))
                .setSaveConsumer(value -> config.setEnabled(TimeOffsetFeature.ID, value))
                .build());

        return builder.build();
    }
}
