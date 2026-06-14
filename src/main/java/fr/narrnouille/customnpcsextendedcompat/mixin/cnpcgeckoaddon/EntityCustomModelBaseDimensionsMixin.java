package fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon;

import fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon.EntityCustomModelSizeBridge;
import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcSizeBridge;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.goodbird.cnpcgeckoaddon.entity.EntityCustomModel", remap = false)
public abstract class EntityCustomModelBaseDimensionsMixin implements EntityCustomModelSizeBridge {
    private static final float EPSILON = 1.0E-5F;

    @Shadow
    public int size;

    @Unique
    private float customnpcsExtendedCompat$npcSizeScale = CustomNpcSizeBridge.DEFAULT_SIZE_SCALE;

    @Override
    public void customnpcsExtendedCompat$setNpcSizeScale(float sizeScale) {
        this.customnpcsExtendedCompat$npcSizeScale = CustomNpcSizeBridge.clampSizeScale(sizeScale);
        this.size = CustomNpcSizeBridge.scaleToLegacySize(this.customnpcsExtendedCompat$npcSizeScale);
    }

    @Override
    public float customnpcsExtendedCompat$getNpcSizeScale() {
        return this.customnpcsExtendedCompat$npcSizeScale;
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void customnpcsExtendedCompat$matchCustomNpcBaseDimensions(
            Pose pose,
            CallbackInfoReturnable<EntityDimensions> callbackInfo
    ) {
        EntityDimensions dimensions = callbackInfo.getReturnValue();
        if (dimensions == null) {
            return;
        }

        float scale = this.customnpcsExtendedCompat$npcSizeScale;
        callbackInfo.setReturnValue(isDefaultScale(scale) ? dimensions : dimensions.scale(scale));
    }

    private static boolean isDefaultScale(float scale) {
        return Math.abs(scale - 1.0F) < EPSILON;
    }
}
