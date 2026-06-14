package fr.narrnouille.customnpcsextendedcompat.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class CustomNpcsExtendedCompatMixinPlugin implements IMixinConfigPlugin {
    private static final String PACKET_SYNC_ANIMATION = "com.goodbird.cnpcgeckoaddon.network.PacketSyncAnimation";
    private static final String PACKET_SYNC_TILE_ANIMATION = "com.goodbird.cnpcgeckoaddon.network.PacketSyncTileAnimation";
    private static final String CUSTOM_MODEL_DATA = "com.goodbird.cnpcgeckoaddon.data.CustomModelData";
    private static final String ENTITY_CUSTOM_MODEL = "com.goodbird.cnpcgeckoaddon.entity.EntityCustomModel";
    private static final String MODEL_CUSTOM = "com.goodbird.cnpcgeckoaddon.client.model.ModelCustom";
    private static final String RENDER_CUSTOM_MODEL = "com.goodbird.cnpcgeckoaddon.client.renderer.RenderCustomModel";
    private static final String GECKOLIB_RENDER_LAYER = "software.bernie.geckolib.renderer.layer.GeoRenderLayer";

    private static final String CUSTOM_MODEL_DATA_DEFAULT_DIMENSIONS_MIXIN =
            "fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon.CustomModelDataDefaultDimensionsMixin";
    private static final String ENTITY_CUSTOM_MODEL_BASE_DIMENSIONS_MIXIN =
            "fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon.EntityCustomModelBaseDimensionsMixin";
    private static final String ENTITY_CUSTOM_MODEL_OVERLAY_MIXIN =
            "fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon.EntityCustomModelOverlayMixin";
    private static final String ENTITY_CUSTOM_MODEL_MANUAL_ANIMATION_TRANSITION_MIXIN =
            "fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon.EntityCustomModelManualAnimationTransitionMixin";
    private static final String ENTITY_CUSTOM_NPC_GECKO_DIMENSIONS_MIXIN =
            "fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs.EntityCustomNpcGeckoDimensionsMixin";
    private static final String PACKET_SYNC_ANIMATION_MIXIN =
            "fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon.PacketSyncAnimationMixin";
    private static final String PACKET_SYNC_TILE_ANIMATION_MIXIN =
            "fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon.PacketSyncTileAnimationMixin";
    private static final String MODEL_CUSTOM_DIALOG_PREVIEW_MIXIN =
            "fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon.ModelCustomDialogPreviewMixin";
    private static final String RENDER_CUSTOM_MODEL_ARMOR_LAYER_MIXIN =
            "fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon.RenderCustomModelArmorLayerMixin";
    private static final String RENDER_CUSTOM_MODEL_HELD_ITEM_MIXIN =
            "fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon.RenderCustomModelHeldItemMixin";
    private static final String RENDER_CUSTOM_MODEL_DIALOG_PREVIEW_MIXIN =
            "fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon.RenderCustomModelDialogPreviewMixin";
    private static final String RENDER_CUSTOM_MODEL_SCALE_MIXIN =
            "fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon.RenderCustomModelScaleMixin";

    private static final Map<String, Boolean> CLASS_AVAILABILITY = new ConcurrentHashMap<>();

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return switch (mixinClassName) {
            case CUSTOM_MODEL_DATA_DEFAULT_DIMENSIONS_MIXIN -> isClassAvailable(CUSTOM_MODEL_DATA);
            case ENTITY_CUSTOM_MODEL_BASE_DIMENSIONS_MIXIN -> isClassAvailable(ENTITY_CUSTOM_MODEL);
            case ENTITY_CUSTOM_MODEL_OVERLAY_MIXIN -> isClassAvailable(ENTITY_CUSTOM_MODEL);
            case ENTITY_CUSTOM_MODEL_MANUAL_ANIMATION_TRANSITION_MIXIN -> isClassAvailable(ENTITY_CUSTOM_MODEL);
            case ENTITY_CUSTOM_NPC_GECKO_DIMENSIONS_MIXIN -> isClassAvailable(ENTITY_CUSTOM_MODEL);
            case PACKET_SYNC_ANIMATION_MIXIN -> isClassAvailable(PACKET_SYNC_ANIMATION);
            case PACKET_SYNC_TILE_ANIMATION_MIXIN -> isClassAvailable(PACKET_SYNC_TILE_ANIMATION);
            case MODEL_CUSTOM_DIALOG_PREVIEW_MIXIN -> isClassAvailable(MODEL_CUSTOM);
            case RENDER_CUSTOM_MODEL_ARMOR_LAYER_MIXIN ->
                    isClassAvailable(RENDER_CUSTOM_MODEL) && isClassAvailable(GECKOLIB_RENDER_LAYER);
            case RENDER_CUSTOM_MODEL_HELD_ITEM_MIXIN -> isClassAvailable(RENDER_CUSTOM_MODEL);
            case RENDER_CUSTOM_MODEL_DIALOG_PREVIEW_MIXIN -> isClassAvailable(RENDER_CUSTOM_MODEL);
            case RENDER_CUSTOM_MODEL_SCALE_MIXIN -> isClassAvailable(RENDER_CUSTOM_MODEL);
            default -> true;
        };
    }

    private static boolean isClassAvailable(String className) {
        return CLASS_AVAILABILITY.computeIfAbsent(className, CustomNpcsExtendedCompatMixinPlugin::canReadClass);
    }

    private static boolean canReadClass(String className) {
        try {
            MixinService.getService().getBytecodeProvider().getClassNode(className);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
