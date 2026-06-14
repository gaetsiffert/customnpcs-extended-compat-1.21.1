package fr.narrnouille.customnpcsextendedcompat.compat.customnpcs;

import noppes.npcs.entity.data.DataDisplay;

public interface CustomNpcSizeBridge {
    float DEFAULT_SIZE_SCALE = 1.0F;
    float MIN_SIZE_SCALE = 0.2F;
    float MAX_SIZE_SCALE = 6.0F;
    float LEGACY_SIZE_STEP = 0.2F;
    float EPSILON = 1.0E-5F;

    float customnpcsExtendedCompat$getSizeScale();

    void customnpcsExtendedCompat$setSizeScale(float sizeScale);

    static float getSizeScale(DataDisplay display) {
        if (display instanceof CustomNpcSizeBridge bridge) {
            return bridge.customnpcsExtendedCompat$getSizeScale();
        }
        return legacySizeToScale(display.getSize());
    }

    static float legacySizeToScale(int size) {
        return clampSizeScale(size * LEGACY_SIZE_STEP);
    }

    static int scaleToLegacySize(float sizeScale) {
        return Math.max(1, Math.min(30, Math.round(clampSizeScale(sizeScale) / LEGACY_SIZE_STEP)));
    }

    static float clampSizeScale(float sizeScale) {
        if (Float.isNaN(sizeScale)) {
            return DEFAULT_SIZE_SCALE;
        }
        return Math.max(MIN_SIZE_SCALE, Math.min(MAX_SIZE_SCALE, sizeScale));
    }

    static boolean isSameSizeScale(float first, float second) {
        return Math.abs(first - second) < EPSILON;
    }

    static String formatSizeScale(float sizeScale) {
        String value = Float.toString(clampSizeScale(sizeScale));
        if (value.endsWith(".0")) {
            return value.substring(0, value.length() - 2);
        }
        return value;
    }
}
