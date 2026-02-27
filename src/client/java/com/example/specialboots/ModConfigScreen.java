package com.example.specialboots;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModConfigScreen {

    public static Screen create(Screen parent) {
        ModConfig config = ModConfig.getInstance();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.specialboots.config"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(
                Component.translatable("category.specialboots.general"));

        general.addEntry(entryBuilder.startIntSlider(
                        Component.translatable("option.specialboots.radius"),
                        config.getRadius(), 0, 2)
                .setDefaultValue(0)
                .setTooltip(Component.translatable("option.specialboots.radius.tooltip"))
                .setSaveConsumer(config::setRadius)
                .build());

        builder.setSavingRunnable(config::save);

        return builder.build();
    }
}
