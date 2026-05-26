package fr.narrnouille.customnpcsscoreboardcompat;

import net.minecraft.resources.ResourceLocation;

public final class CustomMarkTypes {
    public static final int MAX_MARKS_PER_NPC = 64;
    public static final int VANILLA_MARK_TYPES = 7;
    public static final int CUSTOM_MARK_TYPES = 64;
    public static final int FIRST_CUSTOM_MARK_TYPE = VANILLA_MARK_TYPES;
    public static final int TOTAL_MARK_TYPES = VANILLA_MARK_TYPES + CUSTOM_MARK_TYPES;

    private static final String[] DISPLAY_KEYS = buildDisplayKeys();

    private CustomMarkTypes() {
    }

    public static String[] displayKeys() {
        return DISPLAY_KEYS;
    }

    public static boolean isCustomType(int type) {
        return type >= FIRST_CUSTOM_MARK_TYPE && type < TOTAL_MARK_TYPES;
    }

    public static ResourceLocation textureFor(int type) {
        int customIndex = type - FIRST_CUSTOM_MARK_TYPE + 1;
        return ResourceLocation.fromNamespaceAndPath("customnpcs", "textures/marks/custom_mark_" + customIndex + ".png");
    }

    private static String[] buildDisplayKeys() {
        String[] keys = new String[TOTAL_MARK_TYPES];
        keys[0] = "gui.none";
        keys[1] = "mark.question";
        keys[2] = "mark.exclamation";
        keys[3] = "mark.pointer";
        keys[4] = "mark.skull";
        keys[5] = "mark.cross";
        keys[6] = "mark.star";

        for (int i = 1; i <= CUSTOM_MARK_TYPES; i++) {
            keys[FIRST_CUSTOM_MARK_TYPE + i - 1] = "mark.custom_mark_" + i;
        }

        return keys;
    }
}
