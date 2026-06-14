package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon.EntityCustomModelSizeBridge;
import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcSizeBridge;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityCustomNpc.class, remap = false)
public abstract class EntityCustomNpcGeckoDimensionsMixin {
    private static final String GECKO_MODEL_CLASS = "com.goodbird.cnpcgeckoaddon.entity.EntityCustomModel";
    private static final float MIN_DIMENSION = 0.1F;
    private static final float HIDDEN_HITBOX_WIDTH = 1.0E-5F;

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void customnpcsExtendedCompat$useScaledGeckoDimensions(
            Pose pose,
            CallbackInfoReturnable<EntityDimensions> callbackInfo
    ) {
        EntityCustomNpc npc = (EntityCustomNpc) (Object) this;
        if (npc.modelData == null) {
            return;
        }

        LivingEntity modelEntity = npc.modelData.getEntity(npc);
        if (modelEntity == null || !GECKO_MODEL_CLASS.equals(modelEntity.getClass().getName())) {
            return;
        }

        if (modelEntity instanceof EntityCustomModelSizeBridge sizeBridge) {
            sizeBridge.customnpcsExtendedCompat$setNpcSizeScale(CustomNpcSizeBridge.getSizeScale(npc.display));
        }

        EntityDimensions modelDimensions = modelEntity.getDimensions(pose);
        float width = Math.max(modelDimensions.width(), MIN_DIMENSION);
        float height = Math.max(modelDimensions.height(), MIN_DIMENSION);

        if (npc.display.getHitboxState() == 1 || npc.isKilled() && npc.stats.hideKilledBody) {
            width = HIDDEN_HITBOX_WIDTH;
        }

        callbackInfo.setReturnValue(EntityDimensions.scalable(width, height));
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void customnpcsExtendedCompat$useFloatSizeForExternalModelDimensions(
            Pose pose,
            CallbackInfoReturnable<EntityDimensions> callbackInfo
    ) {
        EntityCustomNpc npc = (EntityCustomNpc) (Object) this;
        if (npc.modelData == null) {
            return;
        }

        LivingEntity modelEntity = npc.modelData.getEntity(npc);
        if (modelEntity == null || GECKO_MODEL_CLASS.equals(modelEntity.getClass().getName())) {
            return;
        }

        EntityDimensions dimensions = callbackInfo.getReturnValue();
        if (dimensions == null) {
            return;
        }

        float floatScale = CustomNpcSizeBridge.getSizeScale(npc.display);
        float legacyScale = CustomNpcSizeBridge.legacySizeToScale(npc.display.getSize());
        if (CustomNpcSizeBridge.isSameSizeScale(floatScale, legacyScale)) {
            return;
        }

        float ratio = legacyScale <= 0.0F ? 1.0F : floatScale / legacyScale;
        float width = dimensions.width() <= HIDDEN_HITBOX_WIDTH * 10.0F
                ? HIDDEN_HITBOX_WIDTH
                : dimensions.width() * ratio;
        callbackInfo.setReturnValue(EntityDimensions.scalable(width, dimensions.height() * ratio));
    }
}
