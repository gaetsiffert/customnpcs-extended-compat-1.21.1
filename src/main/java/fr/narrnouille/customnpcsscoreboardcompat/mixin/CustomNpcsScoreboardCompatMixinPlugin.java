package fr.narrnouille.customnpcsscoreboardcompat.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class CustomNpcsScoreboardCompatMixinPlugin implements IMixinConfigPlugin {
    private static final String PACKET_SYNC_ANIMATION = "com.goodbird.cnpcgeckoaddon.network.PacketSyncAnimation";
    private static final String PACKET_SYNC_TILE_ANIMATION = "com.goodbird.cnpcgeckoaddon.network.PacketSyncTileAnimation";
    private static final String RENDER_CUSTOM_MODEL = "com.goodbird.cnpcgeckoaddon.client.renderer.RenderCustomModel";
    private static final String GECKOLIB_RENDER_LAYER = "software.bernie.geckolib.renderer.layer.GeoRenderLayer";

    private static final String PACKET_SYNC_ANIMATION_MIXIN =
            "fr.narrnouille.customnpcsscoreboardcompat.mixin.cnpcgeckoaddon.PacketSyncAnimationMixin";
    private static final String PACKET_SYNC_TILE_ANIMATION_MIXIN =
            "fr.narrnouille.customnpcsscoreboardcompat.mixin.cnpcgeckoaddon.PacketSyncTileAnimationMixin";
    private static final String RENDER_CUSTOM_MODEL_ARMOR_LAYER_MIXIN =
            "fr.narrnouille.customnpcsscoreboardcompat.mixin.cnpcgeckoaddon.RenderCustomModelArmorLayerMixin";

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
            case PACKET_SYNC_ANIMATION_MIXIN -> isClassAvailable(PACKET_SYNC_ANIMATION);
            case PACKET_SYNC_TILE_ANIMATION_MIXIN -> isClassAvailable(PACKET_SYNC_TILE_ANIMATION);
            case RENDER_CUSTOM_MODEL_ARMOR_LAYER_MIXIN ->
                    isClassAvailable(RENDER_CUSTOM_MODEL) && isClassAvailable(GECKOLIB_RENDER_LAYER);
            default -> true;
        };
    }

    private static boolean isClassAvailable(String className) {
        return CLASS_AVAILABILITY.computeIfAbsent(className, CustomNpcsScoreboardCompatMixinPlugin::canReadClass);
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
