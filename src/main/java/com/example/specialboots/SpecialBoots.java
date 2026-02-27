package com.example.specialboots;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpecialBoots implements ModInitializer {

    public static final String MOD_ID = "specialboots";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEnchantmentEffects.register();
    }
}
