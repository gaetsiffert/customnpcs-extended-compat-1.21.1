package fr.narrnouille.customnpcsextendedcompat.compat.customnpcs;

import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataDisplay;

public interface CustomNpcNameTagBridge {
    float DEFAULT_NAME_TAG_Y_OFFSET = 0.0F;
    float MIN_NAME_TAG_Y_OFFSET = -5.0F;
    float MAX_NAME_TAG_Y_OFFSET = 5.0F;
    float DEFAULT_NAME_VISIBILITY_DISTANCE = 22.627417F;
    float DEFAULT_TITLE_VISIBILITY_DISTANCE = 8.0F;
    float MIN_LABEL_VISIBILITY_DISTANCE = 0.0F;
    float MAX_LABEL_VISIBILITY_DISTANCE = 128.0F;
    float DEFAULT_MARK_MOVEMENT_SECONDS = 0.8F;
    float MIN_MARK_MOVEMENT_SECONDS = 0.0F;
    float MAX_MARK_MOVEMENT_SECONDS = 10.0F;
    float VANILLA_NAME_TAG_RENDER_SCALE = 0.025F;
    float MIN_NAME_TAG_SCALE = 0.018F;
    float MAX_NAME_TAG_SCALE = 0.05F;
    float NAME_TEXT_Y_WITH_TITLE = -10.0F;
    float TITLE_TRANSLATE_Y = 4.0F;
    float TITLE_SCALE = 0.6F;
    float NAME_BACKGROUND_TOP_PADDING = 1.0F;
    float NAME_BACKGROUND_BOTTOM_PADDING = 9.0F;
    float COMBO_BOTTOM_GAP_ABOVE_HITBOX = 0.15F;
    float MARK_GAP_ABOVE_NAME_TOP = 2.0F;
    float STANDALONE_MARK_GAP_ABOVE_HITBOX = 0.2F;

    float customnpcsExtendedCompat$getNameTagYOffset();

    void customnpcsExtendedCompat$setNameTagYOffset(float yOffset);

    float customnpcsExtendedCompat$getNameVisibilityDistance();

    void customnpcsExtendedCompat$setNameVisibilityDistance(float distance);

    float customnpcsExtendedCompat$getTitleVisibilityDistance();

    void customnpcsExtendedCompat$setTitleVisibilityDistance(float distance);

    float customnpcsExtendedCompat$getMarkMovementSeconds();

    void customnpcsExtendedCompat$setMarkMovementSeconds(float seconds);

    static float getNameTagYOffset(DataDisplay display) {
        if (display instanceof CustomNpcNameTagBridge bridge) {
            return bridge.customnpcsExtendedCompat$getNameTagYOffset();
        }
        return DEFAULT_NAME_TAG_Y_OFFSET;
    }

    static float clampNameTagYOffset(float yOffset) {
        if (Float.isNaN(yOffset)) {
            return DEFAULT_NAME_TAG_Y_OFFSET;
        }
        return Math.max(MIN_NAME_TAG_Y_OFFSET, Math.min(MAX_NAME_TAG_Y_OFFSET, yOffset));
    }

    static String formatNameTagYOffset(float yOffset) {
        String value = Float.toString(clampNameTagYOffset(yOffset));
        if (value.endsWith(".0")) {
            return value.substring(0, value.length() - 2);
        }
        return value;
    }

    static float getNameVisibilityDistance(DataDisplay display) {
        if (display instanceof CustomNpcNameTagBridge bridge) {
            return bridge.customnpcsExtendedCompat$getNameVisibilityDistance();
        }
        return DEFAULT_NAME_VISIBILITY_DISTANCE;
    }

    static float getTitleVisibilityDistance(DataDisplay display) {
        if (display instanceof CustomNpcNameTagBridge bridge) {
            return bridge.customnpcsExtendedCompat$getTitleVisibilityDistance();
        }
        return DEFAULT_TITLE_VISIBILITY_DISTANCE;
    }

    static float getNameVisibilityDistanceSqr(DataDisplay display) {
        float distance = getNameVisibilityDistance(display);
        return distance * distance;
    }

    static float getMarkMovementSeconds(DataDisplay display) {
        if (display instanceof CustomNpcNameTagBridge bridge) {
            return bridge.customnpcsExtendedCompat$getMarkMovementSeconds();
        }
        return DEFAULT_MARK_MOVEMENT_SECONDS;
    }

