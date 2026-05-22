package fr.narrnouille.customnpcsscoreboardcompat;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CustomNpcsScoreboardCompatMod.MODID)
public final class CustomNpcsScoreboardCompatMod {
    public static final String MODID = "customnpcs_scoreboard_compat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CustomNpcsScoreboardCompatMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(CnpcGeckoPayloadRegistration::register);
        LOGGER.debug("Loaded mod {}", MODID);
    }
}
