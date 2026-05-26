package fr.narrnouille.customnpcsextendedcompat;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(CustomNpcsExtendedCompatMod.MODID)
public final class CustomNpcsExtendedCompatMod {
    public static final String MODID = "customnpcs_extended_compat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CustomNpcsExtendedCompatMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, CustomNpcsExtendedCompatClientConfig.SPEC);
        modEventBus.addListener(CnpcGeckoPayloadRegistration::register);
        LOGGER.debug("Loaded mod {}", MODID);
    }
}