    static float clampNameVisibilityDistance(float distance) {
        if (Float.isNaN(distance)) {
            return DEFAULT_NAME_VISIBILITY_DISTANCE;
        }
        return clampLabelVisibilityDistance(distance);
    }

    static float clampTitleVisibilityDistance(float distance) {
        if (Float.isNaN(distance)) {
            return DEFAULT_TITLE_VISIBILITY_DISTANCE;
        }
        return clampLabelVisibilityDistance(distance);
    }

    static float clampLabelVisibilityDistance(float distance) {
        return Math.max(MIN_LABEL_VISIBILITY_DISTANCE, Math.min(MAX_LABEL_VISIBILITY_DISTANCE, distance));
    }

    static String formatLabelVisibilityDistance(float distance) {
        float clamped = clampLabelVisibilityDistance(distance);
        return formatCompactFloat(clamped);
    }

    static float clampMarkMovementSeconds(float seconds) {
        if (Float.isNaN(seconds)) {
            return DEFAULT_MARK_MOVEMENT_SECONDS;
        }
        return Math.max(MIN_MARK_MOVEMENT_SECONDS, Math.min(MAX_MARK_MOVEMENT_SECONDS, seconds));
    }

    static String formatMarkMovementSeconds(float seconds) {
        return formatCompactFloat(clampMarkMovementSeconds(seconds));
    }

    private static String formatCompactFloat(float value) {
        String formatted = String.format(java.util.Locale.ROOT, "%.2f", value);
        while (formatted.contains(".") && (formatted.endsWith("0") || formatted.endsWith("."))) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        return formatted;
    }

    static float getLabelScaleBase(EntityNPCInterface npc) {
        return npc.baseSize.height() / 5.0F * npc.display.getSize();
    }

    static float getFinalNameTagScale(EntityNPCInterface npc) {
        float rawScale = VANILLA_NAME_TAG_RENDER_SCALE * CustomNpcSizeBridge.getSizeScale(npc.display);
        return Math.max(MIN_NAME_TAG_SCALE, Math.min(MAX_NAME_TAG_SCALE, rawScale));
    }

    static float getNameAnchorY(EntityNPCInterface npc) {
        float labelScaleBase = getLabelScaleBase(npc);
        return labelScaleBase / 6.5F * 2.0F;
    }

    static float getComboBottomLocalY(boolean titleVisible) {
        float nameBottomY = (titleVisible ? NAME_TEXT_Y_WITH_TITLE : 0.0F) + NAME_BACKGROUND_BOTTOM_PADDING;
        if (!titleVisible) {
            return nameBottomY;
        }

        float titleBottomY = TITLE_TRANSLATE_Y + NAME_BACKGROUND_BOTTOM_PADDING * TITLE_SCALE;
        return Math.max(nameBottomY, titleBottomY);
    }

    static float getNameTagBaseY(EntityNPCInterface npc, boolean titleVisible) {
        float finalScale = getFinalNameTagScale(npc);
        float comboBottomY = getComboBottomLocalY(titleVisible);
        return npc.getBbHeight()
                + COMBO_BOTTOM_GAP_ABOVE_HITBOX
                + getNameTagYOffset(npc.display)
                - getNameAnchorY(npc)
                + comboBottomY * finalScale;
    }

    static float getNameTopY(EntityNPCInterface npc, boolean titleVisible) {
        float finalScale = getFinalNameTagScale(npc);
        float nameAnchorY = getNameAnchorY(npc);
        float nameTextY = titleVisible ? NAME_TEXT_Y_WITH_TITLE : 0.0F;
        float titleTranslateY = titleVisible ? TITLE_TRANSLATE_Y : 0.0F;
        float topTextY = titleTranslateY + nameTextY - NAME_BACKGROUND_TOP_PADDING;
        return getNameTagBaseY(npc, titleVisible) + nameAnchorY - topTextY * finalScale;
    }

    static float getMarkY(EntityNPCInterface npc, boolean titleVisible) {
        return getNameTopY(npc, titleVisible) + MARK_GAP_ABOVE_NAME_TOP * getFinalNameTagScale(npc);
    }

    static float getStandaloneMarkY(EntityNPCInterface npc) {
        return npc.getBbHeight() + STANDALONE_MARK_GAP_ABOVE_HITBOX + getNameTagYOffset(npc.display);
    }
}
