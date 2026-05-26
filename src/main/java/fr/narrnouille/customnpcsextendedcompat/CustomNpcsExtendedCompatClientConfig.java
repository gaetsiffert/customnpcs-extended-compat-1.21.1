package fr.narrnouille.customnpcsextendedcompat;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CustomNpcsExtendedCompatClientConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue SHOW_FACTIONS_INVENTORY_TAB;
    private static final ModConfigSpec.BooleanValue SHOW_QUESTS_INVENTORY_TAB;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("inventory_tabs");
        SHOW_FACTIONS_INVENTORY_TAB = builder
                .comment("Show the CustomNPCs factions tab in player inventory screens.")
                .define("show_factions", true);
        SHOW_QUESTS_INVENTORY_TAB = builder
                .comment("Show the CustomNPCs quests tab in player inventory screens.")
                .define("show_quests", true);
        builder.pop();
        SPEC = builder.build();
    }

    private CustomNpcsExtendedCompatClientConfig() {
    }

    public static boolean showFactionsInventoryTab() {
        return SHOW_FACTIONS_INVENTORY_TAB.get();
    }

    public static boolean showQuestsInventoryTab() {
        return SHOW_QUESTS_INVENTORY_TAB.get();
    }

    public static boolean showVanillaInventoryTab() {
        return showFactionsInventoryTab() || showQuestsInventoryTab();
    }
}
